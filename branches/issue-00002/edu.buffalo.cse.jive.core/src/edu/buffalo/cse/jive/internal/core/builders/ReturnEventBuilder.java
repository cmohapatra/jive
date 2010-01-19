package edu.buffalo.cse.jive.internal.core.builders;

import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.MethodExitEvent;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.impl.UninitializedValue;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class ReturnEventBuilder extends AbstractEventBuilder implements ReturnEvent.Importer {

	private static final String VOID_TYPE_NAME = "void";
	
	private Returner previousContext;
	
	private Value returnValue;
	
	public static ReturnEventBuilder create(MethodExitEvent event, ContourUtils contourManager, StackManager stackManager) {
		ThreadReference thread = event.thread();
		ThreadID threadID = stackManager.threadID(thread);
		Returner previousContext = determinePreviousContext(thread, stackManager);
		Value returnValue = determineReturnValue(event, contourManager);
		
		return new ReturnEventBuilder(threadID, previousContext, returnValue, contourManager);
	}
	
	public static ReturnEventBuilder create(ThreadReference thread, ContourUtils contourManager, StackManager stackManager) {		
		ThreadID threadID = stackManager.threadID(thread);
		Returner previousContext = determinePreviousContext(thread, stackManager);
		Value returnValue = UninitializedValue.instance();
		
		return new ReturnEventBuilder(threadID, previousContext, returnValue, contourManager);
	}
	
	private static Returner determinePreviousContext(ThreadReference thread, StackManager stackManager) {
		StackFrame frame = stackManager.peek(thread);
		Returner result = stackManager.createReturner(frame);
		
		stackManager.pop(thread);
		return result;
	}
	
	private static Value determineReturnValue(MethodExitEvent event, ContourUtils contourManager) {
		// TODO Determine if we should handle void types differently
		if (event.method().returnTypeName().equals(VOID_TYPE_NAME)) {
			return null;
		}
		
		// Java SE 6 or later supports method return values
		if (event.virtualMachine().canGetMethodReturnValues()) {
			return ValueFactory.instance().createValue(event.returnValue(), contourManager);
		}
		// Support for J2SE 1.5
		else {
			return UninitializedValue.instance();
		}
	}
	
	private ReturnEventBuilder(ThreadID thread, Returner previousContext, Value returnValue, ContourUtils contourManager) {
		super(thread, contourManager);
		this.previousContext = previousContext;
		this.returnValue = returnValue;
	}
	
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Importer#providePreviousContext()
	 */
	public Returner providePreviousContext() {
		return previousContext;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ReturnEvent.Importer#provideReturnValue()
	 */
	public Value provideReturnValue() {
		return returnValue;
	}

}
