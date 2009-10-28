/**
 * 
 */
package org.eclipse.jdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;

/**
 * @author jkczyz
 *
 */
public /*abstract*/ class JDIThreadAdapter extends JDIThread {
	
//	private IJDIEventListener fStepHandlerProxy;

	/**
	 * @param target
	 * @param thread
	 * @throws ObjectCollectedException
	 */
	public JDIThreadAdapter(JDIDebugTarget target, ThreadReference thread) throws ObjectCollectedException {
		super(target, thread);
	}
	
	protected abstract class StepHandlerAdapter extends JDIThread.StepHandler {
		protected StepHandlerAdapter() {
		}
	}
	
	
//	protected class StepIntoHandlerAdapter extends JDIThread.StepIntoHandler {
//		protected StepIntoHandlerAdapter() {
//		}
//	}
//	
//	protected class StepOverHandlerAdapter extends JDIThread.StepOverHandler {
//		protected StepOverHandlerAdapter() {
//		}
//	}
//	
//	protected class StepReturnHandlerAdapter extends JDIThread.StepReturnHandler {
//		protected StepReturnHandlerAdapter() {
//		}
//	}
//	
//	protected class StepToFrameHandlerAdapter extends JDIThread.StepToFrameHandler {
//		protected StepToFrameHandlerAdapter(IStackFrame frame) throws DebugException {
//			super(frame);
//		}
//	}
//	
//	protected class DropToFrameHandlerAdapter extends JDIThread.DropToFrameHandler {
//		protected DropToFrameHandlerAdapter(IStackFrame frame) throws DebugException {
//			super(frame);
//		}
//	}

}
