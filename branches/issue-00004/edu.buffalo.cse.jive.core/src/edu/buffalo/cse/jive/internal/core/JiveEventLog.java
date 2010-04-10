package edu.buffalo.cse.jive.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;
import edu.buffalo.cse.jive.core.IJiveEventLog;

/**
 * Implementation of an <code>IJiveEventLog</code>.  It listens for events
 * originating from an <code>EventSource</code> and stores them for later use.
 * Interested clients can register as listeners in order to be notified when
 * new events occur. 
 * 
 * @author Jeffrey K Czyz
 * @see edu.bsu.cs.jive.events.Event
 * @see edu.bsu.cs.jive.events.EventSource
 */
public class JiveEventLog implements IJiveEventLog, EventSource.Listener {

	/**
	 * A list of JIVE events representing the log.
	 */
	private List<Event> eventLog;
	
	/**
	 * The source of the log's events. 
	 */
	private EventSource eventSource;
	
	/**
	 * Constructs the log and registers it as a listener for new events from the
	 * supplied source.
	 * 
	 * @param source the source of the incoming JIVE events
	 */
	public JiveEventLog(EventSource source) {
		eventLog = Collections.synchronizedList(new ArrayList<Event>());
		eventSource = source;
	}
	
	public Event[] getEvents() {
		return eventLog.toArray(new Event[0]);
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource.Listener#eventOccurred(edu.bsu.cs.jive.events.EventSource, edu.bsu.cs.jive.events.Event)
	 */
	public void eventOccurred(EventSource source, Event event) {
		eventLog.add(event);
	}

	// TODO Determine if delegation is okay or if this class should extend AbstractEventSource
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource#addListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void addListener(Listener listener) {
		eventSource.addListener(listener);
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource#removeListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void removeListener(Listener listener) {
		eventSource.removeListener(listener);
	}

}
