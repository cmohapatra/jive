package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide labels for a <code>CallEvent</code> to be
 * used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class CallEventLabelProvider extends AbstractEventLabelProvider implements CallEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Call Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_CALL_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the context in which the call takes place.
	 */
	private String fTarget;
	
	/**
	 * The string representation of the actual parameters of the call.
	 */
	private String fActuals;
	
	/**
	 * The string representation of the calling context.
	 */
	private String fCaller;
	
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
		return "target = " + fTarget + ", actuals = " + fActuals + ", caller = " + fCaller;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addActualParams(java.util.List)
	 */
	public void addActualParams(List<Value> actuals) {
		fActuals = actuals.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addCaller(edu.bsu.cs.jive.events.CallEvent.Caller)
	 */
	public void addCaller(Caller caller) {
		fCaller = caller.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addTarget(edu.bsu.cs.jive.events.CallEvent.Target)
	 */
	public void addTarget(Target target) {
		fTarget = target.toString();
	}
}
