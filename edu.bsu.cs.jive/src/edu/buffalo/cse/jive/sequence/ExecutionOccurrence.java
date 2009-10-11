package edu.buffalo.cse.jive.sequence;

import java.util.List;

import edu.bsu.cs.jive.util.ContourID;

/**
 * A portion of execution within a program execution history consisting of an
 * environment in which events occur.  An <code>ExecutionOccurrence</code> is
 * initiated by a <code>MessageSend</code> from an immediately preceding
 * execution.
 * <p>
 * This list of events occurring within an execution occurrence are ordered by
 * when they arose during the program execution.  A message send is a special
 * type of event occurrence that results in another execution occurrence.
 * Therefore, events in the resulting execution (and so on recursively) occur
 * in the program execution history before any events occurring after the
 * message send in the original execution occurrence.  
 * 
 * @see SequenceModel
 * @see EventOccurrence
 * @see Message
 * @author Jeffrey K Czyz
 */
public interface ExecutionOccurrence {

	// TODO Determine if we should use importers
	/**
	 * A builder for execution occurrences.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Importer {
		public ContourID provideID();
		public ContourID provideContext();
		public long provideDuration();
		public List<EventOccurrence> provideEvents();
		public MessageSend provideInitiator();
		public MessageSend provideTerminator();
		public SequenceModel provideContainingModel();
	}
	
	// TODO Determine if we should use exporters
	/**
	 * A reverse-builder for execution occurrences.  Currently, this interface
	 * is not needed.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Exporter {
		public void addID(ContourID id);
		public void addContext(ContourID context);
		public void addDuration(long duration);
		public void addEvents(List<EventOccurrence> events);
		public void addInitiator(MessageSend initiator);
		public void addTerminator(MessageSend terminator);
		public void exportFinished();
	}
	
	/**
	 * Exports the execution occurrence to the reverse-builder supplied as a
	 * parameter.
	 * 
	 * @param exporter the reverse-builder
	 */
	public void export(Exporter exporter);
	
	/**
	 * Returns the identifier of the execution occurrence.  This is typically the
	 * identifier of the method activation in which this execution occurrence
	 * represents.
	 * 
	 * @return the identifier of the execution
	 */
	public ContourID id();
	
	/**
	 * Returns the context identifier of the execution occurrence.  The context is
	 * typically the environment (instance or static) in which the execution took
	 * place.  The contour ID returned may refer to a virtual contour.
	 *  
	 * @return the context identifier of the execution
	 */
	public ContourID context();
	
	/**
	 * Returns whether the execution occurrence is no longer active.
	 * 
	 * @return <code>true</code> if the execution occurrence has terminated,
	 *         <code>false</code> if it is still active
	 */
	public boolean isTerminated();
	
	/**
	 * Returns the number of events occurring while the execution occurrence is
	 * active.  This includes events
	 * <ul>
	 *   <li>contained by the execution,</li>
	 *   <li>contained by execution occurrences resulting from message sends
	 *   contained within the execution (and so on recursively), and
	 *   <li>occurring on other threads before the execution occurrence completes.</li>  
	 * </ul>
	 * 
	 * @return the number of events elapsing while the execution occurrence is active
	 */
	public long duration();
	
	/**
	 * Returns the list of events that occurred within the execution occurrence.
	 * The list should not be the underlying list itself, but rather a copy of it.
	 * 
	 * @return the list of events occurring within the execution
	 */
	public List<EventOccurrence> events();
	
	/**
	 * Returns the {@code MessageSend} that initiated the execution occurrence.
	 * 
	 * @return the message send initiating the execution occurrence
	 */
	public MessageSend initiator();
	
	/**
	 * Returns the {@code MessageSend} that terminated the execution occurrence.
	 * 
	 * @return the terminating message send, or <code>null</code> if terminated by
	 *         an exception
	 * @throws IllegalStateException if the execution is not terminated
	 */
	public MessageSend terminator() throws IllegalStateException;
	
	/**
	 * Returns the <code>SequenceModel</code> in which the execution occurrence
	 * belongs.
	 * 
	 * @return the sequence model containing the execution
	 */
	public SequenceModel containingModel();
}
