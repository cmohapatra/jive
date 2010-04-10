package edu.buffalo.cse.jive.sequence;

/**
 * An {@code EventOccurrence} that results in the termination of a
 * {@code Message}.  A message receive typically marks the destination of a
 * method call or return.
 * 
 * @see SequenceModel
 * @see EventOccurrence
 * @see Message
 * @author Jeffrey K Czyz
 */
public interface MessageReceive extends EventOccurrence {
	
	/**
	 * Returns the {@code Message} that was received.
	 * 
	 * @return the message received
	 */
	public Message message();
	
}
