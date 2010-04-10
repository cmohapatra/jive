package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchActionConstants;

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
import edu.buffalo.cse.jive.internal.ui.views.contour.RunToEventAction;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.EventOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.ExecutionOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.ReplyMessageEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.SequenceDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.SynchCallMessageEditPart;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.JiveUITools;

public class SequenceDiagramContextMenuProvider extends ContextMenuProvider {

	public SequenceDiagramContextMenuProvider(EditPartViewer viewer) {
		super(viewer);
	}
	
	@Override
	public void buildContextMenu(IMenuManager manager) {
		EditPartViewer viewer = getViewer();
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (!selection.isEmpty()) {
			// TODO Change to use either the primary selection (last in list) or all elements
			EditPart part = (EditPart) selection.getFirstElement();
			
			if (part instanceof ExecutionOccurrenceEditPart) {
				ExecutionOccurrence execution = (ExecutionOccurrence) part.getModel();
				buildContextMenuFor(execution, manager);
			}
			else if (part instanceof EventOccurrenceEditPart) {
				EventOccurrence event = (EventOccurrence) part.getModel();
				buildContextMenuFor(event, manager);
			}
			else if (part instanceof SynchCallMessageEditPart ||
					part instanceof ReplyMessageEditPart) {
				Message message = (Message) part.getModel();
				buildContextMenuFor(message, manager);
			}
		}
		
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void buildContextMenuFor(ExecutionOccurrence execution, IMenuManager manager) {
		SequenceModel model = execution.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			RootEditPart root = getViewer().getRootEditPart();
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
			IJiveDebugTarget target = contents.getModel();
			
			manager.add(new CollapseCallTreeAction(execution));
			manager.add(new ExpandCallTreeAction(execution));
			
			IMenuManager jumpToMenu = new MenuManager("Jump to");
			manager.add(jumpToMenu);
			for (EventOccurrence event : execution.events()) {
				Event underlyingEvent = event.underlyingEvent();
				Object result = underlyingEvent.accept(underlyingEventVisitor, target);
				if (result != null) {
					jumpToMenu.add((IAction) result);
				}
			}
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private void buildContextMenuFor(EventOccurrence event, IMenuManager manager) {
		RootEditPart root = getViewer().getRootEditPart();
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
		IJiveDebugTarget target = contents.getModel();
		
		Event underlyingEvent = event.underlyingEvent();
		Object result = underlyingEvent.accept(underlyingEventVisitor, target);
		if (result != null) {
			manager.add((IAction) result);					
		}
	}
	
	private void buildContextMenuFor(Message message, IMenuManager manager) {
		RootEditPart root = getViewer().getRootEditPart();
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
		IJiveDebugTarget target = contents.getModel();
		
		Event underlyingEvent = message.sendEvent().underlyingEvent();
		Object result = underlyingEvent.accept(underlyingEventVisitor, target);
		
		if (result != null) {
			manager.add((IAction) result);					
		}
	}
	
	protected class CollapseCallTreeAction extends Action {
		
		private ExecutionOccurrence execution;
		
		public CollapseCallTreeAction(ExecutionOccurrence execution) {
			super("Collapse");
			this.execution = execution;

			setImageDescriptor(JiveUITools.getImageDescriptor(IJiveUIConstants.ENABLED_COLLAPSE_ALL_ICON_KEY));
			setDisabledImageDescriptor(JiveUITools.getImageDescriptor(IJiveUIConstants.DISABLED_COLLAPSE_ALL_ICON_KEY));
			
			RootEditPart root = getViewer().getRootEditPart();
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
			setEnabled(execution.isTerminated() && !contents.isCollapsed(execution));
		}
		
		public void run() {
			RootEditPart root = getViewer().getRootEditPart();
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
			contents.collapseExecution(execution);
		}
	}
	
	protected class ExpandCallTreeAction extends Action {
		
		private ExecutionOccurrence execution;
		
		public ExpandCallTreeAction(ExecutionOccurrence execution) {
			super("Expand");
			this.execution = execution;

			setImageDescriptor(JiveUITools.getImageDescriptor(IJiveUIConstants.ENABLED_EXPAND_ALL_ICON_KEY));
			setDisabledImageDescriptor(JiveUITools.getImageDescriptor(IJiveUIConstants.DISABLED_EXPAND_ALL_ICON_KEY));
			
			RootEditPart root = getViewer().getRootEditPart();
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
			setEnabled(execution.isTerminated() && contents.isCollapsed(execution));
		}
		
		public void run() {
			RootEditPart root = getViewer().getRootEditPart();
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) root.getContents();
			contents.expandExecution(execution);
		}
	}
	
	private Event.Visitor underlyingEventVisitor = new Event.Visitor() {

		public Object visit(AssignEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}

		public Object visit(CallEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}

		public Object visit(CatchEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}
		
		public Object visit(EOSEvent event, Object arg) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(ExceptionEvent event, Object arg) {
			throw new IllegalStateException("ExceptionEvent has been deprecated.");
		}

		public Object visit(ExitEvent event, Object arg) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object visit(LoadEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}

		public Object visit(NewEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}

		public Object visit(ReturnEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}

		public Object visit(StartEvent event, Object arg) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public Object visit(ThrowEvent event, Object arg) {
			IJiveDebugTarget target = (IJiveDebugTarget) arg;
			return new RunToEventAction(target, event);
		}
	};

}
