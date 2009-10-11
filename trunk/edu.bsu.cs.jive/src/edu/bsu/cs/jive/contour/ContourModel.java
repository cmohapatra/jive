package edu.bsu.cs.jive.contour;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;


/**
 * A contour model is a representation of program execution state. 
 * 
 * @author pvg
 */
public interface ContourModel extends Iterable<Contour> {
  
  /**
   * A visitor interface for the contour model.
   */
  public interface Visitor {
    /**
     * Visit a contour in the model
     * @param contour a contour in the model
     */
    public void visit(Contour contour);
  }
  
  /**
   * Process a visitor in depth-first order.
   * @param visitor a visitor
   */
  public void visitDepthFirst(Visitor visitor);
  
  /**
   * Process a visitor in breadth-first order.
   * @param visitor a visitor
   */
  public void visitBreadthFirst(Visitor visitor);
  
  /**
   * A builder interface for a contour model.
   * Since a contour model represents a specific state of execution,
   * this interface can be used to initialize the contour model to a
   * non-null state.
   * <p>
   * The builder's {@link #provideChildren(Contour)} method will be called
   * until all root contours' children have been found (in all depths),
   * but there is no guarantee of depth-first vs. breadth-first ordering
   * of the calls.
   * 
   * @author pvg
   */
  public interface Importer {
    /**
     * Provide the root contours for this importer
     * @return list of root contours
     */
    public List<Contour> provideRoots();
    
    /**
     * Provide the list of children for a contour.
     * If the contour has no children, then null is returned.
     * @param parent the parent whose children are sought
     * @return the list of children of the contour
     *   or null if there are none. 
     */
    public List<Contour> provideChildren(Contour parent);
    
  }
  
  /**
   * An exporter for a contour model.
   * The roots will be exported first, followed by the remaining contours.
   * 
   * @author pvg
   */
  public interface Exporter {
    /**
     * Export a contour.  Roots will be exported first.
     * @param contour a contour
     * @param parent the contour's parent, or null if it is a root contour
     */
    public void addContour(Contour contour, Contour parent);
    
    /**
     * Called when the exportation is finished.
     */
    public void exportFinished();
  }
  
  /**
   * Export this contour model (a snapshot of program execution) to a
   * reverse-builder.
   * 
   * @param exporter an exporter; a reverse-builder
   */
  public void export(Exporter exporter);
  
  /**
   * Get the lock object for synchronized reading and writing to the model.
   * TODO: complete comments here. (Only owning thread can modify, etc.)
   * @return lock
   */
  public ReentrantLock getModelLock();

  /**
   * Check if the contour model contains a specific contour.
   * @param contour
   * @return true iff the contour is in the model
   */
  public boolean contains(Contour contour);
  
  // TODO Plug-in change
  /**
   * Check if the contour model contains a specific contour by ID.
   * @param id the ID of the contour
   * @return true iff a contour with the given ID is in the model
   */
  public boolean contains(ContourID id);
  
  /**
   * Get the contour associated with a specific contour ID.
   * @param id a contour identifier
   * @return the contour with that ID, or null if it is not in the model
   */
  public Contour getContour(ContourID id);
  
  /**
   * Get the parent (enclosing contour) of a specific contour, by its id.
   * @param id the contour identifier for a contour
   * @return the identifier of the parent of the parameter contour 
   *  or null if it is a root contour
   * @see #getParent(Contour)
   * @see #getContour(ContourID)
   */
  public ContourID getParent(ContourID id);
  
  /**
   * Get the parent (enclosing contour) of a specific contour.
   * @param contour the query contour
   * @return the contour's parent or null if it is a root contour
   * @see #getParent(ContourID)
   */
  public Contour getParent(Contour contour);
 
  
  /**
   * Count the number of children of a specific contour.
   * @param parent a parent contour
   * @return the number of immediate children (not total descendents) of the
   *  given contour
   */
  public int countChildren(ContourID parent);
  
  /**
   * Count the number of children of a specific contour.
   * @param parent a parent contour
   * @return the number of immediate children (not total descendents) of the
   *  given contour
   */
  public int countChildren(Contour parent);
  
  /**
   * Get the children of a specific contour.
   * The returned list must not be a reference to any internal data model
   * of this contour model.
   *  
   * @param parentID the contour whose children are sought
   * @return list of children (possibly empty, not null)
   */
  public List<ContourID> getChildren(ContourID parentID);

  /**
   * Get the children of a specific contour.
   * 
   * @param parentID the contour whose children are sought
   * @param result the list into which the result is put (may be null)
   * @return the result list (or a new list if it is null)
   */
  public List<ContourID> getChildren(ContourID parentID, List<ContourID> result);

  /**
   * Get the children of a specific contour.
   * The returned list must not be a reference to any internal data model
   * of this contour model.
   *  
   * @param parent the contour whose children are sought
   * @return list of children (possibly empty, not null)
   */
  public List<Contour> getChildren(Contour parent);

  /**
   * Get the children of a specific contour.
   * 
   * @param parent the contour whose children are sought
   * @param result the list into which the result is put (may be null)
   * @return the result list (or a new list if it is null)
   */
  public List<Contour> getChildren(Contour parent, List<Contour> result);
  
  // TODO Plug-in change.
  /**
   * Get the root contours.  The returned list must not be a reference to any
   * internal data model of this contour model.
   * 
   * @return list of roots (possibly empty, not null)
   */
  public List<Contour> getRoots();
  
  // TODO Plug-in change
  /**
   * Get the root contours.
   * 
   * @param result the list into which the result is put (may be null)
   * @return the result list (or a new list if it is null)
   */
  public List<Contour> getRoots(List<Contour> result);
  
  /**
   * Count the number of contours in this model.
   * @return the number of contours in the model
   */
  public int size();
  

  /**
   * Listener interface for contour model changes.
   * @author pvg
   */
  public interface Listener {
  	// TODO Plug-in change.
    /**
     * Called when a contour is added to the model
     * @param model the contour model
     * @param contour the contour added
     */
    public void contourAdded(ContourModel model, Contour contour, Contour parent);
    
    /**
     * Called when a contour is removed from the model
     * @param model the contour model
     * @param contour the contour removed
     * @param oldParent the old parent of the contour, or null if 
     *  it was a root contour
     */
    public void contourRemoved(ContourModel model, Contour contour, Contour oldParent);
    
    /**
     * Called when a value is changed in the contour model
     * @param model the contour model
     * @param contour the contour that  contains the value that changed
     * @param variableID identifies the variable in the contour that changed
     * @param newValue the new value of the variable
     * @param oldValue the previous value of the variable
     */
    public void valueChanged(ContourModel model, Contour contour, VariableID variableID,
        Value newValue, Value oldValue);
  }
  
  /**
   * Register a listener.
   * @param listener
   */
  public void addListener(Listener listener);
  
  /**
   * Unregister a listener
   * @param listener
   */
  public void removeListener(Listener listener);
}

