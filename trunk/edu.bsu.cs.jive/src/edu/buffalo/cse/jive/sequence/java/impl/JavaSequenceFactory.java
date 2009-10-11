package edu.buffalo.cse.jive.sequence.java.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.MessageReceive;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor;
import edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation;
import edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation;
import edu.buffalo.cse.jive.sequence.java.MethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;

/**
 * A factory class for creating elements for {@code JavaSequenceModel}s.  This
 * includes all varieties of {@code JavaExecutionActivation} and
 * {@code EventOccurrence}.
 * 
 * @see JavaExecutionActivation
 * @see EventOccurrence
 * @author Jeffrey K Czyz
 */
public class JavaSequenceFactory {
	
	// TODO Remove code.  This is a memory leak.
	// TODO Examine why this is a memory leak.
//	private static Map<SequenceModel, JavaSequenceFactory> cache =
//		new HashMap<SequenceModel, JavaSequenceFactory>();
//
//	public static JavaSequenceFactory instance(SequenceModel model) {
//		JavaSequenceFactory factory = cache.get(model);
//		if (factory == null) {
//			factory = new JavaSequenceFactory(model);
//			cache.put(model, factory);
//		}
//		
//		return factory;
//	}
	
	/**
	 * The model for which the factory will create elements.
	 */
	private final JavaSequenceModel model;
	
	/**
	 * Constructs a factory for the supplied model.
	 * 
	 * @param model the model for which the factory will create elements
	 */
	public JavaSequenceFactory(JavaSequenceModel model) {
		assert model != null;
		this.model = model;
	}
	
	/**
	 * Creates a {@code ThreadActivation} representing the thread with the
	 * supplied {@code ThreadID}.
	 * 
	 * @param thread the thread to model
	 * @return the thread activation for the supplied thread
	 */
	ThreadActivation createThreadActivation(final ThreadID thread) {
		ThreadActivation.Importer builder = new ThreadActivation.Importer() {
			
			public ContourID provideID() {
				return null;
			}
			
			public ContourID provideContext() {
				return null;
			}
			
			public long provideDuration() {
				return -1;
			}
			
			public List<EventOccurrence> provideEvents() {
				return new LinkedList<EventOccurrence>();
			}
			
			public MessageSend provideInitiator() {
				return null;
			}
			
			public MessageSend provideTerminator() {
				return null;
			}
			
			public SequenceModel provideContainingModel() {
				return model;
			}

			public ThreadID provideThread() {
				return thread;
			}
		};
		
		return new ThreadActivationImpl(builder);
	}
	
	/**
	 * Creates a {@code MethodActivation} for a method resulting from the supplied
	 * {@code CallEvent} occurring in the given activation.  As a side effect, a
	 * synchronous call {@code Message} is also created.
	 * 
	 * @param event the call event resulting in the creation of a method activation 
	 * @param source the originating activation
	 * @return the activation created from the supplied call event
	 */
	MethodActivation createMethodActivation(CallEvent event, JavaExecutionActivation source) {
		// Extract the ID and context from the event
		final CallTargetProvider provider = new CallTargetProvider();
		event.export(provider);
		
		// Construct the method activation
		MethodActivationImpl result;
		if (provider.getID() != null) {
			MethodActivation.Importer builder = new MethodActivation.Importer() {
				
				public ContourID provideID() {
					return provider.getID();
				}
				
				public ContourID provideContext() {
					return provider.getContext();
				}
				
				public long provideDuration() {
					return -1;
				}
				
				public List<EventOccurrence> provideEvents() {
					return new LinkedList<EventOccurrence>();
				}
				
				public MessageSend provideInitiator() {
					return null;
				}
				
				public MessageSend provideTerminator() {
					return null;
				}
				
				public SequenceModel provideContainingModel() {
					return model;
				}
			};
			result = new MethodActivationImpl(builder);
		}
		// TODO Separate this case out in a cleaner way
		else {
			result = (FilteredMethodActivationImpl) createFilteredMethodActivation(provider.getDescription());
		}
		
		// Construct the message and link the activation to it
		if (source instanceof AbstractActivation) {
			Message message = new MessageImpl(event, (AbstractActivation) source, result);
			result.setInitiator(message.sendEvent());
			return result;
		}
		else {
			throw new IllegalArgumentException("The source must have been created by this factory.");
		}
	}
	
