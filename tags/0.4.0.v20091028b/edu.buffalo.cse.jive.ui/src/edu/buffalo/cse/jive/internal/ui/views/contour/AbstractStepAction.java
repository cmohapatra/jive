package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.jface.action.Action;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveView;
import edu.buffalo.cse.jive.ui.IUpdatableAction;

/**
 * An {@code Action} that takes one step in an {@code IJiveDebugTarget}'s
 * transaction history.  The action can update itself based on the state of the
 * debug target and its contour model.
 * 
 * @see IStepManager
 * @see IStepAction
 * @author Jeffrey K Czyz
 */
public abstract class AbstractStepAction extends Action implements IUpdatableAction, IStepAction, IStepListener {
	
	/**
	 * The {@code IStepManager} used to determine if a step is in progress.
	 */
	protected IStepManager manager;
	
	/**
	 * The {@code IJiveView} in which the step action is associated.  The
	 * view's active debug target is used for stepping.
	 */
	protected IJiveView view;
	
	/**
	 * Constructs the step action.
	 * 
	 * @param view the view for the action
	 */
	public AbstractStepAction(IJiveView view) {
		this.view = view;
		manager = JiveUIPlugin.getDefault().getStepManager();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
	 */
	public void update() {
		IJiveDebugTarget target = view.getDisplayed();
		if (canReplayRecordedStates(target)) {
			setEnabled(canStep(target));
		}
		else {
			setEnabled(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#stepInitiated(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingInitiated(IJiveDebugTarget target) {
		if (target == view.getDisplayed()) {
			setEnabled(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#stepCompleted(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingCompleted(IJiveDebugTarget target) {
		if (target == view.getDisplayed()) {
			setEnabled(canStep(target));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IJiveDebugTarget target = view.getDisplayed();
		manager.step(target, this);
	}
	
	/**
	 * Returns whether or not the supplied target (in it's current state) can
	 * replay states recorded by its contour model. 
	 * 
	 * @param target the target to check if replaying is possible
	 * @return <code>true</code> if the target's states can be replayed,
	 *         <code>false</code> otherwise
	 */
	private boolean canReplayRecordedStates(IJiveDebugTarget target) {
		return target != null && target.canReplayRecordedStates() && !manager.isStepping(target);
	}
}
