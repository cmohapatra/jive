package edu.buffalo.cse.jive.internal.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ExceptionEvent;

import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.MethodContourCreationRecordBuilder;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.factories.ThreadIDFactory;

public class StackManager {
	
	private ContourUtils contourManager;
	
	private ThreadIDFactory threadIDFactory;
	
	private Map<Long, Deque<StackFrame>> threadToStackMap;
	
	private Map<Long, ExceptionEvent> threadToExceptionMap;
	
	private Map<StackFrame, ContourID> frameToIdMap;
	
	private Set<StackFrame> inModelSet;
	
	public StackManager(ContourUtils utils) {
		contourManager = utils;
		threadIDFactory = new ThreadIDFactory();
		threadToStackMap = new HashMap<Long, Deque<StackFrame>>();
		threadToExceptionMap = new HashMap<Long, ExceptionEvent>();
		frameToIdMap = new HashMap<StackFrame, ContourID>();
		inModelSet = new HashSet<StackFrame>();
	}
	
	public ThreadID threadID(ThreadReference thread) {
		return threadIDFactory.create(thread);
	}
	
	public void handleExceptionThrown(ExceptionEvent event) {
		ThreadReference thread = event.thread();
		threadToExceptionMap.put(thread.uniqueID(), event);
	}
	
	public void handleExceptionCaught(ThreadReference thread) {
		threadToExceptionMap.remove(thread.uniqueID());
	}
	
	public boolean exceptionOccurred(ThreadReference thread) {
		return threadToExceptionMap.containsKey(thread.uniqueID());
	}
	
	public ExceptionEvent outstandingException(ThreadReference thread) {
		if (threadToExceptionMap.containsKey(thread.uniqueID())) {
			return threadToExceptionMap.get(thread.uniqueID());
		}
		else {
			throw new IllegalStateException("An exception has not occurred on the thread.");
		}
	}
	
	public void push(ThreadReference thread, StackFrame frame, boolean isInModel) {
		Deque<StackFrame> stack = getStack(thread);
		stack.push(frame);
		
		if (isInModel) {
			inModelSet.add(frame);
		}
	}
	
	public StackFrame pop(ThreadReference thread) {
		Deque<StackFrame> stack = getStack(thread);
		StackFrame frame = stack.pop();
		
		if (inModelSet.contains(frame)) {
			inModelSet.remove(frame);
		}
		
		if (frameToIdMap.containsKey(frame)) {
			frameToIdMap.remove(frame);
		}
		
		return frame;
	}
	
	public StackFrame peek(ThreadReference thread) {
		Deque<StackFrame> stack = getStack(thread);
		return stack.peek();
	}
	
	public int frameCount(ThreadReference thread) {
		Deque<StackFrame> stack = getStack(thread);
		return stack.size();
	}
	
	public boolean isEmpty(ThreadReference thread) {
		Deque<StackFrame> stack = getStack(thread);
		return stack.isEmpty();
	}
	
	public boolean isFrameInModel(StackFrame frame) {
		return inModelSet.contains(frame);
	}
	
	private Deque<StackFrame> getStack(ThreadReference thread) {
		Deque<StackFrame> stack = threadToStackMap.get(thread.uniqueID());
		
		if (stack == null) {
			stack = new ArrayDeque<StackFrame>();
			threadToStackMap.put(thread.uniqueID(), stack);
		}
		
		return stack;
	}
	
	public Caller createCaller(StackFrame frame) {
		if (inModelSet.contains(frame)) {
			ContourID id = determineContourID(frame, null); // TODO Make a lookup method instead of using null
			return new InModelCallerImpl(id);
		}
		else {
			String description = createDescription(frame);
			return new OutOfModelCallerImpl(description);
		}
	}
	
	public Target createTarget(StackFrame frame) {
		if (inModelSet.contains(frame)) {
			ContourID context = determineContext(frame);
			ContourID id = determineContourID(frame, context);
			Method method = frame.location().method();
			ThreadID thread = threadID(frame.thread());
			return new InModelTargetImpl(context, method, id, thread);
		}
		else {
			String description = createDescription(frame);
			return new OutOfModelTargetImpl(description);
		}
	}
	
