package edu.buffalo.cse.jive.sequence.jivelog_adapter;

//import edu.bsu.cs.jive.events.AssignEvent;
//import edu.bsu.cs.jive.events.CallEvent;
//import edu.bsu.cs.jive.events.EOSEvent;
import java.util.concurrent.locks.ReentrantLock;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;
//import edu.bsu.cs.jive.events.ExceptionEvent;
//import edu.bsu.cs.jive.events.ExitEvent;
//import edu.bsu.cs.jive.events.LoadEvent;
//import edu.bsu.cs.jive.events.NewEvent;
//import edu.bsu.cs.jive.events.ReturnEvent;
//import edu.bsu.cs.jive.events.StartEvent;
import edu.buffalo.cse.jive.sequence.java.impl.JavaSequenceModel;

// TODO Determine if this class is necessary or if code should be moved here
//      from JavaSequenceModel.
/**
 * An event adapter used to convert JIVE events into additions to a
 * @code{JavaSequenceModel}.
 * 
 * @see Event
 * @see JavaSequenceModel
 * @author Jeffrey K Czyz
 */
public class JiveLogToSequenceModelAdapter implements EventSource.Listener {
	
	/**
	 * The model to which additions are being made.
	 */
	private final JavaSequenceModel model;
	
	/**
	 * Constructs a new event adapter for the supplied model.
	 * 
	 * @param model the model for which to adapt JIVE events
	 */
	public JiveLogToSequenceModelAdapter(JavaSequenceModel model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EventSource.Listener#eventOccurred(edu.bsu.cs.jive.events.EventSource, edu.bsu.cs.jive.events.Event)
	 */
	public void eventOccurred(EventSource source, Event event) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			model.addEvent(event);
		}
		finally {
			modelLock.unlock();
		}
	}
	
//	private Event.Visitor eventProcessor = new Event.Visitor() {
//
//		public Object visit(AssignEvent event, Object arg) {
//			event.export(assignEventAdapter);
//			assignEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(CallEvent event, Object arg) {
//			event.export(callEventAdapter);
//			callEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(EOSEvent event, Object arg) {
//			event.export(eosEventAdapter);
//			eosEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(ExceptionEvent event, Object arg) {
//			event.export(exceptionEventAdapter);
//			exceptionEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(ExitEvent event, Object arg) {
//			event.export(exitEventAdapter);
//			exitEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(LoadEvent event, Object arg) {
//			event.export(loadEventAdapter);
//			loadEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(NewEvent event, Object arg) {
//			event.export(newEventAdapter);
//			newEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(ReturnEvent event, Object arg) {
//			event.export(returnEventAdapter);
//			returnEventAdapter.apply(model, event);
//			return null;
//		}
//
//		public Object visit(StartEvent event, Object arg) {
//			event.export(startEventAdapter);
//			startEventAdapter.apply(model, event);
//			return null;
//		}
//	};
//	
//	private AssignEventAdapter assignEventAdapter = new AssignEventAdapter();
//	
//	private CallEventAdapter callEventAdapter = new CallEventAdapter();
//	
//	private EOSEventAdapter eosEventAdapter = new EOSEventAdapter();
//
//	private ExceptionEventAdapter exceptionEventAdapter = new ExceptionEventAdapter();
//	
//	private ExitEventAdapter exitEventAdapter = new ExitEventAdapter();
//	
//	private LoadEventAdapter loadEventAdapter = new LoadEventAdapter();
//	
//	private NewEventAdapter newEventAdapter = new NewEventAdapter();
//	
//	private ReturnEventAdapter returnEventAdapter = new ReturnEventAdapter();
//	
//	private StartEventAdapter startEventAdapter = new StartEventAdapter();
}
