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
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.CustomLineBorder;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class SynchCallMessageEditPart extends AbstractConnectionEditPart {

	private static final Border MESSAGE_BREAK_BORDER = new CustomLineBorder(0, 1, 0, 1);
	
	protected IFigure createFigure() {
		Message model = (Message) getModel();
		ExecutionOccurrence sourceExecution = model.sendEvent().containingExecution();
		if (sourceExecution instanceof ThreadActivation) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			if (!prefs.getBoolean(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS)) {
				return createFoundMessageFigure();
			}
		}
		else if (sourceExecution instanceof FilteredMethodActivation) {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			if (contents.isBroken(model)) {
				return createBrokenMessageFigure();
			}
			else {
				return createFoundMessageFigure();
			}
		}
		
		return createCompleteMessageFigure();
	}
	
	private PolylineConnection createCompleteMessageFigure() {
		PolylineConnection connection = createConnection();
		PolygonDecoration targetDecoration = new PolygonDecoration();
		targetDecoration.setScale(9, 3);
		connection.setTargetDecoration(targetDecoration);
		addTargetLabel(connection);
		return connection;
	}
	
	private PolylineConnection createBrokenMessageFigure() {
		PolylineConnection connection = createConnection();
		
		PolygonDecoration targetDecoration = new PolygonDecoration();
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
	
	private PolylineConnection createFoundMessageFigure() {
		PolylineConnection connection = createConnection();

		Ellipse circle = new Ellipse();
		circle.setBackgroundColor(ColorConstants.lightGray);
		circle.setLineWidth(1);
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.x = -1;
		bounds.y = -1;
		bounds.width = 11;
		bounds.height = 11;
		circle.setBounds(bounds);
		ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(connection, false);
		sourceEndpointLocator.setUDistance(-5);
		sourceEndpointLocator.setVDistance(0);
		connection.add(circle, sourceEndpointLocator);
		
		PolylineDecoration targetDecoration = new PolylineDecoration();
		targetDecoration.setScale(9, 3);
		connection.setTargetDecoration(targetDecoration);
		addTargetLabel(connection);
		
		return connection;
	}
	
	private PolylineConnection createConnection() {
		PolylineConnection result = new PolylineConnection();
		result.setForegroundColor(ColorConstants.gray);
		result.setLineWidth(1);
		return result;
	}
	
	private void addTargetLabel(Connection connection) {
		Message message = (Message) getModel();
		ExecutionOccurrence execution = message.receiveEvent().containingExecution();
		
		Label label = new Label(determineTargetLabel(execution));
		label.setForegroundColor(ColorConstants.black);
		
		ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
		targetEndpointLocator.setUDistance(-1);
		targetEndpointLocator.setVDistance(-2);
		connection.add(label, targetEndpointLocator);

		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		if (contents.isCollapsed(execution)) {
			label = new Label("+");
			label.setForegroundColor(ColorConstants.gray);
			
			targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
			targetEndpointLocator.setUDistance(-16);
			targetEndpointLocator.setVDistance(-20);
			connection.add(label, targetEndpointLocator);
		}
	}
	
	private String determineTargetLabel(ExecutionOccurrence execution) {
		if (execution instanceof FilteredMethodActivation) {
			return "";
		}
		else {
			ContourID id = execution.id();
			String[] tokens = id.toString().split("#");
			return tokens[1];
		}
	}
	
	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
	}
}
