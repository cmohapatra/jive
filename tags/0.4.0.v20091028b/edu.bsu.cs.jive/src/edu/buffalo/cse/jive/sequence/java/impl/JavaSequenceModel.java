package edu.buffalo.cse.jive.sequence.java.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourFormat;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.sequence.impl.AbstractMultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel;
import edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation;
import edu.buffalo.cse.jive.sequence.java.MethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;

/**
 * An implementation of a {@code MultiThreadedSequenceModel} for Java programs.
 * All Java programs are inherently multi-threaded, however, JIVE does not track
 * system threads.  This class is used for both single- or multi-threaded
 * programs.
 * 
 * @author Jeffrey K Czyz
 */
public class JavaSequenceModel extends AbstractMultiThreadedSequenceModel {

	/**
	 * The model lock used for accessing the model.
	 * 
	 * @see SequenceModel#getModelLock()
	 */
	private ReentrantLock lock;
	
	/**
	 * The factory used to create {@code ExecutionOccurrence}s and
	 * {@code EventOccurrence}s for the model.
	 */
	private JavaSequenceFactory factory;
	
	/**
	 * A mapping between {@code ThreadID}s and the sequence model that represents
	 * the thread's execution history.
	 */
	private Map<ThreadID, JavaThreadSequenceModel> threadToModelMap;
	
	/**
	 * A mapping between {@code ContourID}s and those which represent their object
	 * contexts (i.e. non-virtual contours).  Static contexts are mapped to
	 * themselves.
	 */
	private Map<ContourID, ContourID> contextToObjectContextMap;
	
