package edu.buffalo.cse.jive.internal.ui.views.contour;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AssignEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.CallEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.EOSEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.CatchEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.LoadEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.NewEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.ReturnEventActionLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.ThrowEventActionLabelProvider;
import edu.buffalo.cse.jive.ui.JiveUITools;

public class RunToEventAction extends Action implements IStepAction {
	
	private enum SteppingDirection { FORWARD, BACKWARD };
	
	private SteppingDirection direction;

	private IJiveDebugTarget target;
	
	private long eventNumber;
	
	private IStepManager manager;
	
	public RunToEventAction(IJiveDebugTarget target, Event event) {
		event.accept(actionInitializer, this);
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
			if (model.getPrevTransactionIndex() >= transactionIndex) {
				direction = SteppingDirection.BACKWARD;
				return true;
			}
		}
		
		if (model.canStepForwardThroughRecordedStates()) {
			if (model.getNextTransactionIndex() < transactionIndex) {
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
	
	private static Event.Visitor actionInitializer = new Event.Visitor() {

		public Object visit(AssignEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(assignEventLabelProvider);
			action.setText(assignEventLabelProvider.getText());
			action.setImageDescriptor(assignEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(CallEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(callEventLabelProvider);
			action.setText(callEventLabelProvider.getText());
			action.setImageDescriptor(callEventLabelProvider.getImageDescriptor());
			return null;
		}
		
		public Object visit(CatchEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(catchEventLabelProvider);
			action.setText(catchEventLabelProvider.getText());
			action.setImageDescriptor(catchEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(EOSEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(eosEventLabelProvider);
			action.setText(eosEventLabelProvider.getText());
			action.setImageDescriptor(eosEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(ExceptionEvent event, Object arg) {
			throw new IllegalStateException("ExceptionEvent has been deprecated.");
		}

		public Object visit(ExitEvent event, Object arg) {
			throw new IllegalStateException("An Exit event does not result in a transaction."); 
		}

		public Object visit(LoadEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(loadEventLabelProvider);
			action.setText(loadEventLabelProvider.getText());
			action.setImageDescriptor(loadEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(NewEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(newEventLabelProvider);
			action.setText(newEventLabelProvider.getText());
			action.setImageDescriptor(newEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(ReturnEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(returnEventLabelProvider);
			action.setText(returnEventLabelProvider.getText());
			action.setImageDescriptor(returnEventLabelProvider.getImageDescriptor());
			return null;
		}

		public Object visit(StartEvent event, Object arg) {
			throw new IllegalStateException("A Start event does not result in a transaction."); 
		}
		
		public Object visit(ThrowEvent event, Object arg) {
			IAction action = (IAction) arg;
			event.export(throwEventLabelProvider);
			action.setText(throwEventLabelProvider.getText());
			action.setImageDescriptor(throwEventLabelProvider.getImageDescriptor());
			return null;
		}
	};
	
	private static AssignEventActionLabelProvider assignEventLabelProvider = new AssignEventActionLabelProvider();
	
	private static CallEventActionLabelProvider callEventLabelProvider = new CallEventActionLabelProvider();
	
	private static CatchEventActionLabelProvider catchEventLabelProvider = new CatchEventActionLabelProvider();

	private static EOSEventActionLabelProvider eosEventLabelProvider = new EOSEventActionLabelProvider();
	
	private static LoadEventActionLabelProvider loadEventLabelProvider = new LoadEventActionLabelProvider();
	
	private static NewEventActionLabelProvider newEventLabelProvider = new NewEventActionLabelProvider();

	private static ReturnEventActionLabelProvider returnEventLabelProvider = new ReturnEventActionLabelProvider();
	
	private static ThrowEventActionLabelProvider throwEventLabelProvider = new ThrowEventActionLabelProvider();
}
