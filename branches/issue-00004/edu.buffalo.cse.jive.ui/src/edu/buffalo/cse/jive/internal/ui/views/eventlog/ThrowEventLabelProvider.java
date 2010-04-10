package edu.buffalo.cse.jive.internal.ui.views.eventlog;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide column labels for a <code>ThrowEvent</code>
 * to be used by an <code>ITableLabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class ThrowEventLabelProvider extends AbstractEventLabelProvider implements ThrowEvent.Exporter {
	
	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Throw Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_THROW_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the thrown exception.
	 */
	protected String fException;
	
	/**
	 * The string representation of the context where the exception was thrown.
	 */
	protected String fThrower;
	
	/**
	 * The string representation of the boolean specifying if a stack frame was popped.
	 */
	protected String fFramePopped;

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
		return "exception = " + fException + ", thrower = " + fThrower + ", framePopped = " + fFramePopped;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addThrower(edu.bsu.cs.jive.events.ThrowEvent.Thrower)
	 */
	public void addThrower(Thrower thrower) {
		fThrower = thrower.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
	 */
	public void addException(Value exception) {
		fException = exception.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addFramePopped(boolean)
	 */
	public void addFramePopped(boolean framePopped) {
		fFramePopped = Boolean.toString(framePopped);
	}
}