	/**
	 * Constructs the model.
	 */
	public JavaSequenceModel() {
		super();
		lock = new ReentrantLock();
		factory = new JavaSequenceFactory(this);
		threadToModelMap = new LinkedHashMap<ThreadID, JavaThreadSequenceModel>();
		contextToObjectContextMap = new TreeMap<ContourID, ContourID>();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getModelLock()
	 */
	public ReentrantLock getModelLock() {
		return lock;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#containsThread(edu.bsu.cs.jive.util.ThreadID)
	 */
	public boolean containsThread(ThreadID thread) {
		checkLock();
		return threadToModelMap.containsKey(thread);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getThread()
	 */
	public ThreadID getThread() {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#getThreads()
	 */
	public List<ThreadID> getThreads() {
		checkLock();
		List<ThreadID> result = new ArrayList<ThreadID>(threadToModelMap.size());
		result.addAll(threadToModelMap.keySet());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getRoot()
	 */
	public ExecutionOccurrence getRoot() {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#getRoot(edu.bsu.cs.jive.util.ThreadID)
	 */
	public ExecutionOccurrence getRoot(ThreadID thread) {
		checkLock();
		if (containsThread(thread)) {
			return threadToModelMap.get(thread).getRoot();
		}
		
		throw new IllegalArgumentException("Thread " + thread + " does not exist in this model.");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#getRoots()
	 */
	public List<ExecutionOccurrence> getRoots() {
		checkLock();
		List<ExecutionOccurrence> result = new ArrayList<ExecutionOccurrence>(threadToModelMap.size());
		for (JavaThreadSequenceModel model : threadToModelMap.values()) {
			result.add(model.getRoot());
		}
		
		return result;
	}

	public ContourID objectContext(ExecutionOccurrence execution) {
		checkLock();
		ContourID context = execution.context();
		if (contextToObjectContextMap.containsKey(context)) {
			return contextToObjectContextMap.get(context);
		}
		else {
			throw new IllegalArgumentException("The execution is not a part of the model.");
		}
	}
	
	// TODO Override export()

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#accept(edu.buffalo.cse.jive.sequence.SequenceModel.ExecutionVisitor)
	 */
	public void accept(ExecutionVisitor visitor) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#accept(edu.buffalo.cse.jive.sequence.SequenceModel.ExecutionVisitor, edu.bsu.cs.jive.util.ThreadID)
	 */
	public void accept(ExecutionVisitor visitor, ThreadID thread) {
		if (threadToModelMap.containsKey(thread)) {
			JavaThreadSequenceModel submodel = threadToModelMap.get(thread);
			submodel.accept(visitor);
		}
		else {
			throw new IllegalArgumentException("The sequence model does not contain the thread:  " + thread);
		}
	}
	
	// TODO Determine how to organize the code so this is not exposed
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#addEvent(edu.bsu.cs.jive.events.Event)
	 */
	public void addEvent(Event event) {
		// Expose this method for an adapter to use
		super.addEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processAssignEvent(edu.bsu.cs.jive.events.AssignEvent)
	 */
	protected void processAssignEvent(AssignEvent event) {
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processCallEvent(edu.bsu.cs.jive.events.CallEvent)
	 */
	protected void processCallEvent(CallEvent event) {
		processEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processCatchEvent(edu.bsu.cs.jive.events.CatchEvent)
	 */
	protected void processCatchEvent(CatchEvent event) {
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processEOSEvent(edu.bsu.cs.jive.events.EOSEvent)
	 */
	protected void processEOSEvent(EOSEvent event) {
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processExitEvent(edu.bsu.cs.jive.events.ExitEvent)
	 */
	protected void processExitEvent(ExitEvent event) {
		// TODO Determine if anything should be done here
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processLoadEvent(edu.bsu.cs.jive.events.LoadEvent)
	 */
	protected void processLoadEvent(LoadEvent event) {
		event.export(new LoadEvent.Exporter() {

			public void addContourCreationRecords(List<ContourCreationRecord> ccrs) {
				ContourIDExtractor extractor = new ContourIDExtractor();
				ccrs.get(0).export(extractor);
				ContourID staticContext = extractor.getContourID();
				contextToObjectContextMap.put(staticContext, staticContext);
			}

			public void addEnclosingContourID(ContourID enclosingID) {}

			public void addNumber(long n) {}

			public void addThreadID(ThreadID thread) {}
		});
		
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processNewEvent(edu.bsu.cs.jive.events.NewEvent)
	 */
	protected void processNewEvent(NewEvent event) {
		event.export(new NewEvent.Exporter() {

			public void addContourCreationRecords(List<ContourCreationRecord> creationRecords) {
				ContourIDExtractor extractor = new ContourIDExtractor();
				creationRecords.get(0).export(extractor);
				ContourID objectContext = extractor.getContourID();
				for (ContourCreationRecord record : creationRecords) {
					record.export(extractor);
					contextToObjectContextMap.put(extractor.getContourID(), objectContext);
				}
			}

			public void addEnclosingContourID(ContourID enclosingID) {}

			public void addNumber(long n) {}

			public void addThreadID(ThreadID thread) {}
			
		});
		
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processReturnEvent(edu.bsu.cs.jive.events.ReturnEvent)
	 */
	protected void processReturnEvent(ReturnEvent event) {
		processEvent(event);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processStartEvent(edu.bsu.cs.jive.events.StartEvent)
	 */
	protected void processStartEvent(StartEvent event) {
		// TODO Determine if anything should be done here 
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processThrowEvent(edu.bsu.cs.jive.events.ThrowEvent)
	 */
	protected void processThrowEvent(ThrowEvent event) {
		processEvent(event);
	}
	
	/**
	 * Adds an event to the model associated with the event's thread.
	 * 
	 * @param event the event to add to the model
	 */
	private void processEvent(Event event) {
		ThreadID thread = event.thread();
		assert thread != null;
		JavaThreadSequenceModel model = threadToModelMap.get(thread);
		
		// TODO Change this behavior if thread start and death events are added
		// Create the thread model if it does not exist
		if (model == null) {
			model = new JavaThreadSequenceModel(thread);
			threadToModelMap.put(thread, model);
		}
		
		model.addEvent(event);
	}
	
	/**
	 * An implementation of a {@code SequenceModel} for single thread within a Java
	 * program. (N.B., system threads are not represented in JIVE).
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class JavaThreadSequenceModel extends AbstractSequenceModel {
		
		/**
		 * The root of the thread's call tree.
		 */
		private ThreadActivation root;
		
		/**
		 * An activation stack used to infer where an event occurrence should be
		 * contained.
		 */
		private Stack<JavaExecutionActivation> stack;
		
		/**
		 * Constructs the model for the supplied thread.
		 * 
		 * @param thread the thread ID of the thread to model
		 */
		public JavaThreadSequenceModel(ThreadID thread) {
			root = factory.createThreadActivation(thread);
			stack = new Stack<JavaExecutionActivation>();
			stack.push(root);
			
			fireExecutionAdded(root, null);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#addListener(edu.buffalo.cse.jive.sequence.SequenceModel.Listener)
		 */
		public void addListener(Listener listener) {
			JavaSequenceModel.this.addListener(listener);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#removeListener(edu.buffalo.cse.jive.sequence.SequenceModel.Listener)
		 */
		public void removeListener(Listener listener) {
			JavaSequenceModel.this.removeListener(listener);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.java.impl.JavaSequenceModel#getModelLock()
		 */
		public ReentrantLock getModelLock() {
			return JavaSequenceModel.this.getModelLock();
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#addEvent(edu.bsu.cs.jive.events.Event)
		 */
		public void addEvent(Event event) {
			// Expose the method from the superclass
			super.addEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#fireExecutionAdded(edu.buffalo.cse.jive.sequence.ExecutionOccurrence, edu.buffalo.cse.jive.sequence.MessageSend)
		 */
		protected void fireExecutionAdded(ExecutionOccurrence execution, MessageSend initiator) {
			JavaSequenceModel.this.fireExecutionAdded(execution, initiator);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#fireEventAdded(edu.buffalo.cse.jive.sequence.EventOccurrence, edu.buffalo.cse.jive.sequence.ExecutionOccurrence)
		 */
		protected void fireEventAdded(EventOccurrence event, ExecutionOccurrence execution) {
			JavaSequenceModel.this.fireEventAdded(event, execution);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getThread()
		 */
		public ThreadID getThread() {
			return root.thread();
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel#getRoot()
		 */
		public ExecutionOccurrence getRoot() {
			return root;
		}
		
		public ContourID objectContext(ExecutionOccurrence execution) {
			return JavaSequenceModel.this.objectContext(execution);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processAssignEvent(edu.bsu.cs.jive.events.AssignEvent)
		 */
		protected void processAssignEvent(AssignEvent event) {
			processEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processCallEvent(edu.bsu.cs.jive.events.CallEvent)
		 */
		protected void processCallEvent(CallEvent event) {
			JavaExecutionActivation source = stack.peek();
			MethodActivation result = factory.createMethodActivation(event, source);
			stack.push(result);
			
			MessageSend initiator = result.initiator();
			fireEventAdded(initiator, initiator.containingExecution());
			fireExecutionAdded(result, initiator);
			// TODO should we also send notification for the message receive?
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processCatchEvent(edu.bsu.cs.jive.events.CatchEvent)
		 */
		protected void processCatchEvent(CatchEvent event) {
			processEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processEOSEvent(edu.bsu.cs.jive.events.EOSEvent)
		 */
		protected void processEOSEvent(EOSEvent event) {
			processEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processExitEvent(edu.bsu.cs.jive.events.ExitEvent)
		 */
		protected void processExitEvent(ExitEvent event) {
			// TODO Determine if anything should be done here
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processLoadEvent(edu.bsu.cs.jive.events.LoadEvent)
		 */
		protected void processLoadEvent(LoadEvent event) {
			processEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processNewEvent(edu.bsu.cs.jive.events.NewEvent)
		 */
		protected void processNewEvent(NewEvent event) {
			processEvent(event);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processReturnEvent(edu.bsu.cs.jive.events.ReturnEvent)
		 */
		protected void processReturnEvent(ReturnEvent event) {
			JavaExecutionActivation source = stack.pop();
			JavaExecutionActivation destination = stack.peek();
			Message message = factory.terminateMethodActivation(event, source, destination);
			fireEventAdded(message.sendEvent(), source);
			// TODO should we also send notification for the message receive?
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processStartEvent(edu.bsu.cs.jive.events.StartEvent)
		 */
		protected void processStartEvent(StartEvent event) {
			// TODO Determine if anything should be done here
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#processThrowEvent(edu.bsu.cs.jive.events.ThrowEvent)
		 */
		protected void processThrowEvent(ThrowEvent event) {
			if (event.wasFramePopped()) {
				JavaExecutionActivation execution = (AbstractActivation) stack.pop();
				updateActivationDuration((AbstractActivation) execution, event);
				EventOccurrence occurrence = factory.createEventOccurrence(event, execution);
				fireEventAdded(occurrence, execution);
			}
			else {
				processEvent(event);
			}
		}
		
		/**
		 * Processes the supplied event to be added to the model.  This method should
		 * not take {@code CallEvent}s, {@code ReturnEvent}s, or {@code ThrowEvent}s
		 * that resulted in frames popped from the stack.  Instead, they should be
		 * handled by their respective process methods.
		 * 
		 * @param event the event to add to the model
		 * @see #processCallEvent(CallEvent)
		 * @see #processReturnEvent(ReturnEvent)
		 * @see #processThrowEvent(ThrowEvent)
		 */
		private void processEvent(Event event) {
			JavaExecutionActivation execution = stack.peek();
			EventOccurrence occurrence = factory.createEventOccurrence(event, execution);
			fireEventAdded(occurrence, execution);
		}
		
		/**
		 * Updates the duration of the supplied activation where the given event is
		 * the event causing the activation to complete.  This may be a Return event
		 * or a Throw event.
		 * 
		 * @param activation the activation whose duration is to be updated
		 * @param lastEvent the event causing the activation to complete
		 */
		private void updateActivationDuration(AbstractActivation activation, Event lastEvent) {
			// TODO Determine if there is a better way of accomplishing this
			long firstEventNumber = activation.events().get(0).underlyingEvent().number();
			long lastEventNumber = lastEvent.number();
			activation.setDuration(lastEventNumber - firstEventNumber + 1);
			activation.setTerminated();
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.impl.AbstractSequenceModel#iterator()
	 */
	public Iterator<EventOccurrence> iterator() {
		return new MultiThreadedIterator();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel#iterator(edu.bsu.cs.jive.util.ThreadID)
	 */
	public Iterator<EventOccurrence> iterator(ThreadID thread) {
		if (containsThread(thread)) {
			return threadToModelMap.get(thread).iterator();
		}
		
		throw new IllegalArgumentException("Thread " + thread + " does not exist in this model.");
	}
	
	/**
	 * An event iterator over a {@code JavaSequenceModel} where events are
	 * returned in the order in which they occurred across all threads.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class MultiThreadedIterator implements Iterator<EventOccurrence> {
		
		/**
		 * A queue of event iterators; one for each thread containing more events
		 * to return.
		 */
		private Queue<Iterator<EventOccurrence>> queue;
		
		/**
		 * A mapping between iterators and the next event they should return.  Since
		 * {@code Iterator#next()} had to be called to obtain the value, it is
		 * possible that the {@code Iterator#hasNext()} method may return
		 * <code>false</code>.
		 */
		private Map<Iterator<EventOccurrence>, EventOccurrence> iteratorToNextEventMap;
		
		/**
		 * The event number of the last event returned by the iterator.  The
		 * {@code StartEvent} is not captured in the model, so this variable is
		 * initialized to {@code 1}.
		 */
		private long lastEventNumber = 1;
		
		/**
		 * Constructs the iterator.
		 */
		public MultiThreadedIterator() {
			queue = new LinkedList<Iterator<EventOccurrence>>();
			iteratorToNextEventMap = new HashMap<Iterator<EventOccurrence>, EventOccurrence>();
			
			// Initialize the queue and map
			for (JavaThreadSequenceModel model : threadToModelMap.values()) {
				Iterator<EventOccurrence> iterator = model.iterator();
				if (iterator.hasNext()) {
					EventOccurrence nextEvent = iterator.next();
					queue.offer(iterator);
					iteratorToNextEventMap.put(iterator, nextEvent);
				}
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return !queue.isEmpty();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public EventOccurrence next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			EventOccurrence result = findNextEvent();
			return result;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns the next event the iterator should return.  The queue and map are
		 * also updated for the next time this method is called.
		 * 
		 * @return the next element the iterator should return
		 */
		private EventOccurrence findNextEvent() {
			Iterator<EventOccurrence> iterator = queue.element();
			EventOccurrence nextEvent = iteratorToNextEventMap.get(iterator);
			
			// Check if the iterator at the front of the queue contains the next event
			if (nextEvent.underlyingEvent().number() == (lastEventNumber + 1)) {
				
				// Update the queue and map
				if (iterator.hasNext()) {
					iteratorToNextEventMap.put(iterator, iterator.next());
				}
				else {
					queue.remove();
					iteratorToNextEventMap.remove(iterator);
				}
				
				lastEventNumber++;
				return nextEvent;
			}
			// Check for the next event on another iterator (i.e. thread)
			else {
				queue.remove();
				queue.offer(iterator);
				return findNextEvent();
			}
		}
	}
}


/**
 * A {@code ContourCreationRecord.Exporter} used to extractor the
 * {@code ContourID} from the record.
 * 
 * @author Jeffrey K Czyz
 */
class ContourIDExtractor implements ContourCreationRecord.Exporter {
	
	private ContourID id;

	/**
	 * Returns the {@code ContourID} of the {@code ContourCreationRecord}.
	 * 
	 * @return the contour ID
	 */
	public ContourID getContourID() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourFormat(edu.bsu.cs.jive.contour.ContourFormat)
	 */
	public void addContourFormat(ContourFormat cf) {}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addContourID(ContourID id) {
		this.id = id;
	}
}