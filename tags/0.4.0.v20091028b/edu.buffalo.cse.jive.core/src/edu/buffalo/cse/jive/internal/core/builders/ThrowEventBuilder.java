package edu.buffalo.cse.jive.internal.core.builders;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ExceptionEvent;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class ThrowEventBuilder extends AbstractEventBuilder implements ThrowEvent.Importer {

	private Thrower thrower;
	
	private Value exception;
	
	private boolean framePopped;
	
	public static ThrowEventBuilder create(ThreadReference thread, ObjectReference exception, ContourUtils contourManager, StackManager stackManager) {
		ThreadID threadID = stackManager.threadID(thread);
		Thrower thrower = determineThrower(thread, stackManager, false);
		Value exceptionValue = ValueFactory.instance().createValue(exception, contourManager);
		
		return new ThrowEventBuilder(threadID, thrower, exceptionValue, false, contourManager);
	}
	
	public static ThrowEventBuilder create(ThreadReference thread, boolean framePopped, ContourUtils contourManager, StackManager stackManager) {
		ThreadID threadID = stackManager.threadID(thread);
		Thrower thrower = determineThrower(thread, stackManager, framePopped);
		Value exception = determineException(thread, stackManager, contourManager);
		
		return new ThrowEventBuilder(threadID, thrower, exception, framePopped, contourManager);
	}
	
	public static ThrowEventBuilder create(ThreadReference thread, ContourUtils contourManager, StackManager stackManager) {
		ThreadID threadID = stackManager.threadID(thread);
		Thrower thrower = determineThrower(thread, stackManager, true);
		Value exception = determineException(thread, stackManager, contourManager);
		
		return new ThrowEventBuilder(threadID, thrower, exception, true, contourManager);
	}
	
	private static Thrower determineThrower(ThreadReference thread, StackManager stackManager, boolean popFrame) {
		StackFrame frame = stackManager.peek(thread);
		Thrower result = stackManager.createThrower(frame);
		
		if (popFrame) {
			stackManager.pop(thread);
		}
		
		return result;
	}
	
	private static Value determineException(ThreadReference thread, StackManager stackManager, ContourUtils contourManager) {
		ExceptionEvent event = stackManager.outstandingException(thread);
		ObjectReference exception = event.exception();
		return ValueFactory.instance().createValue(exception, contourManager);
	}
	
	private ThrowEventBuilder(ThreadID thread, Thrower thrower, Value exception, boolean framePopped, ContourUtils contourManager) {
		super(thread, contourManager);
		this.thrower = thrower;
		this.exception = exception;
		this.framePopped = framePopped;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Importer#provideThrower()
	 */
	public Thrower provideThrower() {
		return thrower;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Importer#provideException()
	 */
	public Value provideException() {
		return exception;
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent.Importer#provideFramePopped()
	 */
	public boolean provideFramePopped() {
		return framePopped;
	}
}
