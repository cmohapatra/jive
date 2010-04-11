package edu.buffalo.cse.jive.internal.core.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTargetAdapter;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.contour.jivelog_adapter.JiveLogToContourModelAdapter;
import edu.bsu.cs.jive.runtime.DefaultRequestFilter;
import edu.bsu.cs.jive.runtime.JDIEventHandler;
import edu.bsu.cs.jive.runtime.JDIRequestFilter;
import edu.buffalo.cse.jive.core.IJiveCoreConstants;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.core.IJiveEventLog;
import edu.buffalo.cse.jive.internal.core.JDIToJiveEventAdapter;
import edu.buffalo.cse.jive.internal.core.JiveCorePlugin;
import edu.buffalo.cse.jive.internal.core.JiveEventLog;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.java.impl.JavaSequenceModel;
import edu.buffalo.cse.jive.sequence.jivelog_adapter.JiveLogToSequenceModelAdapter;

public class JiveDebugTarget extends JDIDebugTargetAdapter implements IJiveDebugTarget {
	
	private JDIToJiveEventAdapter fEventAdapter;
	
	private JiveEventLog fEventLog;
	
	private JavaInteractiveContourModel fContourModel;
	
	private JiveLogToContourModelAdapter fContourModelAdapter;
	
	private JavaSequenceModel fSequenceModel;
	
	private JiveLogToSequenceModelAdapter fSequenceModelAdapter;
	
	private JDIRequestFilter fRequestFilter;
	
	private ModificationWatchpointHandler fWatchpointHandler;
	

