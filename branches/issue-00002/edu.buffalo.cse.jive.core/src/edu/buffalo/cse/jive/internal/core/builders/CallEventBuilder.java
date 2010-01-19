package edu.buffalo.cse.jive.internal.core.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdi.TimeoutException;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.InternalException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.MethodEntryEvent;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.impl.UninitializedValue;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.util.SystemCaller;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.JiveCorePlugin;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class CallEventBuilder extends AbstractEventBuilder implements CallEvent.Importer {

	private Caller caller;
	
	private Target target;
	
	private List<Value> actuals;
	
	public static CallEventBuilder create(MethodEntryEvent event, StackFrame frame, ContourUtils contourManager, StackManager stackManager) {
		ThreadReference thread = event.thread();
		ThreadID threadID = stackManager.threadID(thread);
		Caller caller = determineCaller(thread, stackManager);
		Target target = determineTarget(frame, stackManager, true);
		List<Value> actuals = determineActualParams(frame, contourManager);
		
		return new CallEventBuilder(threadID, caller, target, actuals, contourManager);
	}
	
	public static CallEventBuilder create(ThreadReference thread, StackFrame frame, ContourUtils contourManager, StackManager stackManager) {
		ThreadID threadID = stackManager.threadID(thread);
		Caller caller = determineCaller(thread, stackManager);
		Target target = determineTarget(frame, stackManager, false);
		List<Value> actuals = determineActualParams(frame, contourManager);
		
		return new CallEventBuilder(threadID, caller, target, actuals, contourManager);
	}
	
	private static Caller determineCaller(ThreadReference thread, StackManager stackManager) {
		if (stackManager.frameCount(thread) == 0) {
			return SystemCaller.instance();
		}
		else {
			StackFrame frame = stackManager.peek(thread);
			return stackManager.createCaller(frame);
		}
	}
	
	private static Target determineTarget(StackFrame frame, StackManager stackManager, boolean isInModel) {
		ThreadReference thread = frame.thread();
		stackManager.push(thread, frame, isInModel);
		return stackManager.createTarget(frame);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Value> determineActualParams(StackFrame frame, ContourUtils contourManager) {
		List<Value> actuals = new ArrayList<Value>();
		try {
			List<LocalVariable> arguments = frame.location().method().arguments();
			ValueFactory valueFactory = ValueFactory.instance();

			for (LocalVariable arg : arguments) {
				try {
					Value value = valueFactory.createValue(frame.getValue(arg), contourManager);
					actuals.add(value);
				}
				// TODO Determine why this is being thrown (or if it still is)
				catch (InternalException e) {
					JiveCorePlugin.log(new Status(IStatus.WARNING, JiveCorePlugin.PLUGIN_ID, e.getMessage(), e));
				}
				catch (TimeoutException e) {
					JiveCorePlugin.log(new Status(IStatus.WARNING, JiveCorePlugin.PLUGIN_ID, "Timeout reading argument: " + arg));
					actuals.add(UninitializedValue.instance());
				}
			}
		}
		catch (AbsentInformationException e) {
			// Do nothing since debug information is not available.  This may occur
			// while monitoring code not compiled with the debug option (javac -g).
			//TODO Only log once per class
//			JiveCorePlugin.log(new Status(IStatus.INFO, JiveCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		catch (TimeoutException e) {
			JiveCorePlugin.log(new Status(IStatus.WARNING, JiveCorePlugin.PLUGIN_ID, "Timeout reading arguments."));
		}
		
		return Collections.unmodifiableList(actuals);
	}
	
	private CallEventBuilder(ThreadID thread, Caller caller, Target target, List<Value> actuals, ContourUtils contourManager) {
		super(thread, contourManager);
		this.caller = caller;
		this.target = target;
		this.actuals = actuals;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Importer#provideCaller()
	 */
	public Caller provideCaller() {
		return caller;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Importer#provideTarget()
	 */
	public Target provideTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CallEvent.Importer#provideActualParams()
	 */
	public List<Value> provideActualParams() {
		return actuals;
	}
}
