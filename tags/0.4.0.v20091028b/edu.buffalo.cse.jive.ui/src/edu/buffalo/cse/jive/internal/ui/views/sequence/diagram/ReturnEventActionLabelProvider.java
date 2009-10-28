package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class ReturnEventActionLabelProvider extends AbstractActionLabelProvider implements ReturnEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Return";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_RETURN_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the returning context.
	 */
	private String returner;
	
	/**
	 * The string representation of the return value.
	 */
	private String value;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventDetails()
	 */
	@Override
	protected String getEventDetails() {
		return value + " from " + returner;
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
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addPreviousContext(edu.bsu.cs.jive.events.ReturnEvent.Returner)
	 */
	public void addPreviousContext(Returner returner) {
		this.returner = returner.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addReturnValue(edu.bsu.cs.jive.contour.Value)
	 */
	public void addReturnValue(Value value) {
		if (value != null) {
			this.value = value.toString();
		}
		else {
			this.value = "<void>";
		}
	}
}