	public JiveDebugTarget(ILaunch launch, VirtualMachine jvm, String name, boolean supportTerminate, boolean supportDisconnect, IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
//		return JiveCorePlugin.PLUGIN_ID;
		return super.getModelIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#initialize()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected synchronized void initialize() {
		fRequestFilter = new DefaultRequestFilter() {
			{
				try {
					ILaunchConfigurationWorkingCopy config = getLaunch().getLaunchConfiguration().getWorkingCopy();
					List<String> filters = config.getAttribute(
							IJiveCoreConstants.EXCLUSION_FILTERS_KEY, (List<String>) null);
					
					// Save the old hard-coded filters to the config for backward compatibility
					if (filters == null) {
						List<String> oldHardCodedFilters = Arrays.asList(
								"java.*",
								"javax.*",
								"com.sun.*",
								"sun.*"
						);
						filters = oldHardCodedFilters;
						config.setAttribute(IJiveCoreConstants.EXCLUSION_FILTERS_KEY, filters);
						config.doSave();
					}
					
					Iterator<String> iter = filters.iterator();
					while (iter.hasNext()) {
						addExclusionFilter(iter.next());
					}
				}
				catch(CoreException e) {
					JiveCorePlugin.log(e);
				}
			}
		};
		
		fEventAdapter = new JDIToJiveEventAdapter(fRequestFilter);
		fEventLog = new JiveEventLog(fEventAdapter);
		fEventAdapter.addListener(fEventLog);
		
		fContourModel = new JavaInteractiveContourModel();
		fContourModelAdapter = new JiveLogToContourModelAdapter(fContourModel);
		fEventAdapter.addListener(fContourModelAdapter);
		
		fSequenceModel = new JavaSequenceModel();
		fSequenceModelAdapter = new JiveLogToSequenceModelAdapter(fSequenceModel);
		fEventAdapter.addListener(fSequenceModelAdapter);
		
		super.initialize();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#initializeRequests()
	 */
	@Override
	protected void initializeRequests() {
		setThreadStartHandler(new ThreadStartHandler());
		new ThreadDeathHandler();
		new ClassPrepareHandler();
		fWatchpointHandler = new ModificationWatchpointHandler();
		new MethodEntryHandler();
		new MethodExitHandler();
		new ExceptionHandler();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	@Override
	public void launchRemoved(ILaunch launch) {
		if (fEventAdapter != null) {
			fEventAdapter.removeListener(fEventLog);
			fEventAdapter.removeListener(fContourModelAdapter);
			fEventAdapter.removeListener(fSequenceModelAdapter);
		}
		
		super.launchRemoved(launch);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#newThread(com.sun.jdi.ThreadReference)
	 */
	//@Override
	protected JDIThread newThread(ThreadReference reference) {
		try {
			return new JiveThread(this, reference);
		} catch (ObjectCollectedException exception) {
			// ObjectCollectionException can be thrown if the thread has already
			// completed (exited) in the VM.
		}
		return null;
	}

	/**
	 * @return the executionMonitor
	 */
	protected JDIEventHandler getEventAdapter() {
		return fEventAdapter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#handleVMStart(com.sun.jdi.event.VMStartEvent)
	 */
	@Override
	public void handleVMStart(VMStartEvent event) {
		fEventAdapter.handleVMStart(event);
		super.handleVMStart(event);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#handleVMDeath(com.sun.jdi.event.VMDeathEvent)
	 */
	@Override
	public void handleVMDeath(VMDeathEvent event) {
		fEventAdapter.handleVMDeath(event);
		super.handleVMDeath(event);
		// TODO Consider overriding to save target state somehow (if needed)
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#handleVMDisconnect(com.sun.jdi.event.VMDisconnectEvent)
	 */
	@Override
	public void handleVMDisconnect(VMDisconnectEvent event) {
		fEventAdapter.handleVMDisconnect(event);
		super.handleVMDisconnect(event);
		// TODO Consider overriding to save target state somehow (if needed)
	}

	protected class ThreadStartHandler extends JDIDebugTargetAdapter.ThreadStartHandlerAdapter {
		
		protected ThreadStartHandler() {
			super();
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			// TODO Determine if this is needed; if so, move to newThread()
//			ThreadReference thread = ((ThreadStartEvent) event).thread();
//			ThreadGroupReference group = thread.threadGroup();
//			
//			if (group != null && !group.name().equals("system")) {
//				JiveThread jiveThread = (JiveThread) findThread(thread);
//				jiveThread.configureNextStepRequest();
//			}
			
			fEventAdapter.handleThreadStart((ThreadStartEvent) event);
			return super.handleEvent(event, target);
		}
	}
	
	protected class ThreadDeathHandler extends JDIDebugTargetAdapter.ThreadDeathHandlerAdapter {
		
		protected ThreadDeathHandler() {
			super();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					EventRequest request = manager.createThreadDeathRequest();
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}					
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			fEventAdapter.handleThreadDeath((ThreadDeathEvent) event);
			return super.handleEvent(event, target);
		}
	}

	protected class ClassPrepareHandler implements IJDIEventListener {
		
		protected ClassPrepareHandler() {
			createRequest();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					ClassPrepareRequest request = manager.createClassPrepareRequest();
					fRequestFilter.filter(request);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			EventRequestManager manager = target.getEventRequestManager();
			if (manager != null) {
				try {
					for (Object o : ((ClassPrepareEvent) event).referenceType().fields()) {
						Field f = (Field) o;
						
						// Ignore compiler generated fields
					      if (!f.isSynthetic() && f.name().indexOf("$") == -1) {
					    	  ModificationWatchpointRequest request = manager.createModificationWatchpointRequest(f);
					    	  request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					    	  request.enable();
					    	  target.addJDIEventListener(fWatchpointHandler, request);
					      }
					}
				} catch (RuntimeException e) {
					logError(e);
				}
			}
			
			fEventAdapter.handleClassPrepare((ClassPrepareEvent) event);
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
	
	protected class ModificationWatchpointHandler implements IJDIEventListener {
		
		protected ModificationWatchpointHandler() {}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			fEventAdapter.handleWatchpoint((ModificationWatchpointEvent) event);
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
	
	protected class MethodEntryHandler implements IJDIEventListener {
		
		protected MethodEntryHandler() {
			createRequest();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					MethodEntryRequest request = manager.createMethodEntryRequest();
					fRequestFilter.filter(request);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			fEventAdapter.handleMethodEntry((MethodEntryEvent) event);
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
	
	protected class MethodExitHandler implements IJDIEventListener {
		
		protected MethodExitHandler() {
			createRequest();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					MethodExitRequest request = manager.createMethodExitRequest();
					fRequestFilter.filter(request);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			fEventAdapter.handleMethodExit((MethodExitEvent) event);
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
	
	protected class ExceptionHandler implements IJDIEventListener {
		
		protected ExceptionHandler() {
			createRequest();
		}
		
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					// Don't filter ExceptionEvents as they are needed to adjust the call stack
					ExceptionRequest request = manager.createExceptionRequest(null, true, true);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.enable();
					addJDIEventListener(this, request);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			fEventAdapter.handleExceptionThrown((ExceptionEvent) event);
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.core.IJiveDebugTarget#canReplayRecordedStates()
	 */
	public boolean canReplayRecordedStates() {
		return !isAvailable() || isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		// Allow suspending the VM if at least one thread is not suspended
		if (!isSuspended() && isAvailable()) {
			for (IThread thread : getThreads()) {
				if (!thread.isSuspended()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#canResume()
	 */
	@Override
	public boolean canResume() {
		// Allow resuming when at the end of the recording
		return super.canResume() && !getContourModel().canStepForwardThroughRecordedStates();
	}
	
	public void resume() throws DebugException {
		// Run to the end of the recording before resuming
		// NOTE:  This is necessary because the target's resume button is not yet disabled
		//        when in a past state (unless the target is unselected then reselected) 
		InteractiveContourModel model = getContourModel();
		while (model.canStepForwardThroughRecordedStates()) {
			model.stepForward();
		}
		
		super.resume();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget#canPopFrames()
	 */
	@Override
	public boolean canPopFrames() {
		return false;
	}

	/**
	 * Returns the default request filter for the launch.
	 * 
	 * @return the JDI request filters
	 */
	public JDIRequestFilter getRequestFilter() {
		return fRequestFilter;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.core.IJiveDebugTarget#getJiveEventLog()
	 */
	public IJiveEventLog getJiveEventLog() {
		return fEventLog;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.core.IJiveDebugTarget#getContourModel()
	 */
	public InteractiveContourModel getContourModel() {
		return fContourModel;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.core.IJiveDebugTarget#getSequenceModel()
	 */
	public MultiThreadedSequenceModel getSequenceModel() {
		return fSequenceModel;
	}
}
