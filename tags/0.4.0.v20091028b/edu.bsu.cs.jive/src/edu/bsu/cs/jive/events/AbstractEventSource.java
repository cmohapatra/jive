package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.util.Publisher;

/**
 * Abstract event source implementation.
 * This event source provides some utility methods for subclasses
 * to facilitate firing events.
 *
 * @author pvg
 */
public abstract class AbstractEventSource implements EventSource {

	private final Publisher<EventSource.Listener> publisher =
		new Publisher<EventSource.Listener>();
	
	public void addListener(Listener listener) {
		publisher.subscribe(listener);
	}

	public void removeListener(Listener listener) {
		publisher.unsubscribe(listener);
	}
	
	/**
	 * Fire an event to all listeners.
	 * @param e the event
	 */
	protected void fireEvent(final Event e) {
		publisher.publish(new Publisher.Distributor<EventSource.Listener>(){
			public void deliverTo(Listener subscriber) {
				subscriber.eventOccurred(AbstractEventSource.this, e);
			}
		});
	}
}