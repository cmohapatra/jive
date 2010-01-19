package edu.buffalo.cse.jive.sequence.impl;

import java.util.HashMap;
import java.util.Map;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.EventOccurrence;

// TODO Determine if this class should be expanded or removed
/**
 * An abstract implementation of a {@code MultiThreadedSequenceModel}.  It
 * provides functionality for managing and notifying listeners.  Visiting and
 * (basic) iteration support is also implemented.
 * 
 * @see SequenceModel
 * @author Jeffrey K Czyz
 */
public abstract class AbstractMultiThreadedSequenceModel extends AbstractSequenceModel implements MultiThreadedSequenceModel {

	/**
	 * A mapping between {@code ThreadID}s and the number of the last event
	 * occurring on it.
	 */
	private Map<ThreadID, Long> threadToLastEventNumberMap = new HashMap<ThreadID, Long>();
	
	// TODO Determine if we should use exporters
//	public void export(final MultiThreadedSequenceModel.Exporter exporter) {
//		SequenceModel.Exporter threadExporter = new SequenceModel.Exporter() {
//			public void addEvent(EventOccurrence event, ExecutionOccurrence execution) {
//				exporter.addEvent(event, execution);
//			}
//
//			public void addExecution(ExecutionOccurrence execution,	MessageSend initiator) {
//				exporter.addExecution(execution, initiator);
//			}
//
//			public void exportFinished() {
//				exporter.exportFinished();
//			}
//		};
//		
//		// TODO Finish implementing this
//	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#accept(edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor)
	 */
	public void accept(EventVisitor visitor) {
		checkLock();
		for (EventOccurrence event : this) {
			event.accept(visitor, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#addEvent(edu.bsu.cs.jive.events.Event)
	 */
	protected void addEvent(Event event) {
		assert checkLock();
		
		ThreadID thread = event.thread();
		if (thread != null) {
			threadToLastEventNumberMap.put(thread, event.number());
		}
		
		super.addEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#lastEventNumber(edu.bsu.cs.jive.util.ThreadID)
	 */
	public long lastEventNumber(ThreadID thread) {
		if (threadToLastEventNumberMap.containsKey(thread)) {
			return threadToLastEventNumberMap.get(thread);
		}
		else {
			throw new IllegalArgumentException("The thread " + thread + " does not exist in the model.");
		}
	}
}
