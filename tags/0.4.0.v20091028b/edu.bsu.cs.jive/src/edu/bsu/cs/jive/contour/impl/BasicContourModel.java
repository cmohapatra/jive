package edu.bsu.cs.jive.contour.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.StringSeparator;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Implementation of a contour model that supports addition and removal
 * of contours.
 * 
 * @author pvg
 */
public class BasicContourModel extends AbstractContourModel {

	private final ReentrantLock lock = new ReentrantLock() {
		/** Generated serial version uid */
		private static final long serialVersionUID = 544994675292416955L;

		@Override
		public void lock() {
			// Call super implementation and then log the acquisition.
			super.lock();
			
      // Commented out until synchro bugs are found and need to be tracked
      //if (log.isDebugEnabled())
			//	log.debug(String.format("Lock acquired by %s; hold count is %d.",
      //      Thread.currentThread(),
      //      lock.getHoldCount()));
		}

    @Override
    public void unlock() {
      // Call super implementation and then log the release
      super.unlock();

      //if (log.isDebugEnabled())
      //  log.debug(String.format("Lock released by %s; hold count is %d.",
      //      Thread.currentThread(),
      //      lock.getHoldCount()));
    }
	};
	
  private class CMIterator implements Iterator<Contour> {
    /** Index into the {@link #list} of the last requested value. */
    private int index = -1;
    
    private final List<Contour> list;
    
    /** 
     * Keeps track of the number of modifications that existed at the time
     * this iterator was created.
     */
    private final long modsAtCreation;

    /**
     * Create a new iterator
     */
    public CMIterator() {
      modsAtCreation = countModifications();
      list = new ArrayList<Contour>(size());
      
      // Dump all the contents into a list; this will work since we
      // count modifications.
      visitDepthFirst(new Visitor() {
        public void visit(Contour contour) {
          list.add(contour);
        }
      });
    }
    
    public boolean hasNext() {
      return index < list.size();
    }

    public Contour next() {
      if (countModifications()!=modsAtCreation)
        throw new IllegalStateException("Contour model changed during iteration!");
      
      else {
        index++;
        return list.get(index);
      }
    }

    public void remove() {
      throw new UnsupportedOperationException("Remove not supported by this iterator.");
    }
    
    @Override
    public String toString() {
      return String.format("%s(modsAtCreation=%d,index=%d)", 
          getClass().getName(), modsAtCreation, index);
    }
  }
  
  /** An unmodifiable empty list (natch). */
  private static final List<Contour> UNMODIFIABLE_EMPTY_LIST
    = Collections.unmodifiableList(new LinkedList<Contour>());

  /** Maps child contours to their parents */
  private Map<Contour,Contour> childToParentMap = new HashMap<Contour,Contour>();
  
  /** Maps contour IDs to their contours */
  // TODO Plug-in change
//  private Map<ContourID, Contour> idMap = new TreeMap<ContourID,Contour>();
  private Map<ContourID, Contour> idMap = new HashMap<ContourID, Contour>();
  
  // TODO Plug-in change
  /** List of root contours */
  private List<Contour> rootList = new LinkedList<Contour>();
  
  /**
   * Counts the number of modifications to this contour model.
   * This is used by iterators to detect illegal states.
   */
  private long modifications = 0;
  
  /** 
   * Maps parents to the list of their children, in order.
   * Contours that are in the model but have no children will be in this
   * map and point to null.  Hence, all of the contours in this model can
   * be listed by getting the keyset of this map.
   */
  private Map<Contour,List<Contour>> parentToChildrenMap = 
    new HashMap<Contour,List<Contour>>();
  
