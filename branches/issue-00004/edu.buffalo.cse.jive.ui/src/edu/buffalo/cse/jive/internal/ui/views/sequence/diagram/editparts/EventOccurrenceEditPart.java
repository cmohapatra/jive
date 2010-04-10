package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.events.Event;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.EventOccurrenceFigure;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.ExecutionOccurrenceFigure;
import edu.buffalo.cse.jive.internal.ui.views.sequence.model.SequenceModelView;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class EventOccurrenceEditPart extends AbstractGraphicalEditPart {

	public EventOccurrence getModel() {
		return (EventOccurrence) super.getModel();
	}
	
	protected IFigure createFigure() {
		final EventOccurrence model = getModel();
		
		return new EventOccurrenceFigure(new EventOccurrenceFigure.Importer() {

			public Image provideToolTipIcon() {
				return labelProvider.getImage(model);
			}

			public String provideToolTipText() {
				return labelProvider.getText(model);
			}
			
		});
	}
	
	private SequenceModelView.SequenceModelLabelProvider labelProvider = new SequenceModelView.SequenceModelLabelProvider();

	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
		// TODO Determine what other edit policies should be added, if any
	}

	@Override
	protected void refreshVisuals() {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		EventOccurrence event = getModel();
		ExecutionOccurrenceEditPart parent = (ExecutionOccurrenceEditPart) getParent();
		ExecutionOccurrenceFigure parentFigure = (ExecutionOccurrenceFigure) parent.getFigure();
		EventOccurrenceFigure figure = (EventOccurrenceFigure) getFigure();
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		int eventHeight = prefs.getInt(IJiveUIConstants.PREF_EVENT_HEIGHT);
		long eventNumber = event.underlyingEvent().number();
		int eventPosition = contents.calculateAdjustedEventNumber(eventNumber);
		
		int y = eventHeight * (eventPosition - parentFigure.getStart());
		Rectangle constraint = new Rectangle(0, y, -1, -1);
		parent.setLayoutConstraint(this, figure, constraint);
	}
}
