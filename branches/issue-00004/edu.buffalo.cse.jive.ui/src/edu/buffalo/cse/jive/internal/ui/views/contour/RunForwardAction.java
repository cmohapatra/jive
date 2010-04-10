package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.IJiveView;

/**
 * An {@code IAction} that runs forward in an {@code IJiveDebugTarget}'s
 * until the last transaction has been committed.  The action can update
 * itself based on the state of the debug target and its contour model.
 * 
 * @see IAction
 * @see IStepAction
 * @see IStepManager#run(IJiveDebugTarget, IStepAction)
 * @author Jeffrey K Czyz
 */
public class RunForwardAction extends AbstractRunAction {
	
	/**
	 * Constructs the action.
	 * 
	 * @param view the view for the action
	 */
	public RunForwardAction(IJiveView view) {
		super(view);
		setText("Run Forward");
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_RUN_FORWARD_ICON_KEY));
		setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_RUN_FORWARD_ICON_KEY));
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction#canStep(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public boolean canStep(IJiveDebugTarget target) {
		InteractiveContourModel model = target.getContourModel();
		return model.canStepForwardThroughRecordedStates();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction#step(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void step(IJiveDebugTarget target) {
		InteractiveContourModel model = target.getContourModel();
		model.stepForward();
	}
}
