package edu.bsu.cs.jive.contour.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.HashUtils;
import edu.bsu.cs.jive.util.Publisher;
import edu.bsu.cs.jive.util.StringSeparator;
import edu.bsu.cs.jive.util.VariableID;

/**
 * A contour model that supports changes that are logged as transactions.
 * These transactions can be rolled back or re-committed like a database's.
 * 
 * @author pvg
 */
public abstract class AbstractInteractiveContourModel implements InteractiveContourModel {
  // Implementation note:
	// This class delegates much of its behavior to a BasicContourModel
	// object.  The decision was made to use delegation rather than inheritance.
	//
	// In terms of synchronization:  when playing back a transaction, a lock
	// must be obtained for the whole operation, not just for each add or remove.
	// This class AND view class(es) must be able to acquire the same lock,
	// and hence it must be made publicly accessible or extrinsic. 
	
  /**
   * Marks a class that listens to interactive contour models for
   * transaction recordings.
   * 
   * @author pvg
   */
  public interface Listener {
    /**
     * Called when a transaction has been recorded by this contour model.
     * Note that the transaction itself is internal to the contour model
     * and is not exposed.
     * @param cm the cm that has recorded a transaction.
     */
    public void transactionRecorded(AbstractInteractiveContourModel cm);
  }
  
  private final Publisher<Listener> publisher = new Publisher<Listener>();
  
  /** The contour model delegate used by this interactive contour model */
  private BasicContourModel cm = new BasicContourModel();
  
  /**
   * The list of transactions
   */
  private List<Transaction> transactions = new TransactionList();
  
  /** 
   * Current index into the {@link #transactions} list.
   * This index always points to the transaction that would be rolled back
   * if we are asked to roll back; hence, this is also one less than the
   * number of a transaction, if one is added and we are currently at the
   * end of the list.
   * <p>
   * Note that this will therefore only be at its initial value of -1 at
   * the very beginning, when there are no transactions in the list.
   */
  private int index = -1;
  
  private Map<Long, Integer> eventNumberToTransactionIndexMap = new HashMap<Long, Integer>();
  
  private Map<Integer, Long> transactionIndexToEventNumberMap = new HashMap<Integer, Long>();
  
  protected AbstractInteractiveContourModel() {
    // Set the initial state.
    setState(readyToRecordState);
  }
  
  /**
   * Add a listener
   * @param l
   */
  public void addListener(Listener l) { publisher.subscribe(l); }
  
  /**
   * Remove a listener
   * @param l
   */
  public void removeListener(Listener l) { publisher.unsubscribe(l); }
  
  private void fireTransactionRecorded() {
    publisher.publish(new Publisher.Distributor<Listener>() {
      public void deliverTo(Listener subscriber) {
        subscriber.transactionRecorded(AbstractInteractiveContourModel.this);
      }
    });
  }
  
  /**
   * Start recording a transaction.
   * Every transaction on the contour model from this call until
   * {@link #endTransactionRecording()} will be recorded as one
   * transaction.
   * This can only be called if the model is ready to record states.
   */
  public void startTransactionRecording() {
    state.startTransactionRecording();
  }
  
  // TODO Plug-in change
  /**
   * Stop recording a transaction.
   * @see #startTransactionRecording()
   */
  public void endTransactionRecording(long eventNumber) {
    state.endTransactionRecording(eventNumber);
  }
  
  /**
   * Check if the transaction index is currently at the "right" end of the
   * transaction list.
   * @return true if the index is at the end
   */
  private final boolean indexAtRightEndOfTransactions() {
    return index == transactions.size() - 1;
  }
  
  /**
   * Check if the transaction index is currently at the "left" end of the
   * transaction list.
   * @return true if it is at the beginning end
   */
  private final boolean indexAtLeftEndOfTransactions() {
    return index == -1;
  }
  
  public boolean canStepForwardThroughRecordedStates() {
    // Do these have to be pushed into the state objects?  I don't think so.
    return !indexAtRightEndOfTransactions();
  }
  
  public boolean canStepBackward() {
    // Do these have to be pushed into the state objects?  I don't think so.
    return !indexAtLeftEndOfTransactions();
  }
  
