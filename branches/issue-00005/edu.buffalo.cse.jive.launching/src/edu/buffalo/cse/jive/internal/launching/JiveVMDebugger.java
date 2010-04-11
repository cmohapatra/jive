package edu.buffalo.cse.jive.internal.launching;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;

import edu.buffalo.cse.jive.core.JiveDebugModel;

public class JiveVMDebugger extends StandardVMDebugger {

	public JiveVMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}
	
	protected IDebugTarget createDebugTarget(VMRunnerConfiguration config, ILaunch launch, int port, IProcess process, VirtualMachine vm) {
		return JiveDebugModel.newDebugTarget(launch, vm, renderDebugTarget(config.getClassToLaunch(), port), process, true, false, config.isResumeOnStartup());
	}
}
