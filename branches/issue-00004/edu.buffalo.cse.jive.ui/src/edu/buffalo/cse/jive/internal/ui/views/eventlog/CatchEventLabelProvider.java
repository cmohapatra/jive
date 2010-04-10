package edu.buffalo.cse.jive.internal.ui.views.eventlog;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide column labels for a <code>CatchEvent</code>
 * to be used by an <code>ITableLabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class CatchEventLabelProvider extends AbstractEventLabelProvider implements CatchEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Catch Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_CATCH_EVENT_ICON_KEY);
	}
	
	/**
	 * The string representation of the caught exception.
	 */
	protected String fException;
	
	/**
	 * The string representation of the context where the exception was caught.
	 */
	protected String fCatcher;
	
	/**
	 * The string representation of the variable referencing the caught
	 * exception.
	 */
	protected String fVariable;

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
		return "exception = " + fException + ", catcher = " + fCatcher + ", variable = " + fVariable;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addCatcher(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addCatcher(ContourID catcher) {
		fCatcher = catcher == null ? "<uncaught>" : catcher.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
	 */
	public void addException(Value exception) {
		fException = exception.toString();
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addVariable(edu.bsu.cs.jive.util.VariableID)
	 */
	public void addVariable(VariableID variable) {
		fVariable = variable == null ? "<unknown>" : variable.toString();
	}
}