  /**
   * Update the state in this contour model.
   * @param state
   */
  private synchronized void setState(State state) {
    // State will be null on the first call.
    if (this.state!=null)  this.state.stateUnset(this);
    
    this.state = state;
    this.state.stateSet(this);
  }
  
  /**
   * Interface for contour model states.
   * An obvious instance of the State design pattern.
   * 
   * @author pvg
   */
  private interface State extends InteractiveContourModel {

    /**
     * Called immediately after this state is set.
     * If the state has some initialization to do, this is when it is done.
     * @param cm the contour model
     */
    void stateSet(AbstractInteractiveContourModel cm);
    
    /**
     * Called immediately before this state is un-set.
     * If the state has any clean-up to do, this is the time to do it.
     * @param cm
     */
    void stateUnset(AbstractInteractiveContourModel cm);
    
    /**
     * Start transaction recording
     */
    void startTransactionRecording();
    
    // TODO Plug-in change
    /**
     * End transaction recording
     * 
     * @param eventNumber the event number corresponding to the transaction
     */
    void endTransactionRecording(long eventNumber);
    
    
    /**
	   * Add a contour to the contour model. An exception will be thrown if the
		 * parent is neither null nor in the model.
		 * 
		 * @param contour
		 *          the contour to add
		 * @param parent
		 *          the parent of the contour, or null if it is a root contour.
		 */
		public void add(Contour contour, Contour parent);

		/**
		 * Remove a contour from the model. All nested contours will also be
		 * removed, and references to the contour in the model become out-of-model
		 * references. Hence, this is operation may take some time, and the caller
		 * (or implementor) is urged to synchronize.
		 * 
		 * @param contour
		 *          the contour to remove
		 */
		public void remove(Contour contour);

		/**
		 * Change a value in the contour model.
		 * 
		 * @param contour
		 *          the contour that contains the variable whose value is changing
		 * @param variableID
		 *          the identifier of the variable that is changing
		 * @param newValue
		 *          the new value for the variable
		 */
		public void setValue(Contour contour, VariableID variableID, Value newValue);

	}
  
  /**
	 * A state that delegates all possible calls to the superclass and has default
	 * handling of interaction calls.
	 * 
	 * <p>
	 * States do not recognize listeners by default.
	 * 
	 * @author pvg
	 */
  private abstract class AbstractState implements State,ContourModel {
    
		public void stepBackward() throws NoSuchElementException,
        IndexOutOfBoundsException {
      throw new IllegalStateException("Cannot step backward from state: "
          + toString());
    }

    public void stepForward() throws NoSuchElementException {
      throw new IllegalStateException("Cannot step forward from state: "
          + toString());
    }

    public int countTransactions() {
      return transactions.size();
    }

    public int getNextTransactionIndex() {
      if (indexAtRightEndOfTransactions()) 
        throw new NoSuchElementException("No available \"next\" transactions!");
      else
        return index + 1;
    }

    public int getPrevTransactionIndex() {
      if (indexAtLeftEndOfTransactions())
        throw new NoSuchElementException("No available \"previous\" transactions!");
      else
        return index;
    }
    
    // TODO Plug-in change
    public int getTransactionIndex(long eventNumber) {
    	if (eventNumberToTransactionIndexMap.containsKey(eventNumber)) {
    		return eventNumberToTransactionIndexMap.get(eventNumber);
    	}
    	else {
    		throw new NoSuchElementException("No transaction corresponding to event number " + eventNumber);
    	}
    }
    
    // TODO Plug-in change
    public long getEventNumber(int transactionIndex) {
    	if (transactionIndexToEventNumberMap.containsKey(transactionIndex)) {
    		return transactionIndexToEventNumberMap.get(transactionIndex);
    	}
    	else {
    		throw new NoSuchElementException("No event number corresponding to transaction index " + transactionIndex);
    	}
    }
    
    public void stateSet(AbstractInteractiveContourModel cm) {
      // Default is to do nothing here.
    }

    public void stateUnset(AbstractInteractiveContourModel cm) {
      // Default is to do nothing here.
    }
    
    public void startTransactionRecording() {
      // By default, it is illegal to start recording a transaction.
      throw new IllegalStateException("I cannot start recording from state: "
          + toString());
    }
    
