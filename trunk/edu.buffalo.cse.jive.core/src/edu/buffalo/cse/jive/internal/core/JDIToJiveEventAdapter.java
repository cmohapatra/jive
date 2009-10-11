package edu.buffalo.cse.jive.internal.core;

import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.impl.EventFactory;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.InnerClassTester;
import edu.bsu.cs.jive.runtime.JDIEventHandler;
import edu.bsu.cs.jive.runtime.JDIRequestFilter;
import edu.bsu.cs.jive.runtime.builders.ExitEventBuilder;
import edu.bsu.cs.jive.runtime.builders.StartEventBuilder;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.core.builders.AssignEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.CallEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.CatchEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.EOSEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.LoadEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.NewEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.ReturnEventBuilder;
import edu.buffalo.cse.jive.internal.core.builders.ThrowEventBuilder;

/**
 * An adapter for converting JDI events to JIVE events.  Event conversion
 * involves ensuring that the appropriate contours exist and that JIVE's model
 * of the program's call stacks matches that of the JVM.  The
 * {@code ContourUtils} and {@code StackManager} classes help in this regard.
 * 
 * This class is also responsible for notifying listeners of JIVE events as they
 * are generated.  This task is delegated to an {@code EventDispatcher} job.
 * 
 * @author Jeffrey K Czyz
 * @see Event
 * @see EventSource
 * @see EventDispatcher
 * @see JDIEventHandler
 * @see ContourUtils
 * @see StackManager
 */
public class JDIToJiveEventAdapter implements EventSource, JDIEventHandler {
	
	/**
	 * A JDI event filter used to determine whether an event was filtered.
	 */
	private final JDIRequestFilter eventFilter;
	
	/**
	 * An event dispatcher used to notify listeners of events.
	 */
	private final EventDispatcher eventDispatcher;
	
	/**
	 * Utility class for contour, stack, local variable, and source code management.
	 */
	private final EventAdapterUtilities adapterUtilities;
	
	/**
	 * Utility class for contour management.
	 */
	private final ContourUtils contourManager;
	
	/**
	 * Utility class for call stack management.
	 */
	private final StackManager stackManager;
	
