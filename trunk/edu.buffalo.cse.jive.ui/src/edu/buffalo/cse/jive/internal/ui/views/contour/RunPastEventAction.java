package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.Action;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.events.Event;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

public class RunPastEventAction extends Action implements IStepAction {
	
	private enum SteppingDirection { FORWARD, BACKWARD };
	
	private SteppingDirection direction;

	private IJiveDebugTarget target;
	
	private long eventNumber;
	
	private IStepManager manager;
	
	public RunPastEventAction(IJiveDebugTarget target, Event event) {
		this.target = target;
		this.eventNumber = event.number();
		this.manager = JiveUIPlugin.getDefault().getStepManager();
	}
	
	public void run() {
		manager.run(target, this);
	}
	
	public boolean canStep(IJiveDebugTarget target) {
		InteractiveContourModel model = target.getContourModel();
		int transactionIndex = model.getTransactionIndex(eventNumber);
		
		if (model.canStepBackward()) {
			if (model.getPrevTransactionIndex() > transactionIndex) {
				direction = SteppingDirection.BACKWARD;
				return true;
			}
		}
		
		if (model.canStepForwardThroughRecordedStates()) {
			if (model.getNextTransactionIndex() <= transactionIndex) {
				direction = SteppingDirection.FORWARD;
				return true;
			}
		}
		
		return false;
	}

	public void step(IJiveDebugTarget target) {
		if (!target.canReplayRecordedStates()) {
			try {
				target.suspend();
			}
			catch (DebugException e) {
				JiveUIPlugin.log(e.getStatus());
			}
		}
		
		InteractiveContourModel model = target.getContourModel();
		switch (direction) {
		case FORWARD:
			model.stepForward();
			break;
		case BACKWARD:
			model.stepBackward();
			break;
		}
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
