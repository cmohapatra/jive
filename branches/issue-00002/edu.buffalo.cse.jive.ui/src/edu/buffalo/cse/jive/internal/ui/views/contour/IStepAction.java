package edu.buffalo.cse.jive.internal.ui.views.contour;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;

/**
 * An interface used to specify an action for stepping through transactions on
 * an {@code InteractiveContourModel} associated with an
 * {@code IJiveDebugTarget}.  An {@code IStepAction} is used as a parameter to
 * either {@link IStepManager#step(IJiveDebugTarget, IStepAction)} or
 * {@link IStepManager#run(IJiveDebugTarget, IStepAction)}.
 * 
 * @see IStepManager
 * @see IStepListener
 * @author Jeffrey K Czyz
 */
public interface IStepAction {

	/**
	 * Returns whether or not stepping through the transactions on the
	 * {@code InteractiveContourModel} associated with the supplied target can
	 * be performed.
	 * 
	 * @param target the target in which to step
	 * @return <code>true</code> if stepping can proceed,
	 *         <code>false</code> otherwise
	 */
	public boolean canStep(IJiveDebugTarget target);
	
	/**
	 * Performs a step through the transactions on the
	 * {@code InteractiveContourModel} associated with the supplied target.
	 * 
	 * @param target the target in which to step
	 */
	public void step(IJiveDebugTarget target);
}
