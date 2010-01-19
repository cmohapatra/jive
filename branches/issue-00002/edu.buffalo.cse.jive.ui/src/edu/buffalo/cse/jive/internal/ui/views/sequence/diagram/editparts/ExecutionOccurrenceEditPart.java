package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.MethodContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.ExecutionOccurrenceFigure;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.LifelineFigure;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * 
 * @author Jeffrey K Czyz
 * @author Nirupama Chakravarti
 */
public class ExecutionOccurrenceEditPart extends AbstractGraphicalEditPart implements NodeEditPart {
	
	public ExecutionOccurrence getModel() {
		return (ExecutionOccurrence) super.getModel();
	}
	
	// TODO Add start and end to execution occurrences the re-write this
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		ExecutionOccurrence execution = getModel();
		
		int start = determineStartPosition(execution);
		int length = contents.calculateExecutionLength(execution);
		Color color = determineThreadColor(execution);
		ContourFigure.Importer importer = createImporter(execution);
		
		return new ExecutionOccurrenceFigure(importer, start, length - 1, color);
	}
	
	private int determineStartPosition(ExecutionOccurrence execution) {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		if (execution.events().isEmpty()) {
			long lastEventNumber = execution.containingModel().lastEventNumber();
			return contents.calculateAdjustedEventNumber(lastEventNumber);
		}
		else {
			return contents.calculateExecutionStart(execution);
		}
	}
	
	private Color determineThreadColor(ExecutionOccurrence execution) {
		IThreadColorManager manager = JiveUIPlugin.getDefault().getThreadColorManager();
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		IJiveDebugTarget target = contents.getModel();
		
		if (execution instanceof ThreadActivation) {
			ThreadActivation activation = (ThreadActivation) execution;
			return manager.threadColor(target, activation.thread());
		}
		else {
			ThreadID thread = execution.initiator().underlyingEvent().thread();
			return manager.threadColor(target, thread);
		}
	}
	
	private ContourFigure.Importer createImporter(ExecutionOccurrence execution) {
		if (execution instanceof ThreadActivation) {
			ThreadActivation activation = (ThreadActivation) execution;
			ThreadID thread = activation.thread();
			final String context = thread.getName() + " (id = " + thread.getId() + ")";
			return new ContourFigure.Importer() {

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
			};
		}
		else {
			return new MethodContourFigureBuilder(execution.id());
		}
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
		// TODO Determine what other edit policies should be added, if any
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List<Message> getModelSourceConnections() {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		ExecutionOccurrence execution = getModel();
		return contents.getSourceMessages(execution);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List<Message> getModelTargetConnections() {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		ExecutionOccurrence execution = getModel();
		return contents.getTargetMessages(execution);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List<EventOccurrence> getModelChildren() {
		SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
		ExecutionOccurrence execution = getModel();
		return contents.getSearchResults(execution);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		LifelineEditPart parent = (LifelineEditPart) getParent();
		LifelineFigure parentFigure = (LifelineFigure) parent.getFigure();
		
		ExecutionOccurrence execution = getModel();
		ExecutionOccurrenceFigure figure = (ExecutionOccurrenceFigure) getFigure();
		
		Dimension headDimension = parentFigure.getLifelineHeadSize();
		
		int width = JiveUIPlugin.getDefault().getActivationWidth();
		int eventHeight = JiveUIPlugin.getDefault().getEventHeight();
		
		int x = (width + 3) * parent.binNumber(execution) + (headDimension.width / 2) - (width / 2);
		int y = eventHeight * figure.getStart() + headDimension.height + 10;
		
		Rectangle constraint = new Rectangle(x, y, -1, -1);
		parent.setLayoutConstraint(this, figure, constraint);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart connection) {
		if (connection instanceof SynchCallMessageEditPart) {
			return new SyncCallSourceConnectionAnchor(getFigure());
		}
		else if (connection instanceof ReplyMessageEditPart) {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			Message message = (Message) connection.getModel();
			if (contents.isBroken(message)) {
				return new BrokenReplySourceConnectionAnchor(getFigure(), contents, message);
			}
			else {
				return new ReplySourceConnectionAnchor(getFigure());
			}
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
	public ConnectionAnchor getTargetConnectionAnchor(final ConnectionEditPart connection) {
		if (connection instanceof SynchCallMessageEditPart) {
			SequenceDiagramEditPart contents = (SequenceDiagramEditPart) getRoot().getContents();
			Message message = (Message) connection.getModel();
			if (contents.isBroken(message)) {
				return new BrokenSyncCallTargetConnectionAnchor(getFigure(), contents, message);
			}
			else {
				return new SyncCallTargetConnectionAnchor(getFigure());
			}
		}
		else if (connection instanceof ReplyMessageEditPart) {
			return new ReplyTargetConnectionAnchor(getFigure());
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
}

/**
 * A source connection anchor for a SyncCall message.  The anchor is placed at
 * the point where the call was made.
 * 
 * @author Jeffrey K Czyz
 */
class SyncCallSourceConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * Creates a new source connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 */
	public SyncCallSourceConnectionAnchor(IFigure owner) {
		super(owner);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		
		Point result = bounds.getTopRight();
		result.y = reference.y;
		return result;
	}
}

/**
 * A target connection anchor for a SyncCall message.  The anchor is placed at
 * the top center of the target.  The reference point is the same point as the
 * anchor's location.
 * 
 * @author Jeffrey K Czyz
 */
class SyncCallTargetConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * Creates a new target connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 */
	public SyncCallTargetConnectionAnchor(IFigure owner) {
		super(owner);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		return bounds.getTop();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getReferencePoint()
	 */
	public Point getReferencePoint() {
		Point result = getBox().getTop();
		getOwner().translateToAbsolute(result);
		return result;
	}
}

/**
 * A target connection anchor for a broken SyncCall message.  The anchor is
 * placed at the top center of the target.  The reference point is located at
 * the same point as the anchor only shifted upward so that the line is angled.
 * 
 * @author Jeffrey K Czyz
 */
class BrokenSyncCallTargetConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * The edit part of the sequence diagram containing the connection.
	 */
	private SequenceDiagramEditPart contents;
	
	/**
	 * The model object for the connection.
	 */
	private Message message;
	
	/**
	 * Creates a new target connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 * @param contents the sequence diagram's edit part
	 * @param message the connection's model object
	 */
	public BrokenSyncCallTargetConnectionAnchor(IFigure owner, SequenceDiagramEditPart contents, Message message) {
		super(owner);
		this.contents = contents;
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		return bounds.getTop();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getReferencePoint()
	 */
	public Point getReferencePoint() {
		Point result = getBox().getTop();
		
		// Calculate the vertical offset from the source to the target
		long start = contents.getFilteredMessageEventNumber(message);
		start = contents.calculateAdjustedEventNumber(start);
		long end = message.receiveEvent().underlyingEvent().number();
		end = contents.calculateAdjustedEventNumber(end);
		int offset = ((int) (end - start)) * JiveUIPlugin.getDefault().getEventHeight();
		
		result.y = result.y - offset;
		getOwner().translateToAbsolute(result);
		return result;
	}
}

/**
 * A source connection anchor for a Reply message.  The anchor is placed on the
 * bottom left corner of the source.  The reference point is the same point as
 * the anchor's location.
 * 
 * @author Jeffrey K Czyz
 */
class ReplySourceConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * Creates a new target connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 */
	public ReplySourceConnectionAnchor(IFigure owner) {
		super(owner);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference){
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		return bounds.getBottomLeft();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getReferencePoint()
	 */
	public Point getReferencePoint() {
		Point result = getBox().getBottomLeft();
		getOwner().translateToAbsolute(result);
		return result;
	}
}

/**
 * A source connection anchor for a broken Reply message.  The anchor is placed
 * on the bottom left corner of the source.  The reference point is located at
 * the same point as the anchor only shifted downward so that the line is
 * angled.
 * 
 * @author Jeffrey K Czyz
 */
class BrokenReplySourceConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * The edit part of the sequence diagram containing the connection.
	 */
	private SequenceDiagramEditPart contents;
	
	/**
	 * The model object for the connection.
	 */
	private Message message;
	
	/**
	 * Creates a new source connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 * @param contents the sequence diagram's edit part
	 * @param message the connection's model object
	 */
	public BrokenReplySourceConnectionAnchor(IFigure owner, SequenceDiagramEditPart contents, Message message) {
		super(owner);
		this.contents = contents;
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference){
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		return bounds.getBottomLeft();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getReferencePoint()
	 */
	public Point getReferencePoint() {
		Point result = getBox().getBottomLeft();

		// Calculate the vertical offset from the source to the target
		long start = message.sendEvent().underlyingEvent().number();
		start = contents.calculateAdjustedEventNumber(start);
		long end = contents.getFilteredMessageEventNumber(message);
		end = contents.calculateAdjustedEventNumber(end);
		int offset = ((int) (end - start)) * JiveUIPlugin.getDefault().getEventHeight();

		result.y = result.y + offset;
		getOwner().translateToAbsolute(result);
		return result;
	}
}

/**
 * A target connection anchor for a Reply message.  The anchor is placed at the
 * point were the reply was received.
 * 
 * @author Jeffrey K Czyz
 */
class ReplyTargetConnectionAnchor extends ChopboxAnchor {
	
	/**
	 * Creates a new target connection anchor for the given owner figure.
	 * 
	 * @param owner the owner figure
	 */
	public ReplyTargetConnectionAnchor(IFigure owner) {
		super(owner);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ChopboxAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference){
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getBox());
		getOwner().translateToAbsolute(bounds);
		
		Point result = bounds.getTopRight();
		result.y = reference.y;
		return result;
	}
}