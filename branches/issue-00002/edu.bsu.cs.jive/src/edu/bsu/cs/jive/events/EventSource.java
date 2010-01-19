package edu.bsu.cs.jive.events;

/**
 * Identifies an entity that produces execution events.
 *
 * @author pvg
 */
public interface EventSource {

	/**
	 * Add a listener to this event source.
	 * @param listener
	 */
	public void addListener(Listener listener);
	
	
	/**
	 * Remove a listener from this event source.
	 * @param listener
	 */
	public void removeListener(Listener listener);
	
	/**
	 * Listens to this event source for event notification.
	 * @author pvg
	 */
	public interface Listener {
		/**
		 * Called when an event is created by this event source.
		 * <p>
		 * Listeners can take event-specific action by using
		 * the {@link Event.Visitor} interface for double-dispatch.
		 * @param source the event source
		 * @param event the event that occurred
		 */
		public void eventOccurred(EventSource source, Event event);
	}
}
