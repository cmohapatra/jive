/**
 * 
 */
package org.eclipse.jdt.internal.debug.core.model;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.sun.jdi.VirtualMachine;

/**
 * @author jkczyz
 *
 */
public class JDIDebugTargetAdapter extends JDIDebugTarget {

	/**
	 * @param launch
	 * @param jvm
	 * @param name
	 * @param supportTerminate
	 * @param supportDisconnect
	 * @param process
	 * @param resume
	 */
	public JDIDebugTargetAdapter(ILaunch launch, VirtualMachine jvm, String name, boolean supportTerminate, boolean supportDisconnect, IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
	}

	protected class ThreadStartHandlerAdapter extends JDIDebugTarget.ThreadStartHandler {
		
		protected ThreadStartHandlerAdapter() {
			super();
		}
		
	}
	
	protected class ThreadDeathHandlerAdapter extends JDIDebugTarget.ThreadDeathHandler {
		
		protected ThreadDeathHandlerAdapter() {
			super();
		}
	}

}