	/**
	 * Constructs the adapter with the supplied request filter.  The filter is
	 * used to determine when a thrown exception (as notified by an
	 * {@code ExceptionEvent}) should be tracked and when a {@code CatchEvent}
	 * should be created.  The same filter is used to limit the amount of other
	 * JDI events to produce.
	 * 
	 * @param filter the event request filter
	 */
	public JDIToJiveEventAdapter(JDIRequestFilter filter) {
		super();
		eventFilter = filter;
		eventDispatcher = new EventDispatcher();
		adapterUtilities = new EventAdapterUtilities();
		contourManager = adapterUtilities.getContourManager();
		stackManager = adapterUtilities.getStackManager();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AbstractEventSource#addListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void addListener(Listener listener) {
		eventDispatcher.addListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AbstractEventSource#removeListener(edu.bsu.cs.jive.events.EventSource.Listener)
	 */
	public void removeListener(Listener listener) {
		eventDispatcher.removeListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AbstractEventSource#fireEvent(edu.bsu.cs.jive.events.Event)
	 */
	private void fireEvent(Event e) {
		eventDispatcher.dispatchEvent(e);
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleLocation(com.sun.jdi.StackFrame, int)
	 */
	public void handleLocation(StackFrame frame, int frameIndex) {
		try {
			// do nothing
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleClassPrepare(com.sun.jdi.event.ClassPrepareEvent)
	 */
	public void handleClassPrepare(ClassPrepareEvent event) {
		try {
			ReferenceType type = event.referenceType();
			adapterUtilities.checkSourceInformation(type);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleThreadStart(com.sun.jdi.event.ThreadStartEvent)
	 */
	public void handleThreadStart(ThreadStartEvent event) {
		try {
			// do nothing
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleThreadDeath(com.sun.jdi.event.ThreadDeathEvent)
	 */
	public void handleThreadDeath(ThreadDeathEvent event) {
		try {
			ThreadReference thread = event.thread();
			while (stackManager.frameCount(thread) != 0) {
				if (stackManager.exceptionOccurred(thread)) {
					Location catchLocation = stackManager.outstandingException(thread).catchLocation();
					if (catchLocation != null) {
						Method catchingMethod = catchLocation.method();
						Method method = stackManager.peek(thread).location().method();
						if (method.equals(catchingMethod)) {
							stackManager.handleExceptionCaught(thread);
							processOutOfModelReturnEvent(thread);
						}
						else {
							processThrowEvent(thread, true);
						}
					}
					else {
						processThrowEvent(thread, true);
					}
				}
				else {
					processOutOfModelReturnEvent(thread);
				}
			}
			
			if (stackManager.exceptionOccurred(thread)) {
				stackManager.handleExceptionCaught(thread);
			}
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleExceptionThrown(com.sun.jdi.event.ExceptionEvent)
	 */
	@SuppressWarnings("unchecked")
	public void handleExceptionThrown(ExceptionEvent event) {
		try {
			// Check if the exception was thrown in-model
			ReferenceType throwingType = event.location().declaringType();
			if (eventFilter.acceptsClass(throwingType.name())) {
				stackManager.handleExceptionThrown(event);
			}
			else {
				// Check if the exception wasn't caught
				Location catchLocation = event.catchLocation();
				if (catchLocation == null) {
					// Check if there are any in-model frames on the stack
					if (containsInModelFrames(event.thread())) {
						stackManager.handleExceptionThrown(event);
					}
				}
				else {
					// Check if the exception was caught in-model
					ReferenceType catchingType = catchLocation.declaringType();
					if (eventFilter.acceptsClass(catchingType.name())) {
						stackManager.handleExceptionThrown(event);
					}
					else {
						// Check if there are any in-model frames between the
						// throw and catch locations
						if (containsInModelFrames(event.thread(), catchLocation)) {
							stackManager.handleExceptionThrown(event);
						}
					}
				}
			}
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}
	
	/**
	 * Returns whether the given thread contains any in-model frames (i.e.,
	 * whether there is a frame on the stack with a corresponding contour).
	 * 
	 * @param thread the thread to examine
	 * @return <code>true</code> if the thread contains an in-model frame,
	 *         <code>false</code> otherwise
	 * @throws IncompatibleThreadStateException if the frames are not available
	 */
	@SuppressWarnings("unchecked")
	private boolean containsInModelFrames(ThreadReference thread) throws IncompatibleThreadStateException {
		List<StackFrame> stack = thread.frames();
		for (StackFrame frame : stack) {
			ReferenceType type = frame.location().declaringType();
			if (eventFilter.acceptsClass(type.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns whether the given thread contains any in-model frames (i.e.,
	 * whether there is a frame on the stack with a corresponding contour)
	 * between the top frame and the frame matching the supplied location.
	 * 
	 * @param thread the thread to examine
	 * @param catchLocation the location limiting the search
	 * @return <code>true</code> if the thread contains an in-model frame,
	 *         <code>false</code> otherwise
	 * @throws IncompatibleThreadStateException if the frames are not available
	 */
	@SuppressWarnings("unchecked")
	private boolean containsInModelFrames(ThreadReference thread, Location catchLocation) throws IncompatibleThreadStateException {
		List<StackFrame> stack = thread.frames();
		for (StackFrame frame : stack) {
			ReferenceType type = frame.location().declaringType();
			if (eventFilter.acceptsClass(type.name())) {
				return true;
			}
			
			if (frame.location().method().equals(catchLocation.method())) {
				return false;
			}
		}
		
		return false;
	}
	

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleMethodEntry(com.sun.jdi.event.MethodEntryEvent)
	 */
	public void handleMethodEntry(MethodEntryEvent event) {
		try {
			ThreadReference thread = event.thread();
			Method method = event.method();
			StackFrame frame = determineStackFrame(event);
			
			assert frame.location().method().equals(event.method());
			
			// Ensure that the necessary contours exist
			if (method.isStatic() || method.isNative()) {
				ReferenceType type = method.declaringType();
				ensureStaticContourExistsFor(type, thread);
			}
			else {
				ObjectReference object = frame.thisObject();
				ReferenceType type = object.referenceType();
				ensureStaticContourExistsFor(type, thread);
				ensureObjectContoursExistFor(object, thread);
			}
			
			processInModelCallEvent(event, frame);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleMethodExit(com.sun.jdi.event.MethodExitEvent)
	 */
	public void handleMethodExit(MethodExitEvent event) {
		try {
			StackFrame frame = determineStackFrame(event);
			assert frame.location().method().equals(event.method());
			
			processInModelReturnEvent(event);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleStep(com.sun.jdi.event.StepEvent)
	 */
	public void handleStep(StepEvent event) {
		try {
			// TODO Implement local variable changes
			StackFrame frame = determineStackFrame(event);
			assert frame.location().method().equals(event.location().method());
			
			ThreadReference thread = event.thread();
			Location location = event.location();
			ReferenceType type = location.method().declaringType();
			
			// Generate EOS events if the source is available
			if (adapterUtilities.isSourceAvailable(type)) {
				processEOSEvent(thread, location);
			}
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleVMDeath(com.sun.jdi.event.VMDeathEvent)
	 */
	public void handleVMDeath(VMDeathEvent event) {
		try {
			ExitEventBuilder builder = new ExitEventBuilder(event, contourManager);
			ExitEvent exitEvent = EventFactory.instance().createExitEvent(builder);
			fireEvent(exitEvent);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleVMDisconnect(com.sun.jdi.event.VMDisconnectEvent)
	 */
	public void handleVMDisconnect(VMDisconnectEvent event) {
		try {
			ExitEventBuilder builder = new ExitEventBuilder(event, contourManager);
			ExitEvent exitEvent = EventFactory.instance().createExitEvent(builder);
			fireEvent(exitEvent);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleVMStart(com.sun.jdi.event.VMStartEvent)
	 */
	public void handleVMStart(VMStartEvent event) {
		try {
			StartEventBuilder builder = new StartEventBuilder(event, contourManager);
			StartEvent startEvent = EventFactory.instance().createStartEvent(builder);
			fireEvent(startEvent);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.runtime.JDIEventHandler#handleWatchpoint(com.sun.jdi.event.ModificationWatchpointEvent)
	 */
	public void handleWatchpoint(ModificationWatchpointEvent event) {
		try {
			StackFrame frame = determineStackFrame(event);
			assert frame.location().method().equals(event.location().method());
			
			ThreadReference thread = event.thread();
			Field field = event.field();

			// Ensure that the necessary contours exist
			if (field.isStatic()) {
				ReferenceType type = field.declaringType();
				ensureStaticContourExistsFor(type, thread);
			}
			else {
				ObjectReference object = event.object();
				ensureObjectContoursExistFor(object, thread);
			}
			
			processAssignEvent(event);
		}
		catch (Throwable e) {
			JiveCorePlugin.log(e);
		}
	}
	
	private void ensureStaticContourExistsFor(ReferenceType type, ThreadReference thread) {
		// ArrayType and InterfaceType not supported
		if (type instanceof ClassType) {
			ensureStaticContourExistsFor((ClassType) type, thread);
		}
		else {
			throw new IllegalArgumentException("Class type expected.");
		}
	}
	
	private void ensureStaticContourExistsFor(ClassType type, ThreadReference thread) {
		if (!contourManager.staticContourExistsFor(type.name())) {
			adapterUtilities.checkSourceInformation(type);
			processLoadEvent(type, thread);
		}
	}
	
	private void ensureObjectContoursExistFor(ObjectReference object, ThreadReference thread) {
		if (!contourManager.instanceContourExistsFor(object)) {
			processNewEvent(object, thread);
		}
	}

	private void processLoadEvent(ClassType type, ThreadReference thread) {
		// Non-static inner classes don't have static contexts
		if (InnerClassTester.isInnerClass(type) && !type.isStatic()) {
			return;
		}

		// Recursively ensure a static contour exists for the superclass type
		ClassType superType = type.superclass();
		if (superType != null) {
			ensureStaticContourExistsFor(superType, thread);
		}

		// Create and fire a Load event for the type
		boolean isInModel = eventFilter.acceptsClass(type.name());
		LoadEventBuilder eventBuilder = LoadEventBuilder.create(type, thread, isInModel, contourManager, stackManager);
		LoadEvent event = EventFactory.instance().createLoadEvent(eventBuilder);
		fireEvent(event);
	}
	
	@SuppressWarnings("unchecked")
	private void processNewEvent(ObjectReference object, ThreadReference thread) {
		// Ensure an object contour exists for the enclosing object, if any
		ContourID enclosingContourID = null;
		List<Field> fields = object.referenceType().fields();
		for (Field field : fields) {
			if (field.name().startsWith("this$")) {
				Value value = object.getValue(field);
				if (value != null) {
					ObjectReference enclosingObject = (ObjectReference) value;
					ensureObjectContoursExistFor(enclosingObject, thread);
					enclosingContourID = contourManager.getInstanceContourID(enclosingObject, field.typeName());
					break;
				}
			}
		}
		
		// Create and fire a New event for the object
		NewEventBuilder eventBuilder = NewEventBuilder.create(object, thread, contourManager, stackManager, eventFilter, enclosingContourID);
		NewEvent event = EventFactory.instance().createNewEvent(eventBuilder);
		fireEvent(event);
	}
	
	/**
	 * Creates and fires an in-model {@code CallEvent} from the given
	 * {@code MethodEntryEvent} and {@code StackFrame}.  The stack frame
	 * represents the method activation resulting from the call.
	 * 
	 * @param entryEvent the method entry event to convert
	 * @param frame the stack frame of the resulting method activation.
	 */
	private void processInModelCallEvent(MethodEntryEvent entryEvent, StackFrame frame) {
		CallEventBuilder builder = CallEventBuilder.create(entryEvent, frame, contourManager, stackManager);
		CallEvent event = EventFactory.instance().createCallEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Create and fires an out-of-model {@code CallEvent} for the method
	 * activation represented by the given {@code StackFrame}.
	 * 
	 * @param thread the thread of the stack frame
	 * @param frame the stack frame of the resulting method activation
	 */
	private void processOutOfModelCallEvent(ThreadReference thread, StackFrame frame) {
		CallEventBuilder builder = CallEventBuilder.create(thread, frame, contourManager, stackManager);
		CallEvent event = EventFactory.instance().createCallEvent(builder);
		fireEvent(event);
	}

	/**
	 * Creates and fires an in-model {@code ReturnEvent} from the given
	 * {@code MethodExitEvent}.
	 * 
	 * @param exitEvent the method exit event to convert
	 */
	private void processInModelReturnEvent(MethodExitEvent exitEvent) {
		ReturnEventBuilder builder = ReturnEventBuilder.create(exitEvent, contourManager, stackManager);
		ReturnEvent event = EventFactory.instance().createReturnEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Creates and fires an out-of-model {@code ReturnEvent} for the topmost
	 * stack frame of the given thread.
	 * 
	 * @param thread the thread for which to generate a return event
	 */
	private void processOutOfModelReturnEvent(ThreadReference thread) {
		ReturnEventBuilder builder = ReturnEventBuilder.create(thread, contourManager, stackManager);
		ReturnEvent event = EventFactory.instance().createReturnEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Creates and fires a {@code ThrowEvent} for the topmost stack frame of the
	 * given thread.  Throw events are generated for the stack frame where the
	 * exception is thrown (regardless if it is caught there) and for any
	 * subsequent stack frame that does not handle the exception.
	 * 
	 * @param thread the thread for which to generate a throw event
	 * @param framePopped whether the throw event results in a popped stack frame
	 */
	private void processThrowEvent(ThreadReference thread, boolean framePopped) {
		ThrowEventBuilder builder = ThrowEventBuilder.create(thread, framePopped, contourManager, stackManager);
		ThrowEvent event = EventFactory.instance().createThrowEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Creates and fires a {@code CatchEvent} for the method activation
	 * represented by the given {@code StackFrame}, which must be a valid
	 * object.  A {@code StackFrame} object is valid if it is obtained from a
	 * suspended VM.  It is invalidated once the VM is resumed.  
	 * 
	 * @param thread the of the stack frame
	 * @param validFrame a valid stack frame where the exception was caught
	 */
	private void processCatchEvent(ThreadReference thread, StackFrame validFrame) {
		CatchEventBuilder builder = CatchEventBuilder.create(thread, validFrame, contourManager, stackManager, adapterUtilities);
		CatchEvent event = EventFactory.instance().createCatchEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Creates and fires an {@code EOSEvent} for the given {@code Location} on
	 * the topmost stack frame.  The location must represent a valid source path
	 * and line number.
	 * 
	 * @param thread the thread for which to generate an EOS event
	 * @param location the location to convert to an EOS event
	 * @throws AbsentInformationException if the source information is not available
	 */
	private void processEOSEvent(ThreadReference thread, Location location) throws AbsentInformationException {
		EOSEventBuilder builder = EOSEventBuilder.create(thread, location, contourManager, stackManager);
  		EOSEvent event = EventFactory.instance().createEOSEvent(builder);
  		fireEvent(event);
	}
	
	/**
	 * Creates and fires an {@code AssignEvent} from the given
	 * {@code ModificationWatchpointEvent} on the topmost stack frame.
	 * 
	 * @param watchpointEvent the modification watchpoint event to convert
	 */
	private void processAssignEvent(ModificationWatchpointEvent watchpointEvent) {
		AssignEventBuilder builder = AssignEventBuilder.create(watchpointEvent, contourManager, stackManager);
		AssignEvent event = EventFactory.instance().createAssignEvent(builder);
		fireEvent(event);
	}
	
	/**
	 * Determines the correct stack frame for the {@code LocatableEvent}.  The
	 * method associated with the event is normally the top frame (number 0)
	 * on the thread's stack.  However, in some situations this not the case.
	 * This has been observed when a {@code CallEvent} for an {@code Enum}'s
	 * {@code <clinit>} is generated.  In this case, additional stack frames
	 * exist.  Therefore, the correct frame needs to be determined.
	 * <p>
	 * This method is also responsible for generating throw and catch events as
	 * well as out-of-model call and return events. 
	 * 
	 * @param event the event
	 * @param utils contour utilities
	 * @return the correct stack frame
	 * @throws IncompatibleThreadStateException
	 */
	private StackFrame determineStackFrame(LocatableEvent event) throws IncompatibleThreadStateException {
		// The stack size according to JIVE should be the same as reported by the JVM 
		ThreadReference thread = event.thread();
		int jdiStackSize = thread.frameCount();
		int jiveStackSize = stackManager.frameCount(thread);
		
		// Except when a method is being called it will be one-off
		boolean methodEntered = event instanceof MethodEntryEvent;
		int modifier = methodEntered ? 1 : 0;
		int index = jdiStackSize - jiveStackSize - modifier;  // 0 == top frame
		
		// Special handling is needed if an exception has occurred on the thread
		boolean exceptionOccurred = stackManager.exceptionOccurred(thread);
		int framesPopped = 0;
		
		// Find the the "top" stack frame
		int top = 0;
		StackFrame frame = thread.frame(top);
		if (methodEntered) {
			while (!event.location().method().equals(frame.location().method())) {
				top++;
				frame = thread.frame(top);
			}
		}
		
		// Less frames on the JVM stack
		if (index < top) {
			while (index != top) {
				index++;
				if (exceptionOccurred) {
					processThrowEvent(thread, true);
					framesPopped++;
				}
				else {
					processOutOfModelReturnEvent(thread);
				}
			}
		}
		
		// Check for different (out-of-model) frames on the JVM stack
		if (methodEntered) {
			while (!stackManager.isEmpty(thread)) {
				// Stop at an in-model frame
				StackFrame topFrame = stackManager.peek(thread);
				if (stackManager.isFrameInModel(topFrame)) {
					break;
				}
				else {
					// Stop once a matching frame is found
					Method topMethod = topFrame.location().method();
					if (topMethod.equals(thread.frame(index + 1).location().method())) {
						break;
					}
					else {
						// Generate the appropriate events
						index++;
						if (exceptionOccurred) {
							Location catchLocation = stackManager.outstandingException(thread).catchLocation();
							if (catchLocation != null && topMethod.equals(catchLocation.method())) {
								stackManager.handleExceptionCaught(thread);
								exceptionOccurred = false;
								processOutOfModelReturnEvent(thread);
							}
							else {
								processThrowEvent(thread, true);
								framesPopped++;
							}
						}
						else {
							processOutOfModelReturnEvent(thread);
						}
					}
				}
			}
		}
		
		// More frames on the JVM stack
		frame = (index == top) ? frame : thread.frame(index);
		while (index > top) {
			processOutOfModelCallEvent(thread, frame);
			index--;
			frame = thread.frame(index);
		}
		
		// Generate exception-related events if needed
		if (exceptionOccurred) {
			StackFrame catchFrame = thread.frame(index + modifier);
			
			// Only generate a catch event if caught in-model
			ReferenceType type = catchFrame.location().method().declaringType();
			if (eventFilter.acceptsClass(type.name())) {
				
				// Generate a throw event if the exception was caught where it was thrown
				if (framesPopped == 0) {
					processThrowEvent(thread, false);
				}
				
				processCatchEvent(thread, catchFrame);
			}
			
			stackManager.handleExceptionCaught(thread);
		}
		
		return frame;
	}
}