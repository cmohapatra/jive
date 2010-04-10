package edu.buffalo.cse.jive.internal.ui.views.contour;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.ui.IJiveView;

/**
 * An {@code Action} that steps through an {@code IJiveDebugTarget}'s
 * transaction history until no more steps should be taken or stepping is
 * paused.  The action can update itself based on the state of the debug target
 * and its contour model.
 * 
 * @see IStepManager
 * @see IStepAction
 * @author Jeffrey K Czyz
 */
public abstract class AbstractRunAction extends AbstractStepAction {
	
	/**
	 * Constructs the run action.
	 * 
	 * @param view the view for the action
	 */
	public AbstractRunAction(IJiveView view) {
		super(view);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IJiveDebugTarget target = view.getDisplayed();
		manager.run(target, this);
	}
}
