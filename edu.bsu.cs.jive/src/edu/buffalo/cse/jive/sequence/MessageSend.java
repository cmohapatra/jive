package edu.buffalo.cse.jive.sequence;

/**
 * An {@code EventOccurrence} that results in the creation of a
 * {@code Message}.  A message send typically marks the origin of a method call
 * or return.
 * 
 * @see SequenceModel
 * @see EventOccurrence
 * @see Message
 * @author Jeffrey K Czyz
 *
 */
public interface MessageSend extends EventOccurrence {
	
	/**
	 * Returns the {@code Message} that was sent.
	 * 
	 * @return the message sent
	 */
	public Message message();
	
//	/**
//	 * Returns the {@code ExecutionOccurrence} that receives the message
//	 * send.  That is, the resulting execution.
//	 * 
//	 * @return the receiving execution of the message send
//	 */
//	public ExecutionOccurrence receivingExecution();
}
