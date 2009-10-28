package edu.buffalo.cse.jive.internal.ui;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStep;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.InstructionPointerManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;

public class SourceLookupFacility implements ILaunchListener, IStepListener {

	public SourceLookupFacility() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
		JiveUIPlugin.getDefault().getStepManager().addStepListener(this);
	}
	
	public void dispose() {
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		JiveUIPlugin.getDefault().getStepManager().removeStepListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
	 */
	public void launchAdded(ILaunch launch) {}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
	 */
	public void launchChanged(ILaunch launch) {}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	public void launchRemoved(ILaunch launch) {
		IDebugTarget target = launch.getDebugTarget();
		if (target instanceof IJiveDebugTarget) {
			removeAnnotations(target);
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#steppingInitiated(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingInitiated(IJiveDebugTarget target) {}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener#steppingCompleted(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void steppingCompleted(IJiveDebugTarget target) {
		long eventNumber = getCurrentEventNumber(target);
		if (eventNumber != -1) {
			EventOccurrence event = findEventOccurrence(target, eventNumber);
			if (event.underlyingEvent() instanceof EOSEvent) {
				removeAnnotations(target);
				IStackFrame stackFrame = createStackFrame(target, event);
				ISourceLookupResult result = DebugUITools.lookupSource(stackFrame, target.getLaunch().getSourceLocator());
				IWorkbench workbench = PlatformUI.getWorkbench();
				DebugUITools.displaySource(result, workbench.getActiveWorkbenchWindow().getActivePage());
			}
			else {
				// No corresponding EOSEvent available
				removeAnnotations(target);
			}
		}
		else {
			// Start of execution
			removeAnnotations(target);
		}
	}

	private long getCurrentEventNumber(IJiveDebugTarget target) {
		InteractiveContourModel model = target.getContourModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			if (model.canStepForwardThroughRecordedStates()) {
				if (model.canStepBackward()) {
					int transactionIndex = model.getPrevTransactionIndex();
					return model.getEventNumber(transactionIndex);
				}
			}
			
			return -1;
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private EventOccurrence findEventOccurrence(IJiveDebugTarget target, long eventNumber) {
		MultiThreadedSequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			EventOccurrence lastEvent = model.getEventOccurrence(eventNumber);
			if (lastEvent.underlyingEvent() instanceof EOSEvent) {
				return lastEvent;
			}
			else {
				Iterator<EventOccurrence> iter = model.iterator(lastEvent.underlyingEvent().thread());
				EventOccurrence result = lastEvent; // No EOSEvent found if not re-assigned
				while (iter.hasNext()) {
					EventOccurrence event = iter.next();
					
					if (event.underlyingEvent() instanceof EOSEvent) {
						result = event;
					}
					
					if (event == lastEvent) {
						if (lastEvent.underlyingEvent() instanceof CatchEvent) {
							if (iter.hasNext()) {
								result = iter.next();
							}
						}
						
						break;
					}
				}
				
				return result;
			}
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private IStackFrame createStackFrame(final IJiveDebugTarget target, EventOccurrence event) {
		try {
			IThread thread = new MockThread(target, event);
			return thread.getTopStackFrame();
		}
		catch (DebugException e) {
			throw new IllegalStateException("This should never occur since a mock object is returned.");
		}
	}
	
	private void removeAnnotations(IDebugTarget target) {
		InstructionPointerManager.getDefault().removeAnnotations(target);
	}
	
	// TODO Determine if we should cache previous MockStackFrame in order to
	// access the MockThread and be able to use this method
	private void removeAnnotations(IThread thread) {
		InstructionPointerManager.getDefault().removeAnnotations(thread);
	}
	
	private abstract class MockDebugElement implements IDebugElement, ISuspendResume, ITerminate, IStep {
		
		private IJiveDebugTarget target;
		
		public MockDebugElement(IJiveDebugTarget target) {
			this.target = target;
		}

		public IDebugTarget getDebugTarget() {
			return target;
		}

		public ILaunch getLaunch() {
			return target.getLaunch();
		}

		public String getModelIdentifier() {
			return target.getModelIdentifier();
		}

		public Object getAdapter(Class adapter) {
			throw new UnsupportedOperationException();
		}

		public boolean canResume() {
			throw new UnsupportedOperationException();
		}

		public boolean canSuspend() {
			throw new UnsupportedOperationException();
		}

		public boolean isSuspended() {
			throw new UnsupportedOperationException();
		}

		public void resume() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public void suspend() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean canTerminate() {
			throw new UnsupportedOperationException();
		}

		public boolean isTerminated() {
			return false;
		}

		public void terminate() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean canStepInto() {
			throw new UnsupportedOperationException();
		}

		public boolean canStepOver() {
			throw new UnsupportedOperationException();
		}

		public boolean canStepReturn() {
			throw new UnsupportedOperationException();
		}

		public boolean isStepping() {
			throw new UnsupportedOperationException();
		}

		public void stepInto() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public void stepOver() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public void stepReturn() throws DebugException {
			throw new UnsupportedOperationException();
		}
	}
	
	private class MockThread extends MockDebugElement implements IThread {
		
		private EventOccurrence event;

		private IStackFrame stackFrame;
		
		public MockThread(IJiveDebugTarget target, EventOccurrence event) {
			super(target);
			this.event = event;
			stackFrame = new MockStackFrame(target, event, this);
		}
		
		public boolean equals(Object o) {
			if (o instanceof MockThread) {
				MockThread other = (MockThread) o;
				return event.underlyingEvent().thread().equals(other.event.underlyingEvent().thread());
			}
			else {
				return false;
			}
		}
		
		public int hashCode() {
			return event.underlyingEvent().thread().hashCode();
		}

		public IBreakpoint[] getBreakpoints() {
			throw new UnsupportedOperationException();
		}

		public String getName() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public int getPriority() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public IStackFrame[] getStackFrames() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public IStackFrame getTopStackFrame() throws DebugException {
			return stackFrame;
		}

		public boolean hasStackFrames() throws DebugException {
			throw new UnsupportedOperationException();
		}
	}
	
	private class MockStackFrame extends MockDebugElement implements IStackFrame, IJavaStackFrame {
		
		private EventOccurrence event;
		
		private IThread thread;
		
		public MockStackFrame(IJiveDebugTarget target, EventOccurrence event, IThread thread) {
			super(target);
			this.event = event;
			this.thread = thread;
		}
		
		public boolean equals(Object o) {
			if (o instanceof MockStackFrame) {
				MockStackFrame other = (MockStackFrame) o;
				return event.containingExecution().equals(other.event.containingExecution());
			}
			else {
				return false;
			}
		}
		
		@Override
		public Object getAdapter(Class adapter) {
			if (adapter == IJavaStackFrame.class) {
				return this;
			}
			else {
				throw new UnsupportedOperationException();
			}
		}

		public int getCharEnd() throws DebugException {
			return -1;
		}

		public int getCharStart() throws DebugException {
			return -1;
		}

		public int getLineNumber() throws DebugException {
			EOSEvent underlyingEvent = (EOSEvent) event.underlyingEvent();
			return underlyingEvent.getLineNumber();
		}

		public String getName() throws DebugException {
			return event.containingExecution().toString();
		}

		public IRegisterGroup[] getRegisterGroups() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public IThread getThread() {
			return thread;
		}

		public IVariable[] getVariables() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean hasRegisterGroups() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean hasVariables() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean canForceReturn() {
			throw new UnsupportedOperationException();
		}

		public IJavaVariable findVariable(String variableName) throws DebugException {
			throw new UnsupportedOperationException();
		}

		public void forceReturn(IJavaValue value) throws DebugException {
			throw new UnsupportedOperationException();
		}

		public List getArgumentTypeNames() throws DebugException {
			throw new DebugException(new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Unknown argument type names"));
		}

		public IJavaClassType getDeclaringType() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public String getDeclaringTypeName() throws DebugException {
			ContourID context = event.containingExecution().context();
			String result = "";
			if (context != null) {
				int index = result.indexOf(':');
				if (index != -1) {
					result = result.substring(0, index);
				}
			}
			
			return result;
		}

		public int getLineNumber(String stratum) throws DebugException {
			throw new UnsupportedOperationException();
		}

		public IJavaVariable[] getLocalVariables() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public String getMethodName() throws DebugException {
			throw new DebugException(new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Unknown method name"));
		}

		public String getReceivingTypeName() throws DebugException {
			throw new DebugException(new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Unknown receiving type name"));
		}

		public IJavaReferenceType getReferenceType() throws DebugException {
			throw new DebugException(new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Unknown reference type"));
		}

		public String getSignature() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public String getSourceName() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public String getSourceName(String stratum) throws DebugException {
			throw new UnsupportedOperationException();
		}

		public String getSourcePath() throws DebugException {
			EOSEvent underlyingEvent = (EOSEvent) event.underlyingEvent();
			return underlyingEvent.getFilename();
		}

		public String getSourcePath(String stratum) throws DebugException {
			throw new UnsupportedOperationException();
		}

		public IJavaObject getThis() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isConstructor() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isNative() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isObsolete() throws DebugException {
			return false;
		}

		public boolean isOutOfSynch() throws DebugException {
			return false;
		}

		public boolean isStaticInitializer() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isSynchronized() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isVarArgs() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean supportsDropToFrame() {
			throw new UnsupportedOperationException();
		}

		public boolean wereLocalsAvailable() {
			return false;
		}

		public boolean isFinal() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isPackagePrivate() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isPrivate() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isProtected() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isPublic() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isStatic() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean isSynthetic() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean canStepWithFilters() {
			throw new UnsupportedOperationException();
		}

		public void stepWithFilters() throws DebugException {
			throw new UnsupportedOperationException();
		}

		public boolean canDropToFrame() {
			throw new UnsupportedOperationException();
		}

		public void dropToFrame() throws DebugException {
			throw new UnsupportedOperationException();
		}
	}
}
