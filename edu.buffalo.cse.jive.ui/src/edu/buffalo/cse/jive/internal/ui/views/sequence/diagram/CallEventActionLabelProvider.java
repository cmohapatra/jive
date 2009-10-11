package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class CallEventActionLabelProvider extends AbstractActionLabelProvider implements CallEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Call";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_CALL_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the context in which the call takes place.
	 */
	private String target;
	
	/**
	 * The string representation of the actual parameters of the call.
	 */
	private String actuals;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventDetails()
	 */
	@Override
	protected String getEventDetails() {
		return target + "(" + actuals + ")";
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getEventImageDescriptor() {
		return EVENT_IMAGE_DESCRIPTOR;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventName()
	 */
	@Override
	protected String getEventName() {
		return EVENT_NAME;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addActualParams(java.util.List)
	 */
	public void addActualParams(List<Value> actuals) {
		String temp = actuals.toString();
		temp = temp.substring(1, temp.length() - 1);
		this.actuals = temp;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addCaller(edu.bsu.cs.jive.events.CallEvent.Caller)
	 */
	public void addCaller(Caller caller) {}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addTarget(edu.bsu.cs.jive.events.CallEvent.Target)
	 */
	public void addTarget(Target target) {
		this.target = target.toString();
	}
}
