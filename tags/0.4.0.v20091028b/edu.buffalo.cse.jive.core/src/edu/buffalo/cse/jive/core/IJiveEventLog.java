package edu.buffalo.cse.jive.core;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;

// TODO Determine if this should be moved to edu.bsu.cs.jive project
// TODO Expand javadoc, naming the events
/**
 * A JIVE event log that supports listeners.  As JIVE events are formed, they
 * are stored for later use by a class implementing the interface.
 * 
 * @author Jeffrey K Czyz
 * @see Event
 */
public interface IJiveEventLog extends EventSource {
	
	/**
	 * Returns an array of JIVE events contained in the event log.
	 * 
	 * @return an array of JIVE events
	 */
	public Event[] getEvents();
	
	// TODO Determine what additional methods are needed
}