  /**
   * Add a contour to this contour model.
   * @param contour the contour to add
   * @param parent the parent of the contour, which must be null (for root
   *   contours) or in this model.
   */
  protected void add(Contour contour, Contour parent) {
  	// protected methods should only be called internally, so this is an assert
  	assert checkLock();
  	
    if (parent!=null && !contains(parent))
      throw new IllegalArgumentException(
          "Parent must be null or in this model.  Provided parent is " 
          + parent + "; contour being added is " + contour);
    
    // Update id map
    idMap.put(contour.id(), contour);
    
    // Update parent map for this contour's parent
    if (parent!=null) {
      List<Contour> children = parentToChildrenMap.get(parent);
      if (children!=null) children.add(contour);
      else {
        children = new ArrayList<Contour>(1);
        children.add(contour);
        parentToChildrenMap.put(parent,children);
      }
    }
    // TODO Plug-in change
    else {
    	rootList.add(contour);
    }
    
    // Update parent map for this contour
    assert !parentToChildrenMap.containsKey(contour);
    // TODO Plug-in change
    parentToChildrenMap.put(contour, new ArrayList<Contour>(1));
    
    // Update childToParentMap
    if (parent!=null)
      childToParentMap.put(contour, parent);
    
    // Record the fact that a modification has been made; req'd for iterators
    modifications++;
    
    // Fire notification to listeners
    // TODO Plug-in change.
    fireContourAdded(contour, parent);
  }
  
  //TODO: add lock checks (as above)
  
  public boolean contains(Contour contour) {
  	checkLock();
    return parentToChildrenMap.containsKey(contour);
  }
  
  // TODO Plug-in change
  public boolean contains(ContourID id) {
  	checkLock();
  	return idMap.containsKey(id);
  }
  
  /** 
   * Check that the lock is held by the current thread or the model is
   * not locked.
   * If the lock is not owned by this thread and the system is locked,
   * throw an exception.
   * Most clients do not need to use the return value; it is provided for
   * ease of use with assertions.
   * @return true as long as an exception was not thrown
   * @throws IllegalMonitorStateException
   */
  private final boolean checkLock() {
  	// Even though these queries are not synchronized, they should work.
  	// The lock must be previously obtained by this thread in order
  	// for the condition to pass.  Even if another thread grabs the lock
  	// in the middle of this expression's evaluation, then the first part
  	// of the conjunction will be false.
  	if (!lock.isHeldByCurrentThread() && lock.isLocked())
  		throw new IllegalMonitorStateException("Lock is not held by current thread");
  	return true;
  }

  private long countModifications() {
  	assert checkLock();
    return modifications;
  }
  
  public final List<Contour> getChildren(Contour parent) {
  	checkLock();
    return getChildren(parent, null);
  }

  public List<Contour> getChildren(Contour parent, List<Contour> result) {
  	checkLock();
  	
    List<Contour> internalList = parentToChildrenMap.get(parent);
    
    // Check if the parent has no children 
    if (internalList==null) return UNMODIFIABLE_EMPTY_LIST;
    
    // Make sure we have a result list and that it's empty.
    if (result==null) result = new ArrayList<Contour>(internalList.size());
    else result.clear();
    
    assert result.size()==0;
    
    // Fill the result list and return it.
    result.addAll(internalList);
    return result;
  }

  public final List<ContourID> getChildren(ContourID parentID) {
    return getChildren(parentID, null);
  }

  public final List<ContourID> 
  getChildren(ContourID parentID, List<ContourID> result) {
  	checkLock();
  
    List<Contour> contourList = getChildren(getContour(parentID));
    
    // Make sure we have a result list and that it's empty
    if (result==null) result = new ArrayList<ContourID>(contourList.size());
    else result.clear();
    assert result.size()==0;
    
    for (Contour contour : contourList)
      result.add(contour.id());
    
    return result;
  }

  public Contour getContour(ContourID id) {
  	checkLock();
  	
    Contour contour = idMap.get(id);
    if (contour!=null) return contour;
    else 
      throw new IllegalArgumentException("Contour " + id 
          + " is not in this model.");
  }
  
  public Contour getParent(Contour contour) {
  	checkLock();
  	
    Contour parent = childToParentMap.get(contour);
    if (parent == null) {
      if (parentToChildrenMap.containsKey(contour))
        // it's a root contour
        return null;
      else
        throw new IllegalArgumentException("This contour is not in the model: "
            + contour);
    } else
      return parent;
  }
  
  public final ContourID getParent(ContourID id) {
  	// No checklock here: delegate to getParent(Contour).
    return getParent(getContour(id)).id();
  }
  
  // TODO Plug-in change.
  public synchronized List<Contour> getRoots() {
  	checkLock();
  	
    return getRoots(null);
  }
  
