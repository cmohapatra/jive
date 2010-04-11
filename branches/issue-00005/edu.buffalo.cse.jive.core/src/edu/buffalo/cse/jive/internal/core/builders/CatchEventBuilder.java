package edu.buffalo.cse.jive.internal.core.builders;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ExceptionEvent;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.runtime.builders.VariableIDFactory;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.core.EventAdapterUtilities;
import edu.buffalo.cse.jive.internal.core.JiveCorePlugin;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class CatchEventBuilder extends AbstractEventBuilder implements CatchEvent.Importer {

	private ContourID catcher;
	
	private VariableID variable;
	
	private Value exception;
	
	public static CatchEventBuilder create(ThreadReference thread, StackFrame validFrame, ContourUtils contourManager, StackManager stackManager, EventAdapterUtilities utils) {
		ExceptionEvent event = stackManager.outstandingException(thread);
		ThreadID threadID = stackManager.threadID(thread);
		ContourID catcher = determineCatcher(thread, stackManager);
		Value exception = determineException(event, contourManager);
		VariableID variable = determineVariable(event, validFrame, contourManager, utils);
		
		return new CatchEventBuilder(threadID, catcher, variable, exception, contourManager);
	}
	
	private static ContourID determineCatcher(ThreadReference thread, StackManager stackManager) {
		StackFrame frame = stackManager.peek(thread);
		return stackManager.createCatcher(frame);
	}
	
	private static Value determineException(ExceptionEvent event, ContourUtils contourManager) {
		ObjectReference exception = event.exception();
		return ValueFactory.instance().createValue(exception, contourManager);
	}
	
	@SuppressWarnings("unchecked")
	private static VariableID determineVariable(ExceptionEvent event, StackFrame frame, ContourUtils contourManager, EventAdapterUtilities utils) {
		ObjectReference exception = event.exception();
		Method method = frame.location().method();
		ReferenceType type = method.declaringType();
		
		if (method.isNative()) {
			String message = "Unable to determine catch variable:  catcher " + type.name() + "." + method.name() + " is native";
			JiveCorePlugin.log(new Status(IStatus.INFO, JiveCorePlugin.PLUGIN_ID, message));
			return null;
		}
		
		if (utils.isSourceAvailable(type)) {
			try {
				List<LocalVariable> localVariables = frame.visibleVariables();
				for (LocalVariable variable : localVariables) {
					if (exception.equals(frame.getValue(variable))) {
						return VariableIDFactory.instance().create(variable, method);
					}
				}
			}
			catch (AbsentInformationException e) {
				JiveCorePlugin.log(new Status(IStatus.ERROR, JiveCorePlugin.PLUGIN_ID, e.getMessage(), e));
				return null;
			}
		}
		else {
			String message = "Unable to determine catch variable:  source unavailable for type " + type.name();
			JiveCorePlugin.log(new Status(IStatus.INFO, JiveCorePlugin.PLUGIN_ID, message));
			return null;
		}
		
		String message = "Unable to determine catch variable:  no matching visible variable";
		JiveCorePlugin.log(new Status(IStatus.WARNING, JiveCorePlugin.PLUGIN_ID, message));
		return null;
	}
	
	private CatchEventBuilder(ThreadID thread, ContourID catcher, VariableID variable, Value exception, ContourUtils contourManager) {
		super(thread, contourManager);
		this.catcher = catcher;
		this.variable = variable;
		this.exception = exception;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Importer#provideCatcher()
	 */
	public ContourID provideCatcher() {
		return catcher;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent.Importer#provideException()
	 */
	public Value provideException() {
		return exception;
	}

	public VariableID provideVariable() {
		return variable;
	}
}