	/**
	 * Terminates the source method activation by creating a reply {@code Message}
	 * to the destination activation.
	 * 
	 * @param event the return event resulting in the method termination
	 * @param source the originating activation for the message
	 * @param destination the destination activation for the message
	 * @return the reply message created between the activations
	 */
	Message terminateMethodActivation(ReturnEvent event, JavaExecutionActivation source, JavaExecutionActivation destination) {
		if (source instanceof AbstractActivation && destination instanceof AbstractActivation) {
			AbstractActivation sourceActivation = (AbstractActivation) source;
			return new MessageImpl(event, sourceActivation, (AbstractActivation) destination);
		}
		else {
			throw new IllegalArgumentException("The activations must have been created by this factory.");
		}
	}
	
	/**
	 * Creates a {@code FilteredMethodActivation}.
	 * 
	 * @return a filtered method activation
	 */
	FilteredMethodActivation createFilteredMethodActivation(final String description) {
		FilteredMethodActivation.Importer builder = new FilteredMethodActivation.Importer() {
			
			public ContourID provideID() {
				return null;
			}
			
			public ContourID provideContext() {
				return null;
			}
			
			public String provideDescription() {
				return description;
			}
			
			public long provideDuration() {
				return -1;
			}
			
			public List<EventOccurrence> provideEvents() {
				return new LinkedList<EventOccurrence>();
			}
			
			public MessageSend provideInitiator() {
				return null;
			}
			
			public MessageSend provideTerminator() {
				return null;
			}
			
			public SequenceModel provideContainingModel() {
				return model;
			}
		};
		
		return new FilteredMethodActivationImpl(builder);
	}
	
	/**
	 * Creates an {@code EventOccurrence} for the supplied {@code Event} occurring
	 * within the given {@code JavaExecutionActivation}.
	 * 
	 * @param event the event to wrap
	 * @param container the activation in which the event occurred
	 * @return the event occurrence for the supplied event
	 */
	EventOccurrence createEventOccurrence(final Event event, final JavaExecutionActivation container) {
		if (container instanceof AbstractActivation) {
			AbstractActivation activation = (AbstractActivation) container;
			EventOccurrenceImpl result = new EventOccurrenceImpl(event, activation);
			return result;
		}
		else {
			throw new IllegalArgumentException("The container must have been created by this factory.");
		}
	}
}
	
/**
 * An abstract implementation of a {@code JavaExecutionActivation}.
 * 
 * @author Jeffrey K Czyz
 */
abstract class AbstractActivation implements JavaExecutionActivation {
	
	/**
	 * The contour ID corresponding to the activation, or <code>null</code> if
	 * none exists.
	 */
	private ContourID id;
	
	/**
	 * The contour ID corresponding to the object or static context of the
	 * activation, or <code>null</code> if none exists.  This may be a virtual
	 * contour.
	 */
	private ContourID context;
	
	/**
	 * The number of events that have elapsed during the life of the activation. 
	 */
	private long duration;
	
	/**
	 * A list of events occurring within the activation.
	 */
	private List<EventOccurrence> events;
	
	/**
	 * The message send that initiated the creation of the activation, or
	 * <code>null</code> if none exists.
	 */
	private MessageSend initiator;
	
	/**
	 * The message send which terminated the activation, or <code>null</code> if
	 * none exists or if terminated by an exception.
	 */
	private MessageSend terminator;
	
	/**
	 * The model containing the activation.
	 */
	private JavaSequenceModel containingModel;
	
	/**
	 * A flag determining whether or not the activation has terminated.
	 */
	private boolean isTerminated = false;
	
