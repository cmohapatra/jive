package edu.bsu.cs.jive.runtime;

import com.sun.jdi.StackFrame;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

// TODO Plug-in change
/**
 * An EventHandler recieves notification about interesting
 * things happining in the target VirtualMachine.
 * 
 * @author Eric Crahen <crahen@cse.buffalo.edu>
 * @author Paul Gestwicki <pvg@cse.buffalo.edu>
 */
public interface JDIEventHandler {
	
	// TODO Plug-in change
	/**
	 * Called when a class prepare event is returned by JPDA.
	 * @param e
	 */
	public void handleClassPrepare(ClassPrepareEvent e);
	
	// TODO Plug-in change	
	/**
	 * Called when a thread start event is returned by JPDA.
	 * @param e
	 */
	public void handleThreadStart(ThreadStartEvent e);
	
	// TODO Plug-in change
	/**
	 * Called when a thread death event is returned by JPDA.
	 * @param e
	 */
	public void handleThreadDeath(ThreadDeathEvent e);	

  /**
   * Called when a method entry event is returned by JPDA.
   * @param e
   */
  public void handleMethodEntry(MethodEntryEvent e);

  /**
   * Called when a method exit event is returned by JPDA.
   * @param e
   */
  public void handleMethodExit(MethodExitEvent e);

  /**
   * Called when a modification watchpoint event is returned by JPDA.
   * Modification watchpoints are used to monitor changes in object attributes.
   * @param e
   */
  public void handleWatchpoint(ModificationWatchpointEvent e);

  /**
   * Called when an exception is thrown.
   * @param e the exception event
   */
  public void handleExceptionThrown(ExceptionEvent e);

  /**
   * Called when the locus of execution changes. This may be a change in line
   * number or a change of thread.
   * @param frame the stack frame
   * @param frameIndex index of the frame
   * @see #handleStep(StepEvent)
   */
  public void handleLocation(StackFrame frame, int frameIndex);

  /**
   * Called when a step is completed. The resolution of a step is set elsewhere,
   * though a step will usually be the smallest possible unit the VM can
   * execute. There can be multiple steps on one line.
   * @param e the step event
   */
  public void handleStep(StepEvent e);
  

  /**
   * Called when the target VM dies.
   * @param e the vm death event
   */
  public void handleVMDeath(VMDeathEvent e);
  
  
  // TODO Plug-in change
  /**
   * Called when the target VM is disconnected.
   * @param e the vm disconnect event
   */
  public void handleVMDisconnect(VMDisconnectEvent e);
  
  /**
   * Called when the target VM starts.
   * @param e the vm start event
   */
  public void handleVMStart(VMStartEvent e);
}
