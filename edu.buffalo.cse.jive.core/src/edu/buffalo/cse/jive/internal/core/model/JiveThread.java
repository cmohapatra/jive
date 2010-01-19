package edu.buffalo.cse.jive.internal.core.model;

import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThreadAdapter;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;

import edu.bsu.cs.jive.runtime.JDIEventHandler;
import edu.bsu.cs.jive.runtime.JDIRequestFilter;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.core.IJiveThread;

public class JiveThread extends JDIThreadAdapter implements IJiveThread {
	
	private JiveStepHandler fJiveStepHandler;
	
	public JiveThread(JDIDebugTarget target, ThreadReference thread) throws ObjectCollectedException {
		super(target, thread);
	}
	
	public void initialize() {
	// TODO(jkczyz):  Determine the best way to handle clones.
//		JiveDebugTarget target = (JiveDebugTarget) getDebugTarget();
//		if (target.getVM().canGetMethodReturnValues()) {
//			new CloneMethodExitHandler();
//		}
		
		fJiveStepHandler = new JiveStepHandler();
		fJiveStepHandler.createRequest();
		super.initialize();
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
//		return JiveCorePlugin.PLUGIN_ID;
		return getDebugTarget().getModelIdentifier();
	}
	
	public IJiveDebugTarget getDebugTarget() {
		return (IJiveDebugTarget) super.getDebugTarget();
	}
	
	/**
	 * Sets the step handler currently handling a step
	 * request.
	 * 
	 * @param handler the current step handler, or <code>null</code>
	 * 	if none
	 */
	protected void setPendingStepHandler(JDTStepHandler handler) {
		super.setPendingStepHandler(handler);
	}
	
	/**
	 * Returns the step handler currently handling a step
	 * request, or <code>null</code> if none.
	 * 
	 * @return step handler, or <code>null</code> if none
	 */
	protected JDTStepHandler getPendingStepHandler() {
		return (JDTStepHandler) (super.getPendingStepHandler());
	}
	
	protected class JiveStepHandler implements IJDIEventListener {
		
		private StepRequest fRequest;
		
		private JDIEventHandler fEventHandler;
		
		protected JiveStepHandler() {
			fEventHandler = ((JiveDebugTarget) getDebugTarget()).getEventAdapter();
		}
		
		/**
		 * @return the fRequest
		 */
		protected StepRequest getRequest() {
			return fRequest;
		}

		/**
		 * @param request the fRequest to set
		 */
		protected void setRequest(StepRequest request) {
			fRequest = request;
		}

		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					StepRequest request = manager.createStepRequest(
							getUnderlyingThread(),
							StepRequest.STEP_LINE,
							StepRequest.STEP_INTO);
					
					JiveDebugTarget target = (JiveDebugTarget) getDebugTarget();
					JDIRequestFilter requestFilter = target.getRequestFilter();
					requestFilter.filter(request);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
					setRequest(request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			StepEvent stepEvent = (StepEvent) event;
			fEventHandler.handleStep(stepEvent);
			
			JDTStepHandler stepHandler = getPendingStepHandler();
			if (stepHandler != null) {
				switch (stepHandler.getStepKind()) {
				case StepRequest.STEP_INTO:
					if (shouldHandleStepInto(stepEvent.location())) {
						return stepHandler.handleEvent(event, target);
					}
					break;
				case StepRequest.STEP_OVER:
					if (shouldHandleStepOver(stepEvent.location())) {
						return stepHandler.handleEvent(event, target);
					}
					break;
				case StepRequest.STEP_OUT:
					if (shouldHandleStepReturn(stepEvent.location())) {
						return stepHandler.handleEvent(event, target);
					}
					break;
				default:
					// TODO log error
					break;
				}
			}
			
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}

		
		protected boolean shouldHandleStepInto(Location location) {
			try {
				if (getOriginalStepStackDepth() != getUnderlyingFrameCount()) {
					return true;
				}
				
				if (getOriginalStepLocation().lineNumber() != location.lineNumber()) {
					return true;
				}
				
				return false;
			}
			catch (DebugException e) {
				// TODO log error
				return false;
			}
		}
		
