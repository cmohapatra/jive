package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.IJiveView;

/**
 * An {@code IAction} that takes one step backward through an
 * {@code IJiveDebugTarget}'s transaction history.  The action can update itself
 * based on the state of the debug target and its contour model.
 * 
 * @see IAction
 * @see IStepAction
 * @see IStepManager#step(IJiveDebugTarget, IStepAction)
 * @author Jeffrey K Czyz
 */
public class StepBackwardAction extends AbstractStepAction {
	
	/**
	 * Constructs the action.
	 * 
	 * @param view the view for the action
	 */
	public StepBackwardAction(IJiveView view) {
		super(view);
		setText("Step Backward");
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_STEP_BACKWARD_ICON_KEY));
		setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_STEP_BACKWARD_ICON_KEY));
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction#canStep(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public boolean canStep(IJiveDebugTarget target) {
		InteractiveContourModel model = target.getContourModel();
		return model.canStepBackward();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction#step(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void step(IJiveDebugTarget target) {
		EventOccurrence event;
		do {
			InteractiveContourModel model = target.getContourModel();
			model.stepBackward();
			if (canStep(target)) {
				int transactionIndex = model.getPrevTransactionIndex();
				long eventNumber = model.getEventNumber(transactionIndex);
				event = target.getSequenceModel().getEventOccurrence(eventNumber);
			}
			else {
				break;
			}
		}
		while (!(event.underlyingEvent() instanceof EOSEvent));
	}
}