    // TODO Plug-in change
    public void endTransactionRecording(long eventNumber) {
      // By default, it is illegal to stop recording a transaction.
      throw new IllegalStateException("I cannot stop recording from state: "
          + toString());
    }
    
    public boolean readyToRecord() {
      //default answer is no
      return false;
    }
    
    public final boolean canStepForwardThroughRecordedStates() {
      // Use the containing class' implementation always.
      return AbstractInteractiveContourModel.this.canStepForwardThroughRecordedStates();
    }
    
    public final boolean canStepBackward() {
      // Use the containing class' implementation always.
      return AbstractInteractiveContourModel.this.canStepBackward();
    }
    
    
    // The rest of these methods should just delegate to the contour model.

    public void add(Contour contour, Contour parent) {
      cm.add(contour,parent);
    }

    public void remove(Contour contour) {
      cm.remove(contour);
    }
    
    public void setValue(Contour contour, VariableID variableID, Value newValue) {
      cm.setValue(contour, variableID, newValue);
    }

    public boolean contains(Contour contour) {
      return cm.contains(contour);
    }
    
    // TODO Plug-in change
    public boolean contains(ContourID id) {
    	return cm.contains(id);
    }

    public void export(ContourModel.Exporter exporter) {
      cm.export(exporter);
    }

    public List<Contour> getChildren(Contour parent, List<Contour> result) {
      return cm.getChildren(parent,result);
    }

    public List<Contour> getChildren(Contour parent) {
      return cm.getChildren(parent);
    }

    public List<ContourID> getChildren(ContourID parentID, List<ContourID> result) {
      return cm.getChildren(parentID,result);
    }

    public List<ContourID> getChildren(ContourID parentID) {
      return cm.getChildren(parentID);
    }

    public Contour getContour(ContourID id) {
      return cm.getContour(id);
    }

    public Contour getParent(Contour contour) {
      return cm.getParent(contour);
    }

    public ContourID getParent(ContourID id) {
      return cm.getParent(id);
    }
    
    // TODO Plug-in change.
    public List<Contour> getRoots() {
    	return cm.getRoots();
    }
    
    // TODO Plug-in change.
    public List<Contour> getRoots(List<Contour> result) {
    	return cm.getRoots(result);
    }

    public int size() {
      return cm.size();
    }

    public void visitBreadthFirst(Visitor visitor) {
      cm.visitBreadthFirst(visitor);
    }

    public void visitDepthFirst(Visitor visitor) {
      cm.visitDepthFirst(visitor);
    }
    
    public Iterator<Contour> iterator() {
      return cm.iterator();
    }
    
    public void addListener(ContourModel.Listener l) {
      cm.addListener(l);
    }
    
    public void removeListener(ContourModel.Listener l) {
      cm.removeListener(l);
    }
    
    public int countChildren(Contour parent) {
    	return cm.countChildren(parent);
    }
    
    public int countChildren(ContourID parent) {
    	return cm.countChildren(parent);
    }
    
    public ReentrantLock getModelLock() {
    	return cm.getModelLock();
		}
    
  } // end class AbstractState

  /**
   * A ready state, where the transaction index is currently "back in time".
   * That is, stepping forward will result in playing back a transaction.
   */
  private final State inPastState = new AbstractState() {
    
    @Override
    public final void stepBackward() {
      try {
        getModelLock().lock();
        if (indexAtLeftEndOfTransactions())
          // Note that the lock will not be released here, but that is
          // acceptable since this is a termination exception (stops jive).
          throw new IndexOutOfBoundsException(
              "We are currently at the dawn of time!");
        else {
          // Here's the good stuff: get a transaction, roll it back, and
          // update the index.
          transactions.get(index)
              .rollback(AbstractInteractiveContourModel.this);
          index--;
        }
      } finally {
        getModelLock().unlock();
      }
    }
    
    @Override
    public final void stepForward() {
      try {
        getModelLock().lock();
        if (indexAtRightEndOfTransactions())
          // Note that the lock will not be released here, but that is
          // acceptable since this is a termination exception (stops jive).
          throw new IndexOutOfBoundsException(
              "There are no more recorded transactions to commit!");
        else {
          // Commit a transaction and update the index
          transactions.get(index + 1).commit(
              AbstractInteractiveContourModel.this);
          index++;

          // If we are now at the right end, we are in ready-to-record state.
          if (indexAtRightEndOfTransactions())
            setState(readyToRecordState);
        }
      } finally {
        getModelLock().unlock();
      }
    }
    
    @Override
    public String toString() { return "inPastState"; }
  };
  
