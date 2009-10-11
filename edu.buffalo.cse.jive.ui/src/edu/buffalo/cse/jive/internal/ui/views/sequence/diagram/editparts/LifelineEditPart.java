package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.InstanceContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.StaticContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.LifelineFigure;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class LifelineEditPart extends AbstractGraphicalEditPart implements NodeEditPart {

	private Map<ExecutionOccurrence, Integer> executionToBinMap = new HashMap<ExecutionOccurrence, Integer>();
	
	private List<ExecutionOccurrence> lastExecutionInBin = new ArrayList<ExecutionOccurrence>(2);
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Object model = getModel();
		if (model instanceof ThreadID) {
			ThreadID thread = (ThreadID) model;
			final String context = thread.getName() + " (id = " + thread.getId() + ")";
			return new LifelineFigure(new ContourFigure.Importer() {

				public Image provideIcon() {
					ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
					return registry.get(IJiveUIConstants.ENABLED_THREAD_ACTIVATION_ICON_KEY);
				}

				public String provideText() {
					return context;
				}

				public Image provideToolTipIcon() {
					ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
					return registry.get(IJiveUIConstants.ENABLED_THREAD_ACTIVATION_ICON_KEY);
				}

				public String provideToolTipText() {
					return context;
				}
			});
		}
		else {
			ContourID id = (ContourID) getModel();
			// TODO Determine if there is a better way to differentiate these
			if (id.toString().indexOf(':') == -1) {
				return new LifelineFigure(new StaticContourFigureBuilder(id));
			}
			else {
				return new LifelineFigure(new InstanceContourFigureBuilder(id));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<ExecutionOccurrence> getModelChildren() {
		List<ExecutionOccurrence> result;
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		Object model = getModel();
		if (model instanceof ThreadID) {
			ThreadID id = (ThreadID) model;
			result = contents.getLifelineChildren(id);
			executionToBinMap.put(result.get(0), 0);
		}
		else {
			ContourID id = (ContourID) getModel();
			result = contents.getLifelineChildren(id);
			updateBinMapping(result);
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	@Override
	protected List<Message> getModelSourceConnections() {
		Object model = getModel();
		if (model instanceof ThreadID) {
			return Collections.emptyList();
		}
		else {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			ContourID id = (ContourID) getModel();
			return contents.getFoundMessages(id);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	@Override
	protected List<Message> getModelTargetConnections() {
		Object model = getModel();
		if (model instanceof ThreadID) {
			return Collections.emptyList();
		}
		else {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			ContourID id = (ContourID) getModel();
			return contents.getLostMessages(id);
		}
	}
	
	private void updateBinMapping(List<ExecutionOccurrence> executionList) {
		executionToBinMap.clear();
		lastExecutionInBin.clear();
		for (ExecutionOccurrence execution : executionList) {
			assignBin(execution);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart connection) {
		// Found message
		if (connection instanceof SynchCallMessageEditPart) {
			return new ChopboxAnchor(getFigure()) {
				public Point getLocation(Point reference) {
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(getBox());
					getOwner().translateToAbsolute(bounds);
					
					Point result = bounds.getTopLeft();
					result.y = reference.y;
					return result;
				}
			};
		}
		else {
			throw new IllegalArgumentException("Unsupported connection:  " + connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		// Lost message
		if (connection instanceof ReplyMessageEditPart) {
			return new ChopboxAnchor(getFigure()) {
				public Point getLocation(Point reference){
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(getBox());
					getOwner().translateToAbsolute(bounds);

					Point result = bounds.getTopRight();
					result.y = reference.y;
					return result;
				}
			};
		}
		else {
			throw new IllegalArgumentException("Unsupported connection:  " + connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		throw new UnsupportedOperationException();
	}
	
	private void assignBin(ExecutionOccurrence execution) {
		long executionStart = execution.initiator().underlyingEvent().number();
		int binCount = lastExecutionInBin.size();
		for (int bin = 0; bin < binCount; bin++) {
			ExecutionOccurrence lastExecution = lastExecutionInBin.get(bin);
			long lastExecutionEnd =
				lastExecution.initiator().underlyingEvent().number() +
				lastExecution.duration() - 1;
			if (executionStart > lastExecutionEnd) {
				executionToBinMap.put(execution, bin);
				lastExecutionInBin.set(bin, execution);
				return;
			}
		}
		
		executionToBinMap.put(execution, binCount);
		lastExecutionInBin.add(execution);
	}
	
	int binNumber(ExecutionOccurrence execution) {
		if (executionToBinMap.containsKey(execution)) {
			return executionToBinMap.get(execution);
		}
		else {
			throw new IllegalStateException("Bin numbers have not been assigned yet.");
		}
	}
}
