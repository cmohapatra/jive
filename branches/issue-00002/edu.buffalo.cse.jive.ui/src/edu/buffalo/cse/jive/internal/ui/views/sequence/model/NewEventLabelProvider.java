package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An event exporter used to provide labels for a <code>NewEvent</code> to be
 * used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class NewEventLabelProvider extends AbstractEventLabelProvider implements NewEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "New Event";
	
	/**
	 * The image representation of the event.
	 */
	private static final Image EVENT_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE = registry.get(IJiveUIConstants.ENABLED_NEW_EVENT_ICON_KEY);
	}
	
	/**
	 * The label provider used to obtain labels of
	 * <code>ContourCreationRecord</code>s.
	 */
	private ContourCreationRecordLabelProvider fRecordLabelProvider = new ContourCreationRecordLabelProvider();
	
	/**
	 * The string representation of the created object.
	 */
	private String fObject;
	
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
		return "object = " + fObject;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addContourCreationRecords(java.util.List)
	 */
	public void addContourCreationRecords(List<ContourCreationRecord> creationRecords) {
		if (!creationRecords.isEmpty()) {
			creationRecords.get(0).export(fRecordLabelProvider);
			fObject = fRecordLabelProvider.getContourID();
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addEnclosingContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addEnclosingContourID(ContourID enclosingID) {
		// do nothing
	}
}