		protected boolean shouldHandleStepOver(Location location) {
			try {
				if (getOriginalStepStackDepth() == getUnderlyingFrameCount() &&
						getOriginalStepLocation().lineNumber() != location.lineNumber()) {
					return true;
				}
				
				if (getOriginalStepStackDepth() > getUnderlyingFrameCount()) {
					return true;
				}
				
				return false;
			}
			catch (DebugException e) {
				// TODO log error
				return false;
			}
		}
		
		protected boolean shouldHandleStepReturn(Location location) {
			try {
				if (getOriginalStepStackDepth() > getUnderlyingFrameCount()) {
					return true;
				}
				
				return false;
			}
			catch (DebugException e) {
				// TODO log error
				return false;
			}
		}
	}
	
	protected boolean canStep() {
		return super.canStep() &&
			!getDebugTarget().getContourModel().canStepForwardThroughRecordedStates();
	}
	
	public boolean canResume() {
		return super.canResume() &&
			!getDebugTarget().getContourModel().canStepForwardThroughRecordedStates();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread#stepInto()
	 */
	@Override
	public synchronized void stepInto() throws DebugException {
		if (!canStepInto()) {
			return;
		}
		else {
			JDTStepHandler handler = new StepIntoHandler();
			handler.step();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread#stepOver()
	 */
	@Override
	public synchronized void stepOver() throws DebugException {
		if (!canStepOver()) {
			return;
		}
		else {
			JDTStepHandler handler = new StepOverHandler();
			handler.step();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread#stepReturn()
	 */
	@Override
	public synchronized void stepReturn() throws DebugException {
		if (!canStepReturn()) {
			return;
		}
		else {
			JDTStepHandler handler = new StepReturnHandler();
			handler.step();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread#stepToFrame(org.eclipse.debug.core.model.IStackFrame)
	 */
	@Override
	protected synchronized void stepToFrame(IStackFrame frame) throws DebugException {
		if (!canStepReturn()) {
			return;
		}
		else {
			JDTStepHandler handler = new StepToFrameHandler(frame);
			handler.step();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread#dropToFrame(org.eclipse.debug.core.model.IStackFrame)
	 */
	@Override
	protected void dropToFrame(IStackFrame frame) throws DebugException {
		// TODO Add message to exception and to error log
		throw new UnsupportedOperationException();
	}
	
	// TODO Add comments
	protected abstract class JDTStepHandler extends JDIThreadAdapter.StepHandlerAdapter {
		
		protected void step() throws DebugException {
			ISchedulingRule rule = getThreadRule();
			try {
				Job.getJobManager().beginRule(rule, null);
				ThreadReference thread = getUnderlyingThread();
				int stackDepth = thread.frameCount();
				
				if (stackDepth == 0) {
					return;
				}
				else {
					setOriginalStepKind(getStepKind());
					Location location = thread.frame(0).location();
					setOriginalStepLocation(location);
					setOriginalStepStackDepth(stackDepth);
					setPendingStepHandler(this);
					setRunning(true);
					preserveStackFrames();
					fireResumeEvent(getStepDetail());
					invokeThread();
				}
			} catch (IncompatibleThreadStateException e) {
				setPendingStepHandler(null);
				return;
			} finally {
				Job.getJobManager().endRule(rule);
			}
		}
		
		protected StepRequest createStepRequest() {
			// TODO Add message to exception and log error
			throw new UnsupportedOperationException();
		}
		
		protected abstract int getStepKind();
		
		protected abstract int getStepDetail();	
		
		protected StepRequest getStepRequest() {
			// TODO Add message to exception and log error
			throw new UnsupportedOperationException();
		}
		
		protected void setStepRequest(StepRequest request) {
			// TODO Add message to exception and log error
			throw new UnsupportedOperationException();
		}
		
		protected void deleteStepRequest() {
			// Do nothing
		}
		
		protected void attachFiltersToStepRequest(StepRequest request) {
			// TODO Filters are applied in the JiveStepHandler
		}
		
		protected void createSecondaryStepRequest() {
			// Do nothing
		}
		
		protected void abort() {
			setPendingStepHandler(null);
		}
	}
	
	/**
	 * Handler for step into requests.
	 */
	class StepIntoHandler extends JDTStepHandler {
		/**
		 * @see StepHandler#getStepKind()
		 */
		protected int getStepKind() {
			return StepRequest.STEP_INTO;
		}	
		
		/**
		 * @see StepHandler#getStepDetail()
		 */
		protected int getStepDetail() {
			return DebugEvent.STEP_INTO;
		}
	}
	
	/**
	 * Handler for step over requests.
	 */
	class StepOverHandler extends JDTStepHandler {
		/**
		 * @see StepHandler#getStepKind()
		 */
		protected int getStepKind() {
			return StepRequest.STEP_OVER;
		}	
		
		/**
		 * @see StepHandler#getStepDetail()
		 */
		protected int getStepDetail() {
			return DebugEvent.STEP_OVER;
		}		
	}
	
	/**
	 * Handler for step return requests.
	 */
	class StepReturnHandler extends JDTStepHandler {
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.model.JDIThread.StepHandler#locationShouldBeFiltered(com.sun.jdi.Location)
		 */
		protected boolean locationShouldBeFiltered(Location location) throws DebugException {
			// if still at the same depth, do another step return (see bug 38744)
			if (getOriginalStepStackDepth() == getUnderlyingFrameCount()) {
				return true;
			}
			return super.locationShouldBeFiltered(location);
		}

		/**
		 * @see StepHandler#getStepKind()
		 */
		protected int getStepKind() {
			return StepRequest.STEP_OUT;
		}	
		
		/**
		 * @see StepHandler#getStepDetail()
		 */
		protected int getStepDetail() {
			return DebugEvent.STEP_RETURN;
		}		
	}
	
	// TODO Add coments
	class StepToFrameHandler extends StepReturnHandler {
		
		private int fRemainingFrames;
		
		protected StepToFrameHandler(IStackFrame frame) throws DebugException {
			List frames = computeStackFrames();
			setRemainingFrames(frames.size() - frames.indexOf(frame));
		}
		
		protected void setRemainingFrames(int num) {
			fRemainingFrames = num;
		}
		
		protected int getRemainingFrames() {
			return fRemainingFrames;
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			try {
				int numFrames = getUnderlyingFrameCount();
				// tos should not be null
				if (numFrames <= getRemainingFrames()) {
					stepEnd();
					return false;
				}
				// reset running state and keep going
				setRunning(true);
				deleteStepRequest();
				createSecondaryStepRequest();
				return true;
			} catch (DebugException e) {
				logError(e);
				stepEnd();
				return false;
			}
		}				
	}
	
	protected class CloneMethodExitHandler implements IJDIEventListener {
		
		protected CloneMethodExitHandler() {
			createRequest();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					MethodExitRequest request = manager.createMethodExitRequest();
					request.addClassFilter("java.lang.Object");
					request.addThreadFilter(getUnderlyingThread());
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			MethodExitEvent exitEvent = (MethodExitEvent) event;
			if (exitEvent.method().name().equals("clone")) {
				JiveDebugTarget jiveTarget = (JiveDebugTarget) target;
				JDIRequestFilter filter = jiveTarget.getRequestFilter();
				ObjectReference object = (ObjectReference) exitEvent.returnValue();
				if (filter.acceptsClass(object.type().name())) {
					jiveTarget.getEventAdapter().handleMethodExit(exitEvent);
				}
			}
			
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
}
