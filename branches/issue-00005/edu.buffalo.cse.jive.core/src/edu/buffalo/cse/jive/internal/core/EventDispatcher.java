package edu.buffalo.cse.jive.internal.core;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;

/**
 * A job for notifying listeners of JIVE events.  The dispatcher operates
 * periodically on a separate thread so as to avoid suspending the virtual
 * machine while JIVE events are being dispatched to the listeners.
 * 
 * @author Jeffrey K Czyz
 */
public class EventDispatcher extends Job implements EventSource {

	/**
	 * A queue used to store newly created events.  The {@code EventDispatcher}
	 * periodically removes events from the queue and notifies listeners.
	 * 
	 * Accessing the queue must be done in a synchronous way.  
	 */
	private ArrayList<Event> eventQueue = new ArrayList<Event>();
	
	/**
	 * A list of {@link EventSource.Listener}s to be notified when {@code Event}s
	 * occur.
	 */
	private ListenerList listenerList = new ListenerList();
	
	/**
	 * Constructs the event dispatcher.
	 */
	public EventDispatcher() {
		super("JIVE Event Dispatcher");
		setPriority(Job.SHORT);
		setSystem(true);
	}
	
	/**
	 * Dispatches the event to all event listeners registered with the
	 * dispatcher.  Listeners are notified of events on a separate thread.
	 * 
	 * @param e the event to dispatch
	 */
	public void dispatchEvent(final Event e) {
		synchronized (eventQueue) {
			eventQueue.add(e);
		}
		
		schedule();
	}
	
	/**
	 * The default buffer used to hold events removed from the event queue.
	 * Events are usually processed fast enough that only one event is ever in
	 * the event queue when it is examined by the job.  If there is more than
	 * one event in the queue, a new buffer is allocated. 
	 */
	private static final Event[] SINGLE_EVENT_BUFFER = new Event[1];
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Event[] buffer = SINGLE_EVENT_BUFFER;
		Event event = null;
		
		// Check if the event queue contains events to process
		synchronized (eventQueue) {
			if (!eventQueue.isEmpty()) {
				
				// Copy the event references to a newly allocated buffer or to
				// the single event buffer if only one event is in the queue
				buffer = eventQueue.toArray(SINGLE_EVENT_BUFFER);
				eventQueue.clear();
				
				// If the event queue contained a single event, hold a local
				// reference to the event since events are processed after
				// leaving the synchronized block
				if (buffer == SINGLE_EVENT_BUFFER) {
					event = buffer[0];
				}
			}
			else {
				return Status.OK_STATUS;
			}
		}

		// Fire the event(s) off to the listeners
		if (buffer == SINGLE_EVENT_BUFFER) {
			fireEvent(event);
		}
		else {
			for (Event e : buffer) {
				fireEvent(e);
			}
		}
				
		return Status.OK_STATUS;
	}
	
	/**
	 * Notifies all the listeners of the given event.
	 * 
	 * @param event the event being dispatched
	 */
	private void fireEvent(Event event) {
		for (final Object listener : listenerList.getListeners()) {
			try {
				((EventSource.Listener) listener).eventOccurred(this, event);
			}
			catch (Exception e) {
				JiveCorePlugin.log(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource#addListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void addListener(Listener listener) {
		listenerList.add(listener);
		
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource#removeListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void removeListener(Listener listener) {
		listenerList.remove(listener);
	}

}
