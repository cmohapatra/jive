package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.events.EOSEvent;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide labels for an <code>EOSEvent</code> to be
 * used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class EOSEventLabelProvider extends AbstractEventLabelProvider implements EOSEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "EOS Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_EOS_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the file where the event occurred.
	 */
	private String fFile;
	
	/**
	 * The string representation of the line number within the file where the
	 * event occurred.
	 */
	private String fLine;

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.AbstractEventLabelProvider#getEventName()
	 */
	protected String getEventName() {
		return EVENT_NAME;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.AbstractEventLabelProvider#getEventImage()
	 */
	protected Image getEventImage() {
		return EVENT_IMAGE;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.AbstractEventLabelProvider#getEventDetails()
	 */
	protected String getEventDetails() {
		return "file = " + fFile + ", line = " + fLine;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EOSEvent.Exporter#addFilename(java.lang.String)
	 */
	public void addFilename(String filename) {
		fFile = filename;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EOSEvent.Exporter#addLineNumber(int)
	 */
	public void addLineNumber(int lineNumber) {
		fLine = Integer.toString(lineNumber);
	}
}
