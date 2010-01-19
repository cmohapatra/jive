package edu.buffalo.cse.jive.sequence;

import java.util.Iterator;
import java.util.List;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * A {@code SequenceModel} used to represent a program execution history of
 * a multi-threaded program.  A multi-threaded sequence model differs from a
 * sequence model in that it may contain more than one root.  Each root
 * corresponds to the root of the call tree for a thread of execution.
 * <p>
 * Iteration over the entire model by event results in event occurrences being
 * returned in the order in which they arose amongst all threads.  Therefore,
 * event occurrences of different threads will be interleaved.
 * 
 * @see SequenceModel
 * @see ExecutionOccurrence
 * @see EventOccurrence
 * @see MessageSend
 * @author Jeffrey K Czyz
 */
public interface MultiThreadedSequenceModel extends SequenceModel {
	
	// TODO Determine if we should use importers
//	public interface Importer extends SequenceModel.Importer {
//		public List<ExecutionOccurrence> provideRoots();
//	}
	
	// TODO Determine if we should use exporters
//	public interface Exporter extends SequenceModel.Exporter {
//		public void addRoot(ExecutionOccurrence root);
//	}
	
//	/**
//	 * Exports the multi-threaded sequence model to the reverse-builder supplied
//	 * as a parameter.
//	 * 
//	 * @param exporter the reverse-builder
//	 */
//	public void export(Exporter exporter);
	
	// TODO Add these later; think about using sub-models instead
//	public void accept(EventVisitor visitor, ThreadID thread);
//	
	
	// TODO Determine if this is the correct way of handling this
	/**
	 * Processes an (@code EventVisitor} the thread of execution of the supplied
	 * {@code ThreadID} in a depth-first order.
	 * 
	 * @param visitor the event visitor
	 * @param thread the desired thread to visit
	 */
	public void accept(ExecutionVisitor visitor, ThreadID thread);
	
	// TODO Determine if should be removed and accessed by a sub-model
	/**
	 * Returns an iterator which traverses the thread of execution of the supplied
	 * {@code ThreadID} in a depth-first order.
	 * 
	 * @param thread the thread of execution to be traversed
	 * @return an iterator over a thread in the model
	 */
	public Iterator<EventOccurrence> iterator(ThreadID thread);
	
	/**
	 * Returns whether or not the thread with the supplied {@code ThreadID}
	 * is part of the model.
	 * 
	 * @param thread the thread to be tested
	 * @return <code>true</code> if the model contains the thread,
	 *         <code>false</code> otherwise
	 */
	public boolean containsThread(ThreadID thread);
	
	/**
	 * Returns a list of all the {@code ThreadID}s that have threads
	 * contained in the model.
	 * 
	 * @return a list of the threads in the model by {@code ThreadID}
	 */
	public List<ThreadID> getThreads();
	
	/**
	 * Returns the root of the thread execution with the supplied
	 * {@code ThreadID}.
	 * 
	 * @param thread the thread whose root should be returned
	 * @return the root of the thread execution
	 */
	public ExecutionOccurrence getRoot(ThreadID thread);
	
	/**
	 * Returns a list of all the roots contained within the model.  Each root
	 * corresponds to the root of a thread of execution.
	 * 
	 * @return the roots contained within the model
	 */
	public List<ExecutionOccurrence> getRoots();
	
	/**
	 * Returns the event number of the last event occurring on the supplied
	 * thread.
	 * 
	 * @param thread the thread whose last event number should be returned
	 * @return the last event number of the thread
	 */
	public long lastEventNumber(ThreadID thread);
}