  /**
   * A ready state, when there is no recording going on and we are not
   * playing back through history.
   * 
   */
  private final State readyToRecordState = new AbstractState() {
    @Override
    public void startTransactionRecording() {
      // This is the case where we can start recording.
    	
    	// Acquire the lock and set the state.
    	cm.getModelLock().lock();
      setState(recordingState);
    }

    @Override
    public final void stepForward() {
      assert indexAtRightEndOfTransactions();
      
      throw new IndexOutOfBoundsException("There are no more recorded transactions to commit!");
    }
    
    @Override
    public final void stepBackward() {
      // Change to the in-past state and then go for it.
      setState(inPastState);
      AbstractInteractiveContourModel.this.stepBackward();
    }
    
    @Override
    public boolean readyToRecord() {
      return true; // by definition!
    }
    
    @Override
    public String toString() { return "readyToRecordState"; }
  };

  /**
   * A state of the contour model when changes to the contour model
   * are being recorded into a transaction object.
   */
  private final State recordingState = new AbstractState() {

    /** The list of contours added in the transaction */
    private List<Contour> contoursAdded = new ArrayList<Contour>();
    
    /** The map of added contours to their parents */
    private Map<Contour,Contour> contourToParentMap 
      = new HashMap<Contour,Contour>();
    
    /** The list of contours being removed by this transaction */
    private List<Contour> contoursRemoved = new ArrayList<Contour>();
    
    /** Values set to variables while recording */
    private Map<ContourAndVariable,NewAndOldValue> valueMap 
      = new HashMap<ContourAndVariable,NewAndOldValue>();
    
    private Transaction transaction;
    
    @Override
    public void add(Contour contour, Contour parent) {
      // Run the addition like normal, but record it.
      //
      // I don't expect that we can both add and remove in a single transaction.
      // This assertion will check such a case.
      assert contoursRemoved.size()==0 : "Adding and removing from a single transaction!";
      
      // Now, we will delegate to the super (which delegates to a contour model)
      // for the addition and record the effect.
      try {
        super.add(contour,parent);
        
        // As long as no exception is thrown, we can update this list safely.
        contoursAdded.add(contour);
        if(parent!=null)
          contourToParentMap.put(contour,parent);
        
      } catch (RuntimeException e) {
        // Something went wrong with the addition, so we'll just propogate
        // the exception
        throw e;
      } 
    }
    
    @Override
    public void remove(Contour contour) {
      // It's probably not possible to have both additions and removals
      // in one transaction, so this assertion checks that.
      // This may have to be removed later, but it's a good sanity check
      // for now.
      assert contoursAdded.size()==0 : "Removals and additions in the same transaction!";
      
      try {
        // We need the parent for building the transaction.
        Contour parent = getParent(contour);
        
        // Run the parent implementation
        super.remove(contour);
        
        // We got this far, so the removal went as planned.
        // Record the change
        contoursRemoved.add(contour);
        contourToParentMap.put(contour,parent);
      } catch (RuntimeException e) {
        // Propogate the exception.
        throw e;
      }
    }
    
    @Override
    public void setValue(Contour contour, VariableID variableID, Value newValue) {
      assert contour!=null;
      assert variableID!=null;
      assert newValue!=null; // I'm pretty sure value can't be null.  
                // I should look it up in the docs, but I don't feel like it.
      
      ContourAndVariable key = ContourAndVariable.create(contour,variableID);
      Value oldValue = VariableFinder.find(variableID,contour).value();
      NewAndOldValue nav = NewAndOldValue.create(oldValue, newValue);
      
      valueMap.put(key, nav);
      
      // Actually change the value in the model.
      super.setValue(contour,variableID,newValue);
    }
    
    @Override
    public int countTransactions() {
      throw new IllegalStateException("I don\'t think I know how many transactions there are, since I\'m currently recording.");
    }

    @Override
    public int getNextTransactionIndex() {
      // We'll just throw an exception here, though we may need to do something
      // more intelligent later on.
      throw new IllegalStateException("Cannot query transaction indices while recording!");
    }

    @Override
    public int getPrevTransactionIndex() {
      throw new IllegalStateException("Cannot query transaction indices while recording!");
    }

    // TODO Plug-in change
    @Override
    public int getTransactionIndex(long eventNumber) {
      throw new IllegalStateException("Cannot query transaction indices while recording!");
    }
    
    // TODO Plug-in change
    public long getEventNumber(int transactionIndex) {
    	throw new IllegalStateException("Cannot query transaction indices while recording!");
    }
    
    @Override
    public String toString() { 
      return "recordingState";
    }

    // TODO Plug-in change
    @Override
    public void endTransactionRecording(long eventNumber) {
      // This ends the transaction recording, so the transaction is ready
      // to be built.
      assert this.transaction==null;
      this.transaction = new DefaultTransaction(contoursAdded,
            contoursRemoved, contourToParentMap, valueMap);
      
      // Record the transaction, update the index
      transactions.add(transaction);
      index++;
      // TODO Plug-in change
      eventNumberToTransactionIndexMap.put(eventNumber, index);
      transactionIndexToEventNumberMap.put(index, eventNumber);
      
      // Reset the state
      setState(readyToRecordState);
      
      // Clean up 
      contoursAdded.clear();
      contoursRemoved.clear();
      contourToParentMap.clear();
      valueMap.clear();
      this.transaction=null;
      
      // Release the lock
      cm.getModelLock().unlock();
      
      // notify listeners
      fireTransactionRecorded();
    }
  };
  
