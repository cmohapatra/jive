package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.CustomLineBorder;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class ReplyMessageEditPart extends AbstractConnectionEditPart {

	private static final Border MESSAGE_BREAK_BORDER = new CustomLineBorder(0, 1, 0, 1);
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		Message model = (Message) getModel();
		ExecutionOccurrence targetExecution = model.receiveEvent().containingExecution();
		if (targetExecution instanceof ThreadActivation) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			if (!prefs.getBoolean(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS)) {
				return createLostMessageFigure();
			}
		}
		else if (targetExecution instanceof FilteredMethodActivation) {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			if (contents.isBroken(model)) {
				return createBrokenMessageFigure();
			}
			else {
				return createLostMessageFigure();
			}
		}
		
		return createCompleteMessageFigure();
	}
	
	private IFigure createCompleteMessageFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setLineStyle(SWT.LINE_DASH);
		connection.setForegroundColor(ColorConstants.gray);
		connection.setLineWidth(1);
		PolylineDecoration targetDecoration = new PolylineDecoration();
		targetDecoration.setScale(9, 3);
		connection.setTargetDecoration(targetDecoration);
		addTargetLabel(connection);
		return connection;
	}
	
	private IFigure createBrokenMessageFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setLineStyle(SWT.LINE_DASH);
		connection.setForegroundColor(ColorConstants.gray);
		
		PolylineDecoration targetDecoration = new PolylineDecoration();
		targetDecoration.setScale(9, 3);
		connection.setTargetDecoration(targetDecoration);
		
		RectangleFigure messageBreak = new RectangleFigure();
		messageBreak.setBackgroundColor(ColorConstants.white);
		messageBreak.setOutline(false);
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.x = -1;
		bounds.y = -1;
		bounds.width = 4;
		bounds.height = 13;
		messageBreak.setBounds(bounds);
		messageBreak.setBorder(MESSAGE_BREAK_BORDER);
		connection.add(messageBreak, new MidpointLocator(connection, 0));
		
		addTargetLabel(connection);
		return connection;
	}
	
	private IFigure createLostMessageFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setLineStyle(SWT.LINE_SOLID);
		connection.setForegroundColor(ColorConstants.gray);

		PolylineDecoration targetDecoration = new PolylineDecoration();
		targetDecoration.setScale(9, 3);
		connection.setTargetDecoration(targetDecoration);
		connection.setLineWidth(1);
		
		ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
		targetEndpointLocator.setUDistance(-5);
		targetEndpointLocator.setVDistance(0);
		Ellipse circle = new Ellipse();
		circle.setBackgroundColor(ColorConstants.lightGray);
		circle.setLineWidth(1);
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.x = -1;
		bounds.y = -1;
		bounds.width = 11;
		bounds.height = 11;
		circle.setBounds(bounds);
		connection.add(circle, targetEndpointLocator);
		addTargetLabel(connection);
		
		return connection;
	}
	
	private void addTargetLabel(Connection connection) {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		Message message = (Message) getModel();
		ExecutionOccurrence execution = message.sendEvent().containingExecution();
		if (contents.isCollapsed(execution)) {
			Label label = new Label("+");
			label.setForegroundColor(ColorConstants.gray);
			
			ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
			targetEndpointLocator.setUDistance(5);
			targetEndpointLocator.setVDistance(-4);
			connection.add(label, targetEndpointLocator);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
	}
}