  // TODO Plug-in change.
  public synchronized List<Contour> getRoots(List<Contour> result) {
  	checkLock();
  	
  	// Make sure we have a result list and that it's empty.
  	// TODO Plug-in change
  	if (result == null) result = new ArrayList<Contour>(rootList.size());
  	else result.clear();
  	
  	// TODO Plug-in change
//  	// Iterate through all contours. (This could possibly be optimized.)
//    for (Contour contour : idMap.values()) 
//      if (isRoot(contour)) result.add(contour);
  	result.addAll(rootList);
  	
  	return result;
  }
  
  /**
   * Test if a contour is a root or not.
   * @param contour
   * @return true iff the contour is a root
   */
  public boolean isRoot(Contour contour) {
  	checkLock();
  	
    // a contour is a root if it has no parent.
    return childToParentMap.get(contour) == null;
  }
  
  public Iterator<Contour> iterator() {
  	checkLock();
  	
    // Note that this must be synchronized since the creation of the
    // CM iterator will need to access the current (not modified) state
    // of this data model.
    return new CMIterator();
  }
  
  
  /**
   * Remove a contour from this contour model.
   * The contour must have no children to be removed.
   * @param contour the contour to be removed
   * @return the contour that was removed
   * @throws IllegalArgumentException if the contour has children in the model
   */
  protected Contour remove(Contour contour) {
  	checkLock();
  	
    if (contour==null) throw new NullPointerException("Contour may not be null");
    
    List<Contour> children = getChildren(contour);
    if (children.size()!=0) 
      throw new IllegalArgumentException("Contour has " 
          + children.size() 
          + " children in the model and cannot be remove."
          + "  Contour is " + contour + "; children are "
          + StringSeparator.toString(children));
    
    // Sanity check
    assert idMap.containsValue(contour);
    // reference equality; it must be *the same* object
    assert idMap.get(contour.id()) == contour;
    assert parentToChildrenMap.containsKey(contour);
    
    // At this point, the contour can be removed.
    // Start by removing it from its parents list of children, if appropriate
    Contour parent = getParent(contour);
    if (parent!=null) {
      assert parentToChildrenMap.get(parent).contains(contour);
      parentToChildrenMap.get(parent).remove(contour);
    }
    // TODO Plug-in change
    else {
    	rootList.remove(contour);
    }
    // Then remove this contour is a key where appropriate.
    idMap.remove(contour.id());
    parentToChildrenMap.remove(contour);
    childToParentMap.remove(contour);
    
    // sanity check
    assert !childToParentMap.containsValue(contour);
    
    // Update modifications
    modifications++;
    
    // Fire notification
    fireContourRemoved(contour, parent);
    
    return contour;
  }
  
  public int size() {
  	checkLock();
  	
    // The size is equal to the number of contour id mappings
    return idMap.size();
  }
  
  /**
   * Change the value of a variable.
   * 
   * @param contour
   *          the containing contour
   * @param id
   *          the variable within the contour
   * @param value
   *          the new value for the variable
   * @throws IllegalArgumentException
   *           if the given contour is not in the model or if the variable is
   *           not in the contour
   */
  protected void setValue(Contour contour, VariableID id, Value value) {
  	assert checkLock();
  	
    assert contour != null;
    assert id != null;
    assert value != null; // I THINK this is prohibited; testing might be
                          // required.

    if (!contains(contour))
      throw new IllegalArgumentException("Contour not in model: " + contour);

    ContourMember.Variable v = VariableFinder.find(id, contour);
    Value oldValue; // This is needed for event notification.
    try {
      MutableContourMember mcm = (MutableContourMember) v;
      oldValue = mcm.value();
      mcm.setValue(value);
    } catch (ClassCastException cce) {
      throw new IllegalStateException(
          "I cannot change the value of this kind of member! " + v);
    }
    
    // Fire notification to listeners
    fireValueChanged(contour, id, value, oldValue);
  }

	public int countChildren(Contour parent) {
		checkLock();
		return parentToChildrenMap.get(parent).size();
	}

	public int countChildren(ContourID parent) {
		// No checkLock here; delgate to countChildren(Contour)
		return countChildren(getContour(parent));
	}
	
	public ReentrantLock getModelLock() {
		return lock;
	}
  
}
