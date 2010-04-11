package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.jface.resource.ImageDescriptor;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.views.IJiveActionLabelProvider;

public abstract class AbstractActionLabelProvider implements IJiveActionLabelProvider, Event.Exporter {

	public ImageDescriptor getImageDescriptor() {
		return getEventImageDescriptor();
	}

	public String getText() {
		return getEventName() + " " + getEventDetails();
	}

	/**
	 * Returns the string representation of the event name.
	 * 
	 * @return the event name
	 */
	protected abstract String getEventName();
	
	/**
	 * Returns the image representation of the event.
	 * 
	 * @return the event image
	 */
	protected abstract ImageDescriptor getEventImageDescriptor();
	
	/**
	 * Returns the details of the event as a string.  The details are dependent
	 * on the event type.
	 * 
	 * @return the event details.
	 */
	protected abstract String getEventDetails();
	
	public void addNumber(long n) {}

	public void addThreadID(ThreadID thread) {}
}
