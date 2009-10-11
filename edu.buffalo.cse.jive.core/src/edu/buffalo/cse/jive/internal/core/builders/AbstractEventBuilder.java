package edu.buffalo.cse.jive.internal.core.builders;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * An abstract implementation of an {@code Event.Importer}.  It provides the
 * event number and {@code ThreadID} for the event being built.
 * 
 * @author Jeffrey K Czyz
 */
public class AbstractEventBuilder implements Event.Importer {

	/**
	 * The event number to be provided for the event.
	 */
	private long number;
	
	/**
	 * The {@code ThreadID} to be provided for the event.
	 */
	private ThreadID threadID;
	
	/**
	 * Constructs an abstract event builder for an event occurring on the
	 * supplied {@code ThreadID}.  The contour manager is used to determine the
	 * event number for the event.
	 * 
	 * @param thread the thread on which the event occurred
	 * @param contourManager the contour management facade
	 */
	AbstractEventBuilder(ThreadID thread, ContourUtils contourManager) {
		number = contourManager.nextEventNumber();
		threadID = thread;
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Importer#provideNumber()
	 */
	public long provideNumber() {
		return number;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Importer#provideThreadID()
	 */
	public ThreadID provideThreadID() {
		return threadID;
	}

}
