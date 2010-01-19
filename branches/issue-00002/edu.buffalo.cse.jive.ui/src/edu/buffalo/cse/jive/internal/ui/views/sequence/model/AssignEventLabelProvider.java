package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide labels for an <code>AssignEvent</code> to
 * be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class AssignEventLabelProvider extends AbstractEventLabelProvider implements AssignEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Assign Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_ASSIGN_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the context where the assignment took place. 
	 */
	private String fContext;
	
	/**
	 * The string representation of the variable being assigned the value.
	 */
	private String fVariable;
	
	/**
	 * The string representation of the value assigned to the variable.
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
		return "context = " + fContext + ", variable = " + fVariable + ", value = " + fValue;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addContourID(ContourID contourID) {
		fContext = contourID.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addNewValue(edu.bsu.cs.jive.contour.Value)
	 */
	public void addNewValue(Value v) {
		fValue = v.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addVariableID(edu.bsu.cs.jive.util.VariableID)
	 */
	public void addVariableID(VariableID variableID) {
		fVariable = variableID.toString();
	}
}