	public Returner createReturner(StackFrame frame) {
		if (inModelSet.contains(frame)) {
			ContourID id = determineContourID(frame, null); // TODO Make a lookup method instead of using null
			return new InModelReturnerImpl(id);
		}
		else {
			String description = createDescription(frame);
			return new OutOfModelReturnerImpl(description);
		}
	}
	
	public Thrower createThrower(StackFrame frame) {
		if (inModelSet.contains(frame)) {
			ContourID id = determineContourID(frame, null); // TODO Make a lookup method instead of using null
			return new InModelThrowerImpl(id);
		}
		else {
			String description = createDescription(frame);
			return new OutOfModelThrowerImpl(description);
		}
	}
	
	public ContourID createCatcher(StackFrame frame) {
		if (inModelSet.contains(frame)) {
			ContourID id = determineContourID(frame, null);  // TODO Make a lookup method instead of using null
			return id;
		}
		else {
			throw new IllegalStateException("A contour ID was not found for the given stack frame.");
		}
	}
	
	private ContourID determineContext(StackFrame frame) {
		Method method = frame.location().method();
		ReferenceType type = method.declaringType();
		ContourID context;
		
		if (method.isStatic() || method.isNative()) {
			context = contourManager.getStaticContourID(type.name());
		}
		else {
			ObjectReference object = frame.thisObject();
			context = contourManager.getInstanceContourID(object, type.name());
		}
		
		return context;
	}
	
	private ContourID determineContourID(StackFrame frame, ContourID context) {
		if (frameToIdMap.containsKey(frame)) {
			return frameToIdMap.get(frame);
		}
		else {
			Method method = frame.location().method();
			ContourID id = contourManager.createMethodContourID(method, context);
			frameToIdMap.put(frame, id);
			return id;
		}
	}
	
	private String createDescription(StackFrame frame) {
		Method method = frame.location().method();
		ReferenceType type = method.declaringType();
		return type.name() + "." + method.name() + (method.isSynthetic() ? " <synthetic>" : "");
	}
}

abstract class AbstractInModelStackFrame {
	
	private final ContourID id;
	
	public AbstractInModelStackFrame(ContourID id) {
		assert id != null;
		this.id = id;
	}

	public ContourID contour() {
		return id;
	}
	
	public String toString() {
		return id.toString();
	}
}

final class InModelCallerImpl extends AbstractInModelStackFrame implements Caller.InModel {
	
	public InModelCallerImpl(ContourID id) {
		super(id);
	}

	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class InModelTargetImpl extends AbstractInModelStackFrame implements Target.InModel {

	private final ContourID enclosing;

	private final MethodContourCreationRecord ccr;

	public InModelTargetImpl(ContourID enclosingContourID, Method method, ContourID methodID, ThreadID threadID) {
		super(methodID);
		enclosing = enclosingContourID;
		ccr = new MethodContourCreationRecord(MethodContourCreationRecordBuilder.create(methodID, method, threadID));
	}
	
	public ContourID enclosing() {
		return enclosing;
	}

	public void export(Exporter e) {
		e.addContourCreationRecord(ccr);
	}

	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class InModelReturnerImpl extends AbstractInModelStackFrame implements Returner.InModel {

	public InModelReturnerImpl(ContourID id) {
		super(id);
	}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class InModelThrowerImpl extends AbstractInModelStackFrame implements Thrower.InModel {

	public InModelThrowerImpl(ContourID id) {
		super(id);
	}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

abstract class AbstractOutOfModelStackFrame {
	
	private final String description;
	
	public AbstractOutOfModelStackFrame(String description) {
		this.description = description;
	}
	
	public String description() {
		return description;
	}
	
	public String toString() {
		return description;
	}
}

final class OutOfModelCallerImpl extends AbstractOutOfModelStackFrame implements Caller.OutOfModel {
	
	public OutOfModelCallerImpl(String description) {
		super(description);
	}

	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class OutOfModelTargetImpl extends AbstractOutOfModelStackFrame implements Target.OutOfModel {

	public OutOfModelTargetImpl(String description) {
		super(description);
	}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class OutOfModelReturnerImpl extends AbstractOutOfModelStackFrame implements Returner.OutOfModel {

	public OutOfModelReturnerImpl(String description) {
		super(description);
	}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}

final class OutOfModelThrowerImpl extends AbstractOutOfModelStackFrame implements Thrower.OutOfModel {

	public OutOfModelThrowerImpl(String description) {
		super(description);
	}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this, arg);
	}
}
