package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;

/**
 * Identifies a JiveLog event adapter class.
 * <p>
 * Note that adapters need to handle locking and unlocking the contour
 * model when writing to it: see 
 * {@link edu.bsu.cs.jive.contour.ContourModel#getModelLock()}.
 * 
 * @author pvg
 */
interface EventAdapter {
  
  /**
   * Apply the event adapter to the contour model.
   * After calling this method, the state of the adapter should be reset.
   * @param cm
   */
  public void apply(JavaInteractiveContourModel cm);

}
