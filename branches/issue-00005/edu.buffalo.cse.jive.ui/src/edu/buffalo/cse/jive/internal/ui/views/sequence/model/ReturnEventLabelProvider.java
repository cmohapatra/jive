package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide labels for a <code>ReturnEvent</code> to
 * be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class ReturnEventLabelProvider extends AbstractEventLabelProvider implements ReturnEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Return Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_RETURN_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the returning context.
	 */
	private String fReturner;
	
	/**
	 * The string representation of the return value.
	 */
	private String fValue;
	
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
		return "returner = " + fReturner + ", value = " + fValue;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addPreviousContext(edu.bsu.cs.jive.events.ReturnEvent.Returner)
	 */
	public void addPreviousContext(Returner returner) {
		fReturner = returner.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addReturnValue(edu.bsu.cs.jive.contour.Value)
	 */
	public void addReturnValue(Value value) {
		if (value != null) {
			fValue = value.toString();
		}
		else {
			fValue = "<void>";
		}
	}
}