  /** The current state of the interactive contour model */
  private State state;

  protected void add(Contour contour, Contour parent) {
    state.add(contour,parent);
  }
  
  protected void remove(Contour contour) {
    state.remove(contour);
  }
  
  protected void setValue(Contour contour, VariableID variableID, Value value) {
    state.setValue(contour,variableID,value);
  }
  
  @Override
  public String toString() {
    //getModelLock().lock();
    StringBuffer b = new StringBuffer();
    b.append("Next transaction index is " + index + "\n");
    b.append("Transactions:\n");
    int i=0;
    for (Transaction t : transactions) {
      b.append(i++ + ".\n");
      b.append(t);
      b.append('\n');
    }
    b.append("Contour model: \n" + cm);
    //getModelLock().unlock();
    return b.toString();
  }
  
  // -------------------------------------------------------------------
  // Contour model methods
  //
  // These simply delegate to the state object.
  // -------------------------------------------------------------------
  
  public boolean contains(Contour contour) {
    return state.contains(contour);
  }
  
  // TODO Plug-in change
  public boolean contains(ContourID id) {
  	return state.contains(id);
  }

  public void export(ContourModel.Exporter exporter) {
    state.export(exporter);
  }

  public List<Contour> getChildren(Contour parent, List<Contour> result) {
    return state.getChildren(parent,result);
  }

  public List<Contour> getChildren(Contour parent) {
    return state.getChildren(parent);
  }

  public List<ContourID> getChildren(ContourID parentID, List<ContourID> result) {
    return state.getChildren(parentID,result);
  }

  public List<ContourID> getChildren(ContourID parentID) {
    return state.getChildren(parentID);
  }

  public Contour getContour(ContourID id) {
    return state.getContour(id);
  }

  public Contour getParent(Contour contour) {
    return state.getParent(contour);
  }

  public ContourID getParent(ContourID id) {
    return state.getParent(id);
  }
  
  // TODO Plug-in change.
  public List<Contour> getRoots() {
  	return state.getRoots();
  }
  
  // TODO Plug-in change.
  public List<Contour> getRoots(List<Contour> result) {
  	return state.getRoots(result);
  }

  public int size() {
    return state.size();
  }

  public void visitBreadthFirst(Visitor visitor) {
    state.visitBreadthFirst(visitor);
  }

  public void visitDepthFirst(Visitor visitor) {
    state.visitDepthFirst(visitor);
  }

  public Iterator<Contour> iterator() {
    return state.iterator();
  }
  
  public int countTransactions() {
    return state.countTransactions();
  }

