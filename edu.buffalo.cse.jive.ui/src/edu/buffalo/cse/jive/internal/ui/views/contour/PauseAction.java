package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageRegistry;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.IJiveView;
import edu.buffalo.cse.jive.ui.IUpdatableAction;

/**
 * An {@code Action} that halts any steps over an {@code IJiveDebugTarget}'s
 * transaction history that may be in progress.  The action can update itself
 * based on the state of the debug target and its contour model.
 * 
 * @see IStepManager
 * @author Jeffrey K Czyz
 */
public class PauseAction extends Action implements IUpdatableAction, IStepListener {
	
	/**
	 * The {@code IStepManager} used to determine if a step is in progress.
	 */
	private IStepManager manager;
	
	/**
	 * The {@code IJiveView} in which a step action is assoiciated.  The
	 * view's active debug target is used for pausing.
	 */
	private IJiveView view;
	
	/**
	 * Constructs the pause action.
	 */
	public PauseAction(IJiveView view) {
		this.view = view;
		manager = JiveUIPlugin.getDefault().getStepManager();;
		setText("Pause");
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_PAUSE_ICON_KEY));
		setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_PAUSE_ICON_KEY));
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
	 */
	public void update() {
		IJiveDebugTarget target = view.getDisplayed();
		if (target != null) {
			if (target.canReplayRecordedStates() && manager.isStepping(target)) {
				setEnabled(true);
				return;
			}
			
			if (target.canSuspend()) {
				setEnabled(true);
				return;
			}
		}
		
		setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IJiveDebugTarget target = view.getDisplayed();
		manager.pause(target);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#steppingInitiated(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingInitiated(IJiveDebugTarget target) {
		if (target == view.getDisplayed()) {
			setEnabled(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#steppingCompleted(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingCompleted(IJiveDebugTarget target) {
		if (target == view.getDisplayed()) {
			setEnabled(false);
		}
	}
}