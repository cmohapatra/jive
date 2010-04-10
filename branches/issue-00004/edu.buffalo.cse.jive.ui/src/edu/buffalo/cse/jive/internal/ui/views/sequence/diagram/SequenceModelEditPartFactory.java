package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.EventOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.ExecutionOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.LifelineEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.ReplyMessageEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.SequenceDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.SynchCallMessageEditPart;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;

public class SequenceModelEditPartFactory implements EditPartFactory {

	private static final EditPart EMPTY_EDIT_PART = new AbstractGraphicalEditPart() {
		
		/* (non-Javadoc)
		 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
		 */
		protected IFigure createFigure() {
			return new Figure();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
		 */
		protected void createEditPolicies() {
			// TODO Determine if this should be implemented
		}
	};

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		if (model == null) {
			return EMPTY_EDIT_PART;
		}
		
		if (model instanceof IJiveDebugTarget) {
			EditPart editPart = new SequenceDiagramEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof ContourID) {
			EditPart editPart = new LifelineEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof ThreadID) {
			EditPart editPart = new LifelineEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof ExecutionOccurrence) {
			EditPart editPart = new ExecutionOccurrenceEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof EventOccurrence) {
			EditPart editPart = new EventOccurrenceEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof Message) {
			Message message = (Message) model;
			EditPart editPart;
			switch (message.messageSort()) {
			case SYNCH_CALL:
				editPart = new SynchCallMessageEditPart();
				editPart.setModel(model);
				return editPart;
			case REPLY:
				editPart = new ReplyMessageEditPart();
				editPart.setModel(model);
				return editPart;
			default:
				throw new IllegalArgumentException("Message sort " + message.messageSort() + " is not supported.");
			}
		}
		
		throw new IllegalArgumentException("Unknown element type:  " + model.getClass());
	}
}
