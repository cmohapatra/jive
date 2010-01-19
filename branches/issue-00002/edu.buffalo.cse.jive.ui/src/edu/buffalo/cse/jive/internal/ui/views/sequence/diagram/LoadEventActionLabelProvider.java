package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.ContourCreationRecordLabelProvider;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class LoadEventActionLabelProvider extends AbstractActionLabelProvider implements LoadEvent.Exporter {

	/**
	 * The string representation of the event name.
	 */
	private static final String EVENT_NAME = "Load class";
	
	/**
	 * The image representation of the event.
	 */
	private static final ImageDescriptor EVENT_IMAGE_DESCRIPTOR;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		EVENT_IMAGE_DESCRIPTOR = registry.getDescriptor(IJiveUIConstants.ENABLED_LOAD_EVENT_ICON_KEY);
	}
	
	/**
	 * The label provider used to obtain labels of
	 * <code>ContourCreationRecord</code>s.
	 */
	private ContourCreationRecordLabelProvider recordLabelProvider = new ContourCreationRecordLabelProvider();
	
	/**
	 * The string representation of the loaded class.
	 */
	private String className;
	
	@Override
	protected String getEventDetails() {
		return className;
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
	 * @see edu.bsu.cs.jive.events.LoadEvent.Exporter#addContourCreationRecords(java.util.List)
	 */
	public void addContourCreationRecords(List<ContourCreationRecord> ccrs) {
		// TODO Determine when more than one class is included in a single load event
		if (!ccrs.isEmpty()) {
			ccrs.get(0).export(recordLabelProvider);
			className = recordLabelProvider.getContourID();
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.LoadEvent.Exporter#addEnclosingContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addEnclosingContourID(ContourID enclosingID) {}
}
