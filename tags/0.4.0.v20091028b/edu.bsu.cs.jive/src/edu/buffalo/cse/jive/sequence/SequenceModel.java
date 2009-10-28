package edu.buffalo.cse.jive.sequence;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * A representation of a program execution history.  It can be thought of as a
 * call tree, where each node of the tree is an <code>ExecutionOccurrence</code>
 * in the program.  Each execution occurrence contains a list of
 * <code>EventOccurrence</code>s in the sequence in which they arose within the
 * containing execution occurrence.
 * <p>
 * A <code>MessageSend</code> is a type of event occurrence that resulted in
 * another execution occurrence within the program.  These message sends are
 * what give rise to the model's tree structure.  A depth-first traversal of the
 * tree results in visiting the event and execution occurrences in the sequence
 * in which they arose during the program execution.
 * <p>
 * This interface is designed to model programs having only a single thread of
 * execution.  For multi-threaded programs, the
 * {@link MultiThreadedSequenceModel} interface should be used.
 * 
 * @see ExecutionOccurrence
 * @see EventOccurrence
 * @see MessageSend
 * @see MultiThreadedSequenceModel
 * @author Jeffrey K Czyz
 */
public interface SequenceModel extends Iterable<EventOccurrence> {

	// TODO Determine if we should use importers
//	public interface Importer {
//		public ExecutionOccurrence provideRoot();
//		public List<EventOccurrence> provideEvents(ExecutionOccurrence execution);
//		public ExecutionOccurrence provideReceivingExecution(MessageSend message);
//	}
	
	// TODO Determine if we should use exporters
//	public interface Exporter {
//		public void addExecution(ExecutionOccurrence execution, MessageSend initiator);
//		public void addEvent(EventOccurrence event, ExecutionOccurrence execution);
//		public void exportFinished();
//	}
	
//	/**
//	 * Exports the sequence model to the reverse-builder supplied as a parameter.
//	 * 
//	 * @param exporter the reverse-builder
//	 */
//	public void export(Exporter exporter);
	
	/**
	 * A visitor for the sequence model that visits <code>EventOccurrence</code>s
	 * in the order in which they occurred within the program execution.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface EventVisitor {
		
		/**
		 * Visits an <code>EventOccurrernce</code> (other than a
		 * <code>MessageSend</code>).
		 * 
		 * @param event the event occurrence being visited
		 */
		public void visit(EventOccurrence event);
		
		/**
		 * Visits an occurrence of a <code>MessageSend</code> event.
		 * 
		 * @param event the message send being visited
		 */
		public void visit(MessageSend event);
	}
	
	/**
	 * Processes an <code>EventVisitor</code> over the model in a depth-first
	 * order.
	 * 
	 * @param visitor the event visitor
	 */
	public void accept(EventVisitor visitor);
	
	// TODO Determine if we need an execution visitor
	/**
	 * A visitor for the sequence model that visits
	 * <code>ExecutionOccurrence</code>s in the order in which they occurred
	 * within the program execution.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface ExecutionVisitor {
		
		/**
		 * Visits an <code>ExecutionOccurrence</code>.
		 * 
		 * @param execution the execution occurrence being visited
		 */
		public void visit(ExecutionOccurrence execution);
		
		/**
		 * Returns whether to recursively visit the given execution's children.
		 * This method can be used to effectively prune the search space.
		 * 
		 * @param execution the execution whose children can be visited
		 * @return <code>true</code> if the children should be visited,
		 *         <code>false</code> otherwise
		 */
		public boolean visitChildren(ExecutionOccurrence execution);
	}
	
	/**
	 * Processes an <code>ExecutionVisitor</code> over the model in a depth-first
	 * order.
	 * 
	 * @param visitor the execution visitor
	 */
	public void accept(ExecutionVisitor visitor);
	
	/**
	 * Returns an iterator which traverses the sequence model in a depth-first
	 * order.
	 * 
	 * @return an iterator over the model
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<EventOccurrence> iterator();
	
	/**
	 * Returns an iterator which traverses the sequence model in a depth-first
	 * order starting at the supplied <code>ExecutionOccurrence</code>.  The
	 * iterator will be exhausted once all descendants of the <code>root</code>
	 * have been returned.
	 * 
	 * @param root the starting point of the iterator
	 * @return an iterator over a sub-tree within the model
	 */
	public Iterator<EventOccurrence> iterator(ExecutionOccurrence root);
	
	/**
	 * Returns the lock object for synchronized reading and writing to the model.
	 * Only the thread owning the lock (by calling {@link ReentrantLock#lock()})
	 * may access the model.
	 * <p>
	 * The following is a typical example of using the lock to correctly access
	 * the model:
	 * <p>
	 * <pre>
	 *     SequenceModel model = ... ;
	 *     ReentrantLock modelLock = model.getModelLock();
	 *     modelLock.lock();
	 *     try {
	 *         ...
	 *     }
	 *     finally {
	 *         modelLock.unlock();
	 *     }
	 * </pre>
	 * 
	 * @return the model's lock object
	 */
	public ReentrantLock getModelLock();
	
	/**
	 * Returns the <code>ThreadID</code> associated with the thread being modeled.
	 * 
	 * @return the thread of execution being modeled
	 */
	public ThreadID getThread();
	
	/**
	 * Returns the root <code>ExecutionOccurrence</code> of the model.  That is,
	 * the execution occurrence that occurred first in the program execution
	 * history.
	 * 
	 * @return the root execution occurrence
	 */
	public ExecutionOccurrence getRoot();
	
	/**
	 * Returns the object context of the given {@code ExecutionOccurrence}.  The
	 * object context is the {@code ContourID} of the inner-most contour of the
	 * object in which the method occurs (i.e., the non-virtual instance contour
	 * of the class that was instantiated).  For static contexts, the execution's
	 * context is simply returned. 
	 * 
	 * @param execution the execution whose object context is returned
	 * @return the object context of the execution
	 * @see ExecutionOccurrence#context()
	 */
	public ContourID objectContext(ExecutionOccurrence execution);
	
	/**
	 * Returns the event number of the last event in the model.
	 * 
	 * @return the event number of the last event
	 */
	public long lastEventNumber();
	
	public EventOccurrence getEventOccurrence(long eventNumber);
	
	/**
	 * A listener interface for the <code>SequenceModel<code>. Implementing
	 * classes are notified when <code>ExecutionOccurrence</code>s and
	 * <code>EventOccurrence</code>s are added to the model.
	 * 
	 * @author Jeffrey K Czyz
	 *
	 */
	public interface Listener {
		
		/**
		 * Called by the model when an <code>ExecutionOccurrence</code> is added to
		 * the model.
		 * 
		 * @param model the model in which the execution is added
		 * @param execution the execution occurrence that was added
		 * @param initiator the message send that initiated the added execution
		 */
		public void executionAdded(SequenceModel model, ExecutionOccurrence execution, MessageSend initiator);
		
		/**
		 * Called by the model when an <code>EventOccurrence</code> is added to the
		 * model.
		 * 
		 * @param model the model in which the execution is added
		 * @param event the event occurrence that was added
		 * @param execution the containing execution of the added event
		 */
		public void eventAdded(SequenceModel model, EventOccurrence event, ExecutionOccurrence execution);
	}
	
	/**
	 * Registers the supplied listener to be notified when the model has changed.
	 * 
	 * @param listener the listener to be notified
	 */
	public void addListener(Listener listener);
	
	/**
	 * Unregisters the supplied listener from being notified when the model has
	 * changed.
	 * 
	 * @param listener the listener which no longer should be notified
	 */
	public void removeListener(Listener listener);
}
