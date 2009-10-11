package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class ThrowEventActionLabelProvider extends AbstractActionLabelProvider implements ThrowEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Throw";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_THROW_EVENT_ICON_KEY);
	}

	/**
	 * The string representation of the thrown exception.
	 */
	private String exception;
	
	/**
	 * The string representation of the context where the exception was thrown.
	 */
	private String thrower;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventDetails()
	 */
	@Override
	protected String getEventDetails() {
		return exception + " (" + thrower + ")";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventImageDescriptor()
	 */
	protected ImageDescriptor getEventImageDescriptor() {
		return EVENT_IMAGE_DESCRIPTOR;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventName()
	 */
	protected String getEventName() {
		return EVENT_NAME;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addThrower(edu.bsu.cs.jive.events.ThrowEvent.Thrower)
	 */
	public void addThrower(Thrower thrower) {
		this.thrower = "thrower:  " + thrower.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
	 */
	public void addException(Value exception) {
		this.exception = exception.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addFramePopped(boolean)
	 */
	public void addFramePopped(boolean framePopped) {
		// do nothing
	}
}
