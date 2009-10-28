package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.ContourCreationRecordLabelProvider;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class NewEventActionLabelProvider extends AbstractActionLabelProvider implements NewEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Create object";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_NEW_EVENT_ICON_KEY);
	}
	
	/**
	 * The label provider used to obtain labels of
	 * <code>ContourCreationRecord</code>s.
	 */
	private ContourCreationRecordLabelProvider recordLabelProvider = new ContourCreationRecordLabelProvider();
	
	/**
	 * The string representation of the created object.
	 */
	private String object;
	
	@Override
	protected String getEventDetails() {
		return object;
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
	 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addContourCreationRecords(java.util.List)
	 */
	public void addContourCreationRecords(List<ContourCreationRecord> creationRecords) {
		object = "";
		if (!creationRecords.isEmpty()) {
			creationRecords.get(0).export(recordLabelProvider);
			object = recordLabelProvider.getContourID();
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addEnclosingContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addEnclosingContourID(ContourID enclosingID) {}
}