  public void stepForward() throws NoSuchElementException {
    state.stepForward();
  }

  public void stepBackward() throws NoSuchElementException,
      IndexOutOfBoundsException {
    state.stepBackward();
  }

  public int getNextTransactionIndex() {
    return state.getNextTransactionIndex();
  }

  public int getPrevTransactionIndex() {
    return state.getPrevTransactionIndex();
  }
  
  public int getTransactionIndex(long eventNumber) {
  	return state.getTransactionIndex(eventNumber);
  }
  
  public long getEventNumber(int transactionIndex) {
  	return state.getEventNumber(transactionIndex);
  }
  
  public boolean readyToRecord() {
    return state.readyToRecord();
  }
  
  public void addListener(ContourModel.Listener l) {
    state.addListener(l);
  }
  
  public void removeListener(ContourModel.Listener l) {
    state.removeListener(l);
  }

	public int countChildren(Contour parent) {
		return state.countChildren(parent);
	}

	public int countChildren(ContourID parent) {
		return state.countChildren(parent);
	}
	
	public ReentrantLock getModelLock() { 
		return state.getModelLock();
	}
}

//----------------------------------------------------------------------

interface Transaction {
  public void commit(AbstractInteractiveContourModel cm);
  public void rollback(AbstractInteractiveContourModel cm);
}

//----------------------------------------------------------------------

//TODO: this could become a flyweight object, which would reduce the
// total number of maps, probably.
class DefaultTransaction implements Transaction {
  
  private enum ExecutionDirection { FORWARD, BACKWARD };
  
  private List<Contour> contoursToAdd;
  private List<Contour> contoursToRemove;
  private Map<Contour,Contour> contourToParentMap;
  private Map<ContourAndVariable,NewAndOldValue> valueMap;
  
  public DefaultTransaction(List<Contour> contoursToAdd, 
      List<Contour> contoursToRemove,
      Map<Contour,Contour> contourToParentMap,
      Map<ContourAndVariable,NewAndOldValue> valueMap) {
    
    if (contoursToAdd!=null && contoursToAdd.size()>0)
      this.contoursToAdd = new ArrayList<Contour>(contoursToAdd);
    
    if (contoursToRemove!=null && contoursToRemove.size()>0)
      this.contoursToRemove = new ArrayList<Contour>(contoursToRemove);
    
    if (valueMap!=null && valueMap.size()>0)
      this.valueMap = new HashMap<ContourAndVariable,NewAndOldValue>(valueMap);
    
    this.contourToParentMap = new HashMap<Contour,Contour>(contourToParentMap);
  }
  
  private void addContours(List<Contour> list, AbstractInteractiveContourModel cm) {
    for (Contour c : list) 
      cm.add(c,contourToParentMap.get(c));
  }
  
  /**
   * Remove a list of contours from the contour model.
   * We can only remove contours if they have no children, but there is a 
   * chance that this list contains contours that contain each other.
   * Contours must be ordered before removal in such cases.
   * Hence, in the best case, this method takes linear time (in the number
   * of contours in the list, discounting the complexity of actually
   * calling the remove method), but it may take quadratic time in the
   * worst case.
   * However, the lists should usually be very small, so the cost is
   * acceptable, for now at least.
   * 
   * @param list contours to remove from the contour model
   * @param cm the contour model
   */
  private void removeContours(List<Contour> list, AbstractInteractiveContourModel cm) {
  	// TODO Plug-in change
  	LinkedList<Contour> temp = new LinkedList<Contour>();
  	temp.addAll(list);
  	while (!temp.isEmpty()) {
  		sortByNumberOfChildren(temp, cm);
  		Contour c = temp.removeFirst();
  		cm.remove(c);
  	}
  }
  
  private void sortByNumberOfChildren(List<Contour> list, final AbstractInteractiveContourModel cm) {
  	// simple bubble sort
  	for (int i=0; i<list.size()-1; i++) {
  		for (int j=1; j<list.size(); j++) {
  			Contour ci = list.get(i);
  			Contour cj = list.get(j);
  			if (cm.countChildren(ci) > cm.countChildren(cj)) {
  				list.set(i, cj);
  				list.set(j, ci);
  			}
  		}
  	}
  }
  
