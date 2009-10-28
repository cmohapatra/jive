package edu.buffalo.cse.jive.internal.ui.views.eventlog;

import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider;

/**
 * An abstract event exporter used to provide labels to an
 * <code>ITableLabelProvider</code>.  Column labels are provided for event
 * number, event name, thread name, and event details.
 * 
 * @author Jeffrey K Czyz
 */
public abstract class AbstractEventLabelProvider implements IJiveTableRowLabelProvider, Event.Exporter {
	
	/**
	 * The index of the thread name column. 
	 */
	public static final int THREAD_NAME_COLUMN = 0;
	
	/**
	 * The index of the event number column.
	 */
	public static final int EVENT_NUMBER_COLUMN = 1;
	
	/**
	 * The index of the event name column. 
	 */
	public static final int EVENT_NAME_COLUMN = 2;
	
	/**
	 * The index of the event details column. 
	 */
	public static final int EVENT_DETAILS_COLUMN = 3;
	
	/**
	 * The event number representation. 
	 */
	private String fEventNumber;
	
	/**
	 * The thread name representation.
	 */
	private String fThreadName;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
	 */
	public String getColumnText(int columnIndex) {
		switch (columnIndex) {
		case THREAD_NAME_COLUMN:
			return getThreadName();
		case EVENT_NUMBER_COLUMN:
			return getEventNumber();
		case EVENT_NAME_COLUMN:
			return getEventName();
		case EVENT_DETAILS_COLUMN:
			return getEventDetails();
		default:
			return "";
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnImage(int)
	 */
	public Image getColumnImage(int columnIndex) {
		if (columnIndex == EVENT_NAME_COLUMN) {
			return getEventImage();
		}
		
		return null;
	}
	
	/**
	 * Returns the string representation of the thread name.
	 * 
	 * @return the thread name
	 */
	protected String getThreadName() {
		return fThreadName;
	}
	
	/**
	 * Returns the string representation of the event number.
	 * 
	 * @return the event number
	 */
	protected String getEventNumber() {
		return fEventNumber;
	}
	
	/**
	 * Returns the string representation of the event name.
	 * 
	 * @return the event name
	 */
	protected abstract String getEventName();
	
	/**
	 * Returns the image representation of the event.
	 * 
	 * @return the event image
	 */
	protected abstract Image getEventImage();
	
	/**
	 * Returns the details of the event as a string.  The details are dependent
	 * on the event type.
	 * 
	 * @return the event details.
	 */
	protected abstract String getEventDetails();
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
	 */
	public void addNumber(long n) {
		fEventNumber = Long.toString(n);
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
	 */
	public void addThreadID(ThreadID thread) {
		if (thread != null) {
			fThreadName = thread.getName();
		}
		else {
			fThreadName = "";
		}
	}
}
