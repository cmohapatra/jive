package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class CatchEventActionLabelProvider extends AbstractActionLabelProvider implements CatchEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Catch";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_CATCH_EVENT_ICON_KEY);
	}

	/**
	 * The string representation of the caught exception.
	 */
	private String exception;
	
	/**
	 * The string representation of the context where the exception was caught.
	 */
	private String catcher;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventDetails()
	 */
	@Override
	protected String getEventDetails() {
		return exception + " (" + catcher + ")";
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
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addCatcher(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addCatcher(ContourID catcher) {
		this.catcher = "catcher:  " + (catcher == null ? "<uncaught>" :catcher.toString());
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
	 */
	public void addException(Value exception) {
		this.exception = exception.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addVariable(edu.bsu.cs.jive.util.VariableID)
	 */
	public void addVariable(VariableID v) {}
}