  /**
   * Update all the variables in the contour model in accordance with
   * the provided value map.
   * 
   * @param valueMap
   * @param cm
   * @param direction
   */
  private void updateValues(Map<ContourAndVariable, NewAndOldValue> valueMap,
      AbstractInteractiveContourModel cm, ExecutionDirection direction) {
    for (ContourAndVariable cv : valueMap.keySet()) {
      if (direction.equals(ExecutionDirection.FORWARD))
        cm.setValue(cv.getContour(), cv.getVariableID(), valueMap.get(cv).getNewValue());
      else {
        assert direction.equals(ExecutionDirection.BACKWARD);
        // TODO Plug-in change
        cm.setValue(cv.getContour(), cv.getVariableID(), valueMap.get(cv).getOldValue());
      }
    }
  }
  
  public void commit(AbstractInteractiveContourModel cm) {
    if (contoursToAdd!=null)
      addContours(contoursToAdd, cm);
    if (contoursToRemove!=null) 
      removeContours(contoursToRemove, cm);
    if (valueMap!=null) 
      updateValues(valueMap,cm,ExecutionDirection.FORWARD);
  }
  
  public void rollback(AbstractInteractiveContourModel cm) {
    if (contoursToRemove!=null)
      addContours(contoursToRemove, cm);
    // TODO Plug-in change
    // Values should be updated before contours are removed for the case of
    // un-initializing actual parameters
    if (valueMap!=null)
      updateValues(valueMap,cm,ExecutionDirection.BACKWARD);
    if (contoursToAdd!=null) 
      removeContours(contoursToAdd,cm);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("Transaction(");
    sb.append("add={" + 
        (contoursToAdd==null ? "none" : 
          StringSeparator.toString(contoursToAdd)));
    sb.append("}, ");
    sb.append("remove={" + 
        (contoursToRemove == null ? "none" :
          StringSeparator.toString(contoursToRemove)));
    sb.append("})");
    return sb.toString();
  }
}

/**
 * Collects a contour and a variable identifier together for use as a 
 * key in a {@link java.util.HashMap}.
 * 
 * @see edu.bsu.cs.jive.contour.Contour
 * @see edu.bsu.cs.jive.util.VariableID
 * @author pvg
 */
final class ContourAndVariable {
  private final Contour contour;
  private final VariableID variableID;
  
  public static ContourAndVariable create(Contour contour, VariableID variableID) {
    // A cache might be useful here, but it would have to be carefully weak.
    return new ContourAndVariable(contour,variableID);
  }
  
  private ContourAndVariable(Contour contour, VariableID v) {
    assert contour!=null;
    assert v!=null;
    this.contour=contour;
    this.variableID=v;
  }
  
  public Contour getContour() { return contour; }
  public VariableID getVariableID() { return variableID; }
  
  @Override 
  public boolean equals(Object o) {
    try {
      ContourAndVariable cv = (ContourAndVariable)o;
      return cv.contour.equals(this.contour) && cv.variableID.equals(this.variableID); 
    } catch (ClassCastException cce) {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    int result = HashUtils.hash(HashUtils.SEED, contour);
    result = HashUtils.hash(result, variableID);
    return result;
  }
}

/**
 * A class that holds both a new (commit, forward) value for a variable
 * as well as an old (rollback, backward) value for a variable.
 * 
 * @see edu.bsu.cs.jive.contour.Value
 * @author pvg
 */
final class NewAndOldValue {
  private final Value oldValue, newValue;
  
  public static NewAndOldValue create(Value oldValue, Value newValue) {
    return new NewAndOldValue(oldValue,newValue);
  }
  
  private NewAndOldValue(Value oldValue, Value newValue) {
    this.oldValue=oldValue;
    this.newValue=newValue;
  }
  
  public Value getNewValue() { return newValue; }
  public Value getOldValue() { return oldValue; }
  
  @Override
  public boolean equals(Object o) {
    try {
      NewAndOldValue nov = (NewAndOldValue)o;
      return nov.newValue.equals(this.newValue)
        && nov.oldValue.equals(this.oldValue);
    } catch (ClassCastException cce) {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    int result = HashUtils.hash(HashUtils.SEED, oldValue);
    result = HashUtils.hash(result, newValue);
    return result;
  }
}