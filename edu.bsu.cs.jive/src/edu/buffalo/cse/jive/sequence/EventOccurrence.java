package edu.buffalo.cse.jive.sequence;

import edu.bsu.cs.jive.events.Event;
import edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor;

/**
 * An occurrence of a method call, variable assignment, or any other event of
 * interest within a program execution history.  A <code>EventOccurrence</code>
 * is contained within an <code>ExecutionOccurrence</code> and effectively
 * encapsulates the underlying event.
 * 
 * @see SequenceModel
 * @see ExecutionOccurrence
 * @see MessageSend
 * @author Jeffrey K Czyz
 */
public interface EventOccurrence {
	
	/**
	 * Accepts the <code>EventVisitor</code>.
	 * 
	 * @param visitor the visitor to accept
	 * @param arg the argument of the visitor
	 */
	public void accept(EventVisitor visitor, Object arg);
	
	/**
	 * Returns the underlying event in which the event occurrence encapsulates.
	 * 
	 * @return the event underlying the event occurrence
	 */
	public Event underlyingEvent();
	
	/**
	 * Returns the <code>ExecutionOccurrence</code> in which the event occurrence
	 * is contained.
	 * 
	 * @return the containing execution of the event occurrence
	 */
	public ExecutionOccurrence containingExecution();
}
