package edu.buffalo.cse.jive.core;

import org.eclipse.jdt.debug.core.IJavaDebugTarget;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;

// TODO Determine if this should be used as a facade for the two models
// (or at for least stepping).  
/**
 * An <code>IJavaDebugTarget</code> monitored by JIVE.  The target is monitored
 * for particular events of interest which are used to produce JIVE events.  It
 * maintains two models representing the execution state and execution history
 * of the running program.
 *  
 * @author Jeffrey K Czyz
 */
public interface IJiveDebugTarget extends IJavaDebugTarget {
	
	/**
	 * Returns whether or not the the debug target can replay past states.  It
	 * can if all non-system threads are suspended.
	 * 
	 * @return <code>true</code> if past states can be replayed,
	 *         <code>false</code> otherwise
	 */
	public boolean canReplayRecordedStates();
	
	/**
	 * Returns the JIVE event log that the debug target has built thus far.
	 * 
	 * @return the JIVE event log
	 */
	public IJiveEventLog getJiveEventLog();
	
	/**
	 * Returns the contour model associated with the debug target.  A contour
	 * model is used to represent the execution state of a program.
	 * 
	 * @return the contour model
	 */
	public InteractiveContourModel getContourModel();
	
	/**
	 * Returns the sequence model associated with the debug target.  A sequence
	 * model is used to represent the execution history of a program.
	 * 
	 * @return the sequence model
	 */
	public MultiThreadedSequenceModel getSequenceModel();

}
