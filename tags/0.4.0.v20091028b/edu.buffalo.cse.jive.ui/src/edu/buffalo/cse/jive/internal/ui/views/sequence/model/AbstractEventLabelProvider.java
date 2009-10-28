package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider;

/**
 * An abstract event exporter used to provide labels to an
 * <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public abstract class AbstractEventLabelProvider implements IJiveLabelProvider, Event.Exporter {
	
	/**
	 * The event number representation. 
	 */
	private String fEventNumber;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider#getText()
	 */
	public String getText() {
		return getEventName() + " (number = " + getEventNumber() + ", " + getEventDetails() + ")";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider#getImage()
	 */
	public Image getImage() {
		return getEventImage();
	}
	
	/**
	 * Returns the string representation of the event number.
	 * 
	 * @return the event number
	 */
	protected String getEventNumber() {
		return fEventNumber;
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
	protected abstract Image getEventImage();
	
	/**
	 * Returns the details of the event as a string.  The details are dependent
	 * on the event type.
	 * 
	 * @return the event details.
	 */
	protected abstract String getEventDetails();
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
	 */
	public void addNumber(long n) {
		fEventNumber = Long.toString(n);
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
	 */
	public void addThreadID(ThreadID thread) {
		// do nothing
	}
}
