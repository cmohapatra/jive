package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class AssignEventActionLabelProvider extends AbstractActionLabelProvider implements AssignEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Assign";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_ASSIGN_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the context where the assignment took place. 
	 */
	private String context;
	
	/**
	 * The string representation of the variable being assigned the value.
	 */
	private String variable;
	
	/**
	 * The string representation of the value assigned to the variable.
	 */
	private String value;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.AbstractActionLabelProvider#getEventDetails()
	 */
	@Override
	protected String getEventDetails() {
		return context + "." + variable + " = " + value;
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
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addContourID(ContourID contourID) {
		context = contourID.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addNewValue(edu.bsu.cs.jive.contour.Value)
	 */
	public void addNewValue(Value v) {
		value = v.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addVariableID(edu.bsu.cs.jive.util.VariableID)
	 */
	public void addVariableID(VariableID variableID) {
		variable = variableID.toString();
	}
}
