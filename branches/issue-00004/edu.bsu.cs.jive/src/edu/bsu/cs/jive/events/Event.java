package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * Superinterface of all jivelog events.
 * <p>
 * All events have an event number and a thread on which they occurred.
 * 
 * @author pvg
 */
public interface Event {

	/**
	 * Get the sequence number of this event.
	 * Each event has a unique sequence number with respect to a
	 * single execution.
	 * @return event sequence number
	 */
	// TODO Plug-in change
	public long number();
  // This may be able to be handled only in exporters and importers
	//public long getNumber();
	
	/**
	 * Get the thread on which this event took place.
	 * @return thread identifier
	 */
	// TODO Plug-in change
	public ThreadID thread();
  // This may be able to be handled only in exporters and importers
	//public ThreadID getThread();
	
	/**
	 * Accept a visitor
	 * @param visitor
	 * @param arg
	 * @return visitation result
	 */
	public Object accept(Visitor visitor, Object arg);
	
	/**
	 * Visitor class for event types.
	 * @author pvg
	 */
	public interface Visitor {
		
		/**
		 * Visits an {@code AssignEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(AssignEvent event, Object arg);
		
		/**
		 * Visits a {@code CallEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(CallEvent event, Object arg);
		
		/**
		 * Visits a {@code CatchEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(CatchEvent event, Object arg);
		
		/**
		 * Visits an {@code EOSEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(EOSEvent event, Object arg);
		
		/**
		 * Visits an {@code ExceptionEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 * @deprecated As of JIVE Platform 0.4, replace with {@link #visit(CatchEvent, Object)}
		 * and visit {@link #visit(ThrowEvent, Object)}.
		 */
		public Object visit(ExceptionEvent event, Object arg);
		
		/**
		 * Visits an {@code ExitEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(ExitEvent event, Object arg);
		
		/**
		 * Visits a {@code LoadEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(LoadEvent event, Object arg);
		
		/**
		 * Visits a {@code NewEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(NewEvent event, Object arg);
		
		/**
		 * Visits a {@code ReturnEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(ReturnEvent event, Object arg);
		
		/**
		 * Visits a {@code StartEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(StartEvent event, Object arg);
		//public Object visit(ThreadStartEvent event, Object arg);
		//public Object visit(ThreadDeathEvent event, Object arg);
    
		/**
		 * Visits a {@code ThrowEvent}.
		 * 
		 * @param event the event
		 * @param arg an argument
		 * @return the result
		 */
		public Object visit(ThrowEvent event, Object arg);
	}
	
	/**
	 * Superinterface of event-specific importer interfaces.
	 * @author pvg
	 */
	public interface Importer {
		/**
		 * Provide the sequence number of the event.
		 * @return sequence number
		 */
		public long provideNumber();
		
		/**
		 * Provide the thread identifier for the event.
		 * @return threadID
		 */
		public ThreadID provideThreadID();
	}
	
	/**
	 * Superinterface for event-specific exporter interfaces.
	 * Subclasses of {@link Event} should implement an
	 * <tt>export(Exporter)</tt> method.
	 * @author pvg
	 */
	public interface Exporter {
		
		/**
		 * Get the sequence number of this event.  Each event has a unique sequence
		 *  number with respect to a single execution.
		 * 
		 * @param n the event number
		 */
		public void addNumber(long n);
		
		/**
		 * Adds the thread on which this event took place.
		 * 
		 * @param thread the thread identifier
		 */
		public void addThreadID(ThreadID thread);
	}
}
