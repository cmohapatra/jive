package edu.buffalo.cse.jive.sequence;

/**
 * A Message defines a particular communication between lifelines (or contexts).
 * 
 * @author Jeffrey K Czyz
 */
public interface Message {

	/**
	 * The sort of message as defined by the UML specification.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public enum MessageSort {
		
		/**
		 * A synchronous message call.
		 */
		SYNCH_CALL,
		
		/**
		 * An asynchronous message call.  This is not used by JIVE.
		 */
		ASYNCH_CALL,
		
		/**
		 * An asynchronous message signal.  This is not used by JIVE.
		 */
		ASYNCH_SIGNAL,
		
		
		/**
		 * A create message.  This is not used by JIVE. 
		 */
		CREATE_MESSAGE,
		
		/**
		 * A delete message.  This is not used by JIVE.
		 */
		DELETE_MESSAGE,
		
		/**
		 * A reply message.
		 */
		REPLY
	}
	
	/**
	 * The kind of message as defined by the UML specification.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public enum MessageKind {
		
		/**
		 * A complete message.
		 */
		COMPLETE,
		
		/**
		 * A message that was lost (has an unknown destination).
		 */
		LOST,
		
		/**
		 * A message that was found (has an unknown origin).
		 */
		FOUND,
		
		/**
		 * An unknown message.
		 */
		UNKNOWN
	}
	
	/**
	 * Returns the message sort.
	 * 
	 * @return the message sort
	 */
	public MessageSort messageSort();
	
	/**
	 * Returns the message kind.
	 * 
	 * @return the message kind
	 */
	public MessageKind messageKind();
	
	/**
	 * The {@code MessageSend} marking the message origin.
	 * 
	 * @return the send event
	 */
	public MessageSend sendEvent();
	
	/**
	 * The {@code MessageReceive} marking the message destination.
	 * 
	 * @return the receive event
	 */
	public MessageReceive receiveEvent();
	
}