	/**
	 * Updates the duration with the supplied value.
	 * 
	 * @param duration the new duration
	 */
	void setDuration(long duration) {
		this.duration = duration;
	}
	
	/**
	 * Adds an event occurrence to the activation.
	 * 
	 * @param event the event occurrence to add
	 */
	void addEvent(EventOccurrence event) {
		events.add(event);
	}
	
	/**
	 * Sets the initiator with the supplied value.
	 * 
	 * @param initiator the initiator
	 */
	void setInitiator(MessageSend initiator) {
		this.initiator = initiator; 
	}
	
	/**
	 * Sets the state of the activation to terminated by the supplied message
	 * send, effectively halting the activation's duration growth.
	 * 
	 * @param terminator the terminator
	 */
	void setTerminated(MessageSend terminator) {
		this.terminator = terminator;
		setTerminated();
	}
	
	/**
	 * Sets the state of the activation to terminated, effectively halting the
	 * activation's duration growth.
	 */
	void setTerminated() {
		isTerminated = true;
		long firstEventNumber = events.get(0).underlyingEvent().number();
		long lastEventNumber = containingModel.lastEventNumber();
		setDuration(lastEventNumber - firstEventNumber + 1);
	}
	
	/**
	 * Constructs the activation using the supplied builder.
	 * 
	 * @param builder the importer used to construct the activation
	 */
	public AbstractActivation(ExecutionOccurrence.Importer builder) {
		id = builder.provideID();
		context = builder.provideContext();
		duration = builder.provideDuration();
		events = builder.provideEvents();
		initiator = builder.provideInitiator();
		terminator = builder.provideTerminator();
		containingModel = (JavaSequenceModel) builder.provideContainingModel();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#export(edu.buffalo.cse.jive.sequence.ExecutionOccurrence.Exporter)
	 */
	public void export(Exporter exporter) {
		exporter.addID(id);
		exporter.addContext(context);
		exporter.addDuration(duration);
		List<EventOccurrence> temp = new ArrayList<EventOccurrence>(events.size());
		temp.addAll(events);
		exporter.addEvents(temp);
		exporter.addInitiator(initiator);
		exporter.addTerminator(terminator);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#id()
	 */
	public ContourID id() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#context()
	 */
	public ContourID context() {
		return context;
	}
	
	public boolean isTerminated() {
		return isTerminated;
	}
	
	// TODO Re-implement when method termination state is added
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#duration()
	 */
	public long duration() {
		if (isTerminated) {
			return duration;
		}
		else {
			long firstEventNumber = events.get(0).underlyingEvent().number();
			long lastEventNumber = containingModel.lastEventNumber();
			return lastEventNumber - firstEventNumber + 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#events()
	 */
	public List<EventOccurrence> events() {
		containingModel.checkLock();
		List<EventOccurrence> result = new ArrayList<EventOccurrence>(events.size());
		result.addAll(events);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#initiator()
	 */
	public MessageSend initiator() {
		return initiator;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#terminator()
	 */
	public MessageSend terminator() throws IllegalStateException {
		if (isTerminated()) {
			return terminator;
		}
		else {
			throw new IllegalStateException("The execution occurrence has not terminated.");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.ExecutionOccurrence#containingModel()
	 */
	public SequenceModel containingModel() {
		return containingModel;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return id + ", start = " + events().get(0).underlyingEvent().number();
	}
}

/**
 * An implementation of a {@code ThreadActivation}.
 * 
 * @author Jeffrey K Czyz
 */
class ThreadActivationImpl extends AbstractActivation implements ThreadActivation {
	
	/**
	 * The thread ID of the thread that the activation represents. 
	 */
	private ThreadID thread;
	
	/**
	 * Constructs the activation using the supplied builder.
	 * 
	 * @param builder the importer used to construct the activation
	 */
	public ThreadActivationImpl(ThreadActivation.Importer builder) {
		super(builder);
		thread = builder.provideThread();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.AbstractActivation#export(edu.buffalo.cse.jive.sequence.ExecutionOccurrence.Exporter)
	 */
	public void export(ThreadActivation.Exporter exporter) {
		super.export(exporter);
		exporter.addThread();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation#accept(edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor, java.lang.Object)
	 */
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.ThreadActivation#thread()
	 */
	public ThreadID thread() {
		return thread;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.AbstractActivation#duration()
	 */
	public long duration() {
		long firstEventNumber = events().get(0).underlyingEvent().number();
		long lastEventNumber = ((JavaSequenceModel) containingModel()).lastEventNumber(thread);
		return lastEventNumber - firstEventNumber + 1;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.AbstractActivation#toString()
	 */
	public String toString() {
		return thread.getName() + " (id = " + thread.getId() + ")";
	}
}

/**
 * An implementation of a {@code MethodActivation}.
 * 
 * @author Jeffrey K Czyz
 */
class MethodActivationImpl extends AbstractActivation implements MethodActivation {
	
	/**
	 * Constructs the activation using the supplied builder.
	 * 
	 * @param builder the importer used to construct the activation
	 */
	public MethodActivationImpl(MethodActivation.Importer builder) {
		super(builder);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation#accept(edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor, java.lang.Object)
	 */
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

/**
 * An implementation of a {@code FilteredMethodActivation}.
 * 
 * @author Jeffrey K Czyz
 */
class FilteredMethodActivationImpl extends MethodActivationImpl implements FilteredMethodActivation {
	
	private String description; 
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation#description()
	 */
	public String description() {
		return description;
	}
	
	/**
	 * Constructs the activation using the supplied builder.
	 * 
	 * @param builder the importer used to construct the activation
	 */
	public FilteredMethodActivationImpl(FilteredMethodActivation.Importer builder) {
		super(builder);
		description = builder.provideDescription();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.AbstractActivation#export(edu.buffalo.cse.jive.sequence.ExecutionOccurrence.Exporter)
	 */
	public void export(FilteredMethodActivation.Exporter exporter) {
		super.export(exporter);
		exporter.addDescription(description);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.MethodActivationImpl#accept(edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor, java.lang.Object)
	 */
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.java.impl.AbstractActivation#toString()
	 */
	public String toString() {
		return description + ", start = " + events().get(0).underlyingEvent().number();
	}
}

/**
 * An implementation of an {@code EventOccurrence}.
 * 
 * @author Jeffrey K Czyz
 */
class EventOccurrenceImpl implements EventOccurrence {
	
	/**
	 * The event wrapped by the event occurrence.
	 */
	private Event underlyingEvent;
	
	/**
	 * The execution occurrence in which the event occurred.
	 */
	private ExecutionOccurrence containingExecution;
	
	/**
	 * Constructs the event occurrence using the supplied event within the given
	 * execution occurrence.
	 * 
	 * @param event the underlying event
	 * @param execution the execution to contain the event
	 */
	EventOccurrenceImpl(Event event, AbstractActivation activation) {
		underlyingEvent = event;
		containingExecution = activation;
		activation.addEvent(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.EventOccurrence#accept(edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor, java.lang.Object)
	 */
	public void accept(EventVisitor visitor, Object arg) {
		visitor.visit(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.EventOccurrence#underlyingEvent()
	 */
	public Event underlyingEvent() {
		return underlyingEvent;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.EventOccurrence#containingExecution()
	 */
	public ExecutionOccurrence containingExecution() {
		return containingExecution;
	}
}

class MessageImpl implements Message {
	
	private MessageSort messageSort;
	
	private MessageKind messageKind;
	
	private MessageSend sendEvent;
	
	private MessageReceive receiveEvent;
	
	MessageImpl(CallEvent event, AbstractActivation source, AbstractActivation destination) {
		messageSort = MessageSort.SYNCH_CALL;
		messageKind = MessageKind.COMPLETE;
		initializeEvents(event, source, destination);
	}
	
	MessageImpl(ReturnEvent event, AbstractActivation source, AbstractActivation destination) {
		messageSort = MessageSort.REPLY;
		messageKind = MessageKind.COMPLETE;
		initializeEvents(event, source, destination);
		source.setTerminated(sendEvent);
	}
	
	private void initializeEvents(Event event, AbstractActivation source, AbstractActivation destination) {
		sendEvent = new MessageSendImpl(event, source);
		receiveEvent = new MessageReceiveImpl(event, destination);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.Message#messageKind()
	 */
	public MessageKind messageKind() {
		return messageKind;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.Message#messageSort()
	 */
	public MessageSort messageSort() {
		return messageSort;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.Message#sendEvent()
	 */
	public MessageSend sendEvent() {
		return sendEvent;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.Message#receiveEvent()
	 */
	public MessageReceive receiveEvent() {
		return receiveEvent;
	}
	
	private class MessageSendImpl extends EventOccurrenceImpl implements MessageSend {

		private MessageSendImpl(Event event, AbstractActivation activation) {
			super(event, activation);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.MessageSend#message()
		 */
		public Message message() {
			return MessageImpl.this;
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.java.impl.EventOccurrenceImpl#accept(edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor, java.lang.Object)
		 */
		public void accept(EventVisitor visitor, Object arg) {
			visitor.visit(this);
		}
	}
	
	private class MessageReceiveImpl extends EventOccurrenceImpl implements MessageReceive {

		private MessageReceiveImpl(Event event, AbstractActivation activation) {
			super(event, activation);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.MessageReceive#message()
		 */
		public Message message() {
			return MessageImpl.this;
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.java.impl.EventOccurrenceImpl#accept(edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor, java.lang.Object)
		 */
		public void accept(EventVisitor visitor, Object arg) {
			visitor.visit(this);
		}
	}
}

/**
 * An exporter used to extract the target from a {@code CallEvent} to be
 * provided for the creation of a {@code MethodActivation}.
 * 
 * @see #processCallEvent(CallEvent)
 * @author Jeffrey K Czyz
 */
class CallTargetProvider implements CallEvent.Exporter {
	
	/**
	 * The ID of the method contour resulting from the call event.
	 */
	private ContourID id;
	
	/**
	 * The contour ID of the context in which the resulting method occurs.
	 */
	private ContourID context;
	
	/**
	 * A description of the target for filtered calls.
	 */
	private String description;
	
	/**
	 * Returns the ID of the method contour resulting from the call event.
	 * 
	 * @return the ID of the method contour
	 */
	public ContourID getID() {
		return id;
	}
	
	/**
	 * Returns the contour ID of the context in which the resulting method
	 * occurs.
	 * 
	 * @return the ID of the execution context
	 */
	public ContourID getContext() {
		return context;
	}
	
	/**
	 * Returns the description of the target if it is out-of-model.
	 * 
	 * @return the description of the target, otherwise null
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addActualParams(java.util.List)
	 */
	public void addActualParams(List<Value> actuals) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addCaller(edu.bsu.cs.jive.events.CallEvent.Caller)
	 */
	public void addCaller(Caller caller) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addTarget(edu.bsu.cs.jive.events.CallEvent.Target)
	 */
	public void addTarget(Target target) {
		Target.Visitor visitor = new Target.Visitor() {

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.CallEvent.Target.Visitor#visit(edu.bsu.cs.jive.events.CallEvent.Target.InModel, java.lang.Object)
			 */
			public Object visit(Target.InModel target, Object obj) {
				id = target.contour();
				context = target.enclosing();
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.CallEvent.Target.Visitor#visit(edu.bsu.cs.jive.events.CallEvent.Target.OutOfModel, java.lang.Object)
			 */
			public Object visit(Target.OutOfModel target,	Object obj) {
				description = target.description();
				return null;
			}
		};
		target.accept(visitor, null);
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
	 */
	public void addNumber(long n) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
	 */
	public void addThreadID(ThreadID thread) {
		// do nothing
	}
}