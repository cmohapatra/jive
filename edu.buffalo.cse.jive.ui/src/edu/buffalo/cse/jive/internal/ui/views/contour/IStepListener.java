package edu.buffalo.cse.jive.internal.ui.views.contour;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;

/**
 * An interface used for those who need to be notified when stepping occurs on a
 * {@code IJiveDebugTarget}.   Stepping typically consists of traversing the
 * transaction history of an {@code InteractiveContourModel} associated with the
 * target.  Classes conforming to this interface are notified when stepping
 * is initiated and when it is completed.  Stepping can refer to either single
 * steps or multiple steps (i.e., running).
 * 
 * @see IStepAction
 * @see IStepManager
 * @author Jeffrey K Czyz
 */
public interface IStepListener {
	
	/**
	 * Called when stepping has been initiated on the supplied target.
	 * 
	 * @param target the target upon which stepping was initiated
	 */
	public void steppingInitiated(IJiveDebugTarget target);
	
	/**
	 * Called when stepping has completed on the supplied target.
	 * 
	 * @param target the target upon which stepping has completed
	 */
	public void steppingCompleted(IJiveDebugTarget target);
}
