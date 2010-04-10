package edu.buffalo.cse.jive.sequence.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.util.Publisher;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.MessageReceive;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;

/**
 * An abstract implementation of a <code>SequenceModel</code>.  It provides
 * functionality for managing and notifying listeners.  Visiting and iteration
 * support is also implemented.  Subclasses must implement the event processing
 * methods in order to add events to the model.
 * 
 * @see AbstractMultiThreadedSequenceModel
 * @author Jeffrey K Czyz
 */
public abstract class AbstractSequenceModel implements SequenceModel {
	
	/**
	 * The event number of the last event added to the model.
	 */
	private long lastEventNumber = 1;
	
	/**
	 * A mapping from event numbers to the corresponding event occurrence.
	 */
	private Map<Long, EventOccurrence> eventNumberToOccurrenceMap = new HashMap<Long, EventOccurrence>();
	
	/**
	 * A publisher used to notify listeners of updates to the model.
	 */
	private final Publisher<SequenceModel.Listener> publisher = new Publisher<SequenceModel.Listener>();
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#addListener(edu.buffalo.cse.jive.sequence.SequenceModel.Listener)
	 */
	public void addListener(Listener listener) {
		publisher.subscribe(listener);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#removeListener(edu.buffalo.cse.jive.sequence.SequenceModel.Listener)
	 */
	public void removeListener(Listener listener) {
		publisher.unsubscribe(listener);
	}
	
	/**
	 * Notifies the registered listeners that an <code>ExecutionOccurrence</code>
	 * has been added to the model. 
	 * 
	 * @param execution the execution added to the model
	 * @param initiator the message send that initiated the added execution
	 */
	protected void fireExecutionAdded(final ExecutionOccurrence execution, final MessageSend initiator) {
		publisher.publish(new Publisher.Distributor<SequenceModel.Listener>() {
			public void deliverTo(SequenceModel.Listener listener) {
				listener.executionAdded(AbstractSequenceModel.this, execution, initiator);
			}
		});
	}

	/**
	 * Notifies the registered listeners that an <code>EventOccurrence</code> has
	 * been added to the model.
	 *  
	 * @param event the event added to the model
	 * @param execution the containing execution of the added event
	 */
	protected void fireEventAdded(final EventOccurrence event, final ExecutionOccurrence execution) {
		eventNumberToOccurrenceMap.put(event.underlyingEvent().number(), event);
		publisher.publish(new Publisher.Distributor<SequenceModel.Listener>() {
			public void deliverTo(SequenceModel.Listener listener) {
				listener.eventAdded(AbstractSequenceModel.this, event, execution);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#lastEventNumber()
	 */
	public long lastEventNumber() {
		return lastEventNumber;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getEventOccurrence(long)
	 */
	public EventOccurrence getEventOccurrence(long eventNumber) {
		if (eventNumberToOccurrenceMap.containsKey(eventNumber)) {
			return eventNumberToOccurrenceMap.get(eventNumber);
		}
		else {
			throw new IllegalArgumentException("Event number " + eventNumber + " not in model");
		}
	}
	
	/**
	 * Adds the supplied event to the model.  How an event is added is
	 * implementation specific.
	 * 
	 * @param event the event to add to the model
	 * @see #processAssignEvent(AssignEvent)
	 * @see #processCallEvent(CallEvent)
	 * @see #processEOSEvent(EOSEvent)
	 * @see #processExceptionEvent(ExceptionEvent)
	 * @see #processExitEvent(ExitEvent)
	 * @see #processLoadEvent(LoadEvent)
	 * @see #processNewEvent(NewEvent)
	 * @see #processReturnEvent(ReturnEvent)
	 * @see #processStartEvent(StartEvent)
	 */
	protected void addEvent(Event event) {
		assert checkLock();
		lastEventNumber = event.number();
		event.accept(eventProcessor, null);
	}
	
	/**
	 * A visitor used by {@code #addEvent(Event)} to process each type of event in
	 * a specific manner.
	 */
	private Event.Visitor eventProcessor = new Event.Visitor() {

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.AssignEvent, java.lang.Object)
		 */
		public Object visit(AssignEvent event, Object arg) {
			processAssignEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.CallEvent, java.lang.Object)
		 */
		public Object visit(CallEvent event, Object arg) {
			processCallEvent(event);
			return null;
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.CatchEvent, java.lang.Object)
		 */
		public Object visit(CatchEvent event, Object arg) {
			processCatchEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.EOSEvent, java.lang.Object)
		 */
		public Object visit(EOSEvent event, Object arg) {
			processEOSEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ExceptionEvent, java.lang.Object)
		 */
		public Object visit(ExceptionEvent event, Object arg) {
			// Do nothing.  ExceptionEvent to be removed.
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ExitEvent, java.lang.Object)
		 */
		public Object visit(ExitEvent event, Object arg) {
			processExitEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.LoadEvent, java.lang.Object)
		 */
		public Object visit(LoadEvent event, Object arg) {
			processLoadEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.NewEvent, java.lang.Object)
		 */
		public Object visit(NewEvent event, Object arg) {
			processNewEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ReturnEvent, java.lang.Object)
		 */
		public Object visit(ReturnEvent event, Object arg) {
			processReturnEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.StartEvent, java.lang.Object)
		 */
		public Object visit(StartEvent event, Object arg) {
			processStartEvent(event);
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ThrowEvent, java.lang.Object)
		 */
		public Object visit(ThrowEvent event, Object arg) {
			processThrowEvent(event);
			return null;
		}
	};
	
	/**
	 * Processes an {@code AssignEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processAssignEvent(AssignEvent event);
	
	/**
	 * Processes a {@code CallEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processCallEvent(CallEvent event);
	
	/**
	 * Processes a {@code CatchEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processCatchEvent(CatchEvent event);
	
	/**
	 * Processes an {@code EOSEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processEOSEvent(EOSEvent event);
	
	/**
	 * Processes an {@code ExitEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processExitEvent(ExitEvent event);
	
	/**
	 * Processes a {@code LoadEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processLoadEvent(LoadEvent event);
	
	/**
	 * Processes a {@code NewEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processNewEvent(NewEvent event);
	
	/**
	 * Processes a {@code ReturnEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processReturnEvent(ReturnEvent event);
	
	/**
	 * Processes a {@code StartEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processStartEvent(StartEvent event);
	
	/**
	 * Processes a {@code ThrowEvent} for addition to the model.
	 * 
	 * @param event the event to add to the model
	 */
	protected abstract void processThrowEvent(ThrowEvent event);
	
	// TODO Determine if we can make this protected
	/**
	 * Checks to see if the current thread owns the model lock.
	 * 
	 * @return <code>true</code> if the current thread holds the lock,
	 *         otherwise an exception is thrown
	 * @throws IllegalMonitorStateException if the current thread does not hold
	 *         the lock
	 */
	public final boolean checkLock() throws IllegalMonitorStateException {
		ReentrantLock lock = getModelLock();
		// TODO Determind if isLocked() is really needed
		if (!lock.isHeldByCurrentThread() && lock.isLocked()) {
  		throw new IllegalMonitorStateException("The model lock is not held by current thread.");
		}
		
  	return true;
	}
	
	// TODO Determine if we should use exporters
//	/* (non-Javadoc)
//	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#export(edu.buffalo.cse.jive.sequence.SequenceModel.Exporter)
//	 */
//	public void export(final Exporter exporter) {
//		accept(new ExecutionVisitor() {
//			public void visit(ExecutionOccurrence execution) {
//				exporter.addExecution(execution, execution.initiator());
//				
//				// TODO Determine how to export in order for multithreaded/filtered programs?
//				// Or is this impossible when visiting by execution? 
//				for (EventOccurrence event : execution.events()) {
//					exporter.addEvent(event, execution);
//				}
//			}
//		});
//		exporter.exportFinished();
//	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#accept(edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor)
	 */
	public void accept(EventVisitor visitor) {
		checkLock();
		for (EventOccurrence event : this) {
			event.accept(visitor, null);
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#accept(edu.buffalo.cse.jive.sequence.SequenceModel.ExecutionVisitor)
	 */
	public void accept(ExecutionVisitor visitor) {
		checkLock();
		ExecutionOccurrence execution = getRoot();
		visitor.visit(execution);
		
		if (visitor.visitChildren(execution)) {
			visitResultingExecutions(visitor, execution);
		}
	}
	
	/**
	 * A helper method for visiting <code>ExecutionOccurrence</code>s.
	 * 
	 * @param visitor the visitor being processed
	 * @param execution the execution being visited
	 */
	private void visitResultingExecutions(ExecutionVisitor visitor, ExecutionOccurrence execution) {
		for (EventOccurrence event : execution.events()) {
			if (event instanceof MessageSend) {
				MessageSend sendEvent = (MessageSend) event;
				if (shouldTraverseForInOrder(sendEvent)) {
					ExecutionOccurrence nextExecution = sendEvent.message().receiveEvent().containingExecution();
					visitor.visit(nextExecution);
					
					if (visitor.visitChildren(nextExecution)) {
						visitResultingExecutions(visitor, nextExecution);
					}
				}
			}
		}
	}
	
	/**
	 * Returns whether the supplied event should be used to traverse the sequence
	 * model in order.
	 * 
	 * @param send the message send in question
	 * @return <code>true</code> if the message send is a call event,
	 *         <code>false</code> otherwise
	 */
	private boolean shouldTraverseForInOrder(MessageSend send) {
		return send.underlyingEvent() instanceof CallEvent;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#iterator()
	 */
	public Iterator<EventOccurrence> iterator() {
		checkLock();
		return new DepthFirstIterator(getRoot());
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#iterator(edu.buffalo.cse.jive.sequence.ExecutionOccurrence)
	 */
	public Iterator<EventOccurrence> iterator(ExecutionOccurrence root) {
		if (root.containingModel() == this) {
			checkLock();
			return new DepthFirstIterator(root);
		}
		
		throw new IllegalArgumentException("Execution " + root + " is not contained in this model.");
	}
	
	/**
	 * An <code>Iterator</code> that traverses the sequence model in a depth-first
	 * order.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class DepthFirstIterator implements Iterator<EventOccurrence> {
		
		/**
		 * A stack of <code>Iterator</code> to simulate recursive calls over the
		 * sequence model. 
		 */
		private Stack<Iterator<EventOccurrence>> stack;
		
		/**
		 * The <code>EventOccurrence</code> that should be returned next, or
		 * <code>null</code> if the iterator is exhausted.
		 */
		private EventOccurrence nextEvent;
		
		/**
		 * Constructs a new iterator that starts traversing the sequence model at
		 * the supplied <code>ExecutionOccurrence</code>.
		 *  
		 * @param root where the traversal should start (and finish)
		 */
		public DepthFirstIterator(ExecutionOccurrence root) {
			checkLock();
			stack = new Stack<Iterator<EventOccurrence>>();
			Iterator<EventOccurrence> rootIterator = root.events().iterator();
			stack.push(rootIterator);
			calculateNextEvent();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return nextEvent != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public EventOccurrence next() {
			// TODO Determine if checkLock() should be called
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			EventOccurrence result = nextEvent;
			calculateNextEvent();
			return result;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Determines what <code>EventOccurrence</code> should be returned by the
		 * iterator next.
		 */
		private void calculateNextEvent() {
			if (stack.isEmpty()) {
				nextEvent = null;
			}
			else {
				Iterator<EventOccurrence> iterator = stack.peek();
				if (iterator.hasNext()) {
					nextEvent = iterator.next();
					
					// Adjust the stack if necessary
					if (nextEvent instanceof MessageSend) {
						MessageSend sendEvent = (MessageSend) nextEvent;
						
						// Only traverse on calls, not on returns
						if (shouldTraverseForInOrder(sendEvent)) {
							MessageReceive receiveEvent = sendEvent.message().receiveEvent();
							iterator = receiveEvent.containingExecution().events().iterator();
							stack.push(iterator);
						}
					}
					// Ignore the duplicate underlying events
					else if (nextEvent instanceof MessageReceive) {
						calculateNextEvent();
					}
				}
				else {
					stack.pop();
					calculateNextEvent();
				}
			}
		}		
	}
}
