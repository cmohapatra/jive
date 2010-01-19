package edu.buffalo.cse.jive.internal.ui.views.contour;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

/**
 * An implementation of an {@code IStepManager} used to perform stepping on
 * {@code IJiveDebugTarget}s.
 * 
 * @see IStepManager
 * @see IStepAction
 * @see IStepListener
 * @author Jeffrey K Czyz
 */
public class StepManager implements IStepManager {
	
	/**
	 * A list of {@code IStepListener}s that are interested of being notified of
	 * step occurrences.
	 */
	private List<IStepListener> updateList;
	
	/**
	 * A list of {@code IJiveDebugTarget}s in which stepping is in progress. 
	 */
	private List<IJiveDebugTarget> steppingList;
	
	/**
	 * Constructs the step manager.
	 */
	public StepManager() {
		updateList = new LinkedList<IStepListener>();
		steppingList = new ArrayList<IJiveDebugTarget>();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#addStepListener(edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener)
	 */
	public void addStepListener(IStepListener listener) {
		if (!updateList.contains(listener)) {
			updateList.add(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#removeStepListener(edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener)
	 */
	public void removeStepListener(IStepListener listener) {
		if (updateList.contains(listener)) {
			updateList.remove(listener);
		}
	}
	
	/**
	 * Cleans up the manager when it is no longer needed.
	 */
	public void dispose() {
		updateList.clear();
		steppingList.clear();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#step(edu.buffalo.cse.jive.core.IJiveDebugTarget, edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction)
	 */
	public synchronized void step(IJiveDebugTarget target, IStepAction action) {
		if (action.canStep(target)) {
			initiateStep(target);
			action.step(target);
			completeStep(target);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#run(edu.buffalo.cse.jive.core.IJiveDebugTarget, edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction)
	 */
	public synchronized void run(IJiveDebugTarget target, IStepAction action) {
		if (action.canStep(target)) {
			initiateStep(target);
			requestStep(target, action);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#pause(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public synchronized void pause(IJiveDebugTarget target) {
		if (isStepping(target)) {
			completeStep(target);
		}
		else if (target.canSuspend()) {
			try {
				target.suspend();
			}
			catch (DebugException e) {
				JiveUIPlugin.log(e.getStatus());
			}
		}
	}
	
	/**
	 * Requests another step to be taken on the supplied target by delegating to
	 * the given action.  The step is performed on a separate {@code Runnable}
	 * in order to allow pausing.
	 * 
	 * @param target the target on which to step
	 * @param action the step action to use
	 */
	private synchronized void requestStep(final IJiveDebugTarget target, final IStepAction action) {
		Display display = JiveUIPlugin.getStandardDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				// Check if stepping was not paused
				if (isStepping(target)) {
					if (action.canStep(target)) {
						action.step(target);  // take the step
						requestStep(target, action);  // request another step
					}
					else {
						completeStep(target);
					}
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager#isStepping(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public synchronized boolean isStepping(IJiveDebugTarget target) {
		return steppingList.contains(target);
	}
	
	/**
	 * Initiates stepping on the supplied target by recording that stepping is
	 * in progress and by notifying any listeners.
	 * 
	 * @param target the target in which stepping is being initiated
	 */
	private synchronized void initiateStep(IJiveDebugTarget target) {
		assert !isStepping(target);
		steppingList.add(target);
		fireStepInitiatedEvent(target);
	}
	
	/**
	 * Completes stepping on the supplied target by recording that a stepping is
	 * no longer in progress and by notifying any listeners.
	 * 
	 * @param target the target in which stepping has completed
	 */
	private synchronized void completeStep(IJiveDebugTarget target) {
		assert isStepping(target);
		steppingList.remove(target);
		fireStepCompletedEvent(target);
	}
	
	/**
	 * Notifies listeners that stepping has initiated on the given target.
	 * 
	 * @param target the target in which stepping has initiated
	 */
	private void fireStepInitiatedEvent(IJiveDebugTarget target) {
		for (IStepListener listener : updateList) {
			listener.steppingInitiated(target);
		}
	}
	
	/**
	 * Notifies listeners that stepping has completed on the given target.
	 * 
	 * @param target the target in which stepping has completed
	 */
	private void fireStepCompletedEvent(IJiveDebugTarget target) {
		for (IStepListener listener : updateList) {
			listener.steppingCompleted(target);
		}
	}
}
