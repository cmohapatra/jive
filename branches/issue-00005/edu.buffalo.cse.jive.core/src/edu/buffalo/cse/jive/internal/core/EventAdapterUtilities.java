package edu.buffalo.cse.jive.internal.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.ContourUtilsImpl;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

public class EventAdapterUtilities {
	
	private final ContourUtils contourManager = new ContourUtilsImpl() {
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.runtime.ContourUtilsImpl#threadID(com.sun.jdi.ThreadReference)
		 */
		public ThreadID threadID(ThreadReference ref) {
			throw new UnsupportedOperationException("This operation is no longer supported.");
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.runtime.ContourUtilsImpl#peekStack(edu.bsu.cs.jive.util.ThreadID)
		 */
		public ContourID peekStack(ThreadID threadID) {
			throw new UnsupportedOperationException("This operation is no longer supported.");
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.runtime.ContourUtilsImpl#popStack(edu.bsu.cs.jive.util.ThreadID)
		 */
		public ContourID popStack(ThreadID threadID) {
			throw new UnsupportedOperationException("This operation is no longer supported.");
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.runtime.ContourUtilsImpl#pushStack(edu.bsu.cs.jive.util.ContourID, edu.bsu.cs.jive.util.ThreadID)
		 */
		public void pushStack(ContourID methodContourID, ThreadID threadID) {
			throw new UnsupportedOperationException("This operation is no longer supported.");
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.runtime.ContourUtilsImpl#stackSize(edu.bsu.cs.jive.util.ThreadID)
		 */
		public int stackSize(ThreadID threadID) {
			throw new UnsupportedOperationException("This operation is no longer supported.");
		}
	};
	
	private final StackManager stackManager = new StackManager(contourManager);
	
	private final Set<Long> absentInformationSet = new HashSet<Long>();
	
	private final Set<Long> checkedTypeSet = new HashSet<Long>();
	
	public void checkSourceInformation(ReferenceType type) {
		try {
			long id = type.classObject().uniqueID();
			if (!checkedTypeSet.contains(id)) {
				checkedTypeSet.add(id);
				String sourceName = type.sourceName();
				String message = "Source available for type " + type.name() + " (" + sourceName + ")";
				JiveCorePlugin.log(new Status(IStatus.OK, JiveCorePlugin.PLUGIN_ID, message));
			}
		}
		catch (AbsentInformationException e) {
			absentInformationSet.add(type.classObject().uniqueID());
			String message = "Source unavailable for type " + type.name();
			JiveCorePlugin.log(new Status(IStatus.INFO, JiveCorePlugin.PLUGIN_ID, message));
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<LocalVariable> availableArguments(Method method) {
		ReferenceType type = method.declaringType();
		if (isSourceAvailable(type)) {
			try {
				List<LocalVariable> arguments = method.arguments();
				return arguments;
			}
			catch (AbsentInformationException e) {
				// In case of native or abstract methods
				return Collections.emptyList();
			}
		}
		else {
			return Collections.emptyList();
		}
	}
	
	public boolean isSourceAvailable(ReferenceType type) {
		return !absentInformationSet.contains(type.classObject().uniqueID());
	}
	
	public ContourUtils getContourManager() {
		return contourManager;
	}
	
	public StackManager getStackManager() {
		return stackManager;
	}
}
