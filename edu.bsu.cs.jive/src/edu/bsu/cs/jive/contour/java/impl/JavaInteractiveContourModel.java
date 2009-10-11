package edu.bsu.cs.jive.contour.java.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.impl.AbstractInteractiveContourModel;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Default implementation of an interactive contour model.
 * 
 * @author pvg
 */
public class JavaInteractiveContourModel extends AbstractInteractiveContourModel
    implements InteractiveContourModel {

	// TODO Plug-in change
  private final JavaContourFactory factory = new JavaContourFactory(this);

  /**
   * Keeps track of the methods on each thread.
   */
  private Map<ThreadID, Stack<MethodContour>> threadStacks =
    new HashMap<ThreadID,Stack<MethodContour>>();
  
  // TODO Plug-in change
  private ThreadID lastThread = null;
  
  // TODO Plug-in change
  public JavaInteractiveContourModel() {
  	super();
  	addListener(new ContourModel.Listener() {

			public void contourAdded(ContourModel model, Contour contour, Contour parent) {
				if (contour instanceof MethodContour) {
					setLastThread((MethodContour) contour);
					updateThreadStacks((MethodContour) contour, lastThread); 
				}
			}

			public void contourRemoved(ContourModel model, Contour contour,	Contour oldParent) {
				if (contour instanceof MethodContour) {
					setLastThread((MethodContour) contour);
					Stack<MethodContour> stack = threadStacks.get(lastThread);
					stack.pop();
				}
			}

			public void valueChanged(ContourModel model, Contour contour, VariableID variableID, Value newValue, Value oldValue) {
				// do nothing
			}
			
			private void setLastThread(MethodContour contour) {
				contour.export(new MethodContour.Exporter() {

					public void addThread(ThreadID thread) {
						lastThread = thread;
					}

					public void addID(ContourID id) {}

					public void addMember(ContourMember member) {}

					public void exportFinished() {}
					
				});
			}
  		
  	});
  }
  
  public ThreadID lastThread() {
  	return lastThread;
  }
  
  /**
   * Create an instance contour and add it to the model.
   * @param ccr
   * @param parentID
   * @param virtual
   * @return the newly created contour
   */
  public Contour addInstanceContour(ContourCreationRecord ccr, ContourID parentID,
      boolean virtual) {
    Contour contour = factory.createInstanceContour(ccr, virtual);
    if (parentID != null)
      add(contour, getContour(parentID));
    else
      add(contour, null);
    return contour;
  }
  
  /**
   * Create a static contour and add it to the contour model.
   * @param ccr
   * @param parentID
   * @return the newly created contour
   */
  public Contour addStaticContour(ContourCreationRecord ccr, ContourID parentID) {
    Contour contour = factory.createStaticContour(ccr);
    if (parentID!=null)
      add(contour, getContour(parentID));
    else
      add(contour,null);
    return contour;
  }
  
  /**
   * Create and add a method contour to the contour model.
   * @param ccr
   * @param parentID
   * @return the newly created contour
   */
  public MethodContour addMethodContour(MethodContourCreationRecord ccr, ContourID parentID) {
    MethodContour contour= factory.createMethodContour(ccr);
    if (parentID!=null)
      add(contour, getContour(parentID));
    else
      add(contour,null);
    
    // TODO Plug-in change
//    updateThreadStacks(contour, ccr.thread());
        
    return contour;
  }
  
  /**
   * Change the value of a variable.
   * @param contourID 
   * @param variableID 
   * @param newValue 
   */
  public void setValue(ContourID contourID, VariableID variableID, Value newValue) {
    // Pass this along to the protected superimplementation.
    super.setValue(getContour(contourID), variableID, newValue);
  }
  
  /**
   * Update the thread stacks for the addition of this contour.
   * @param contour
   * @param thread
   */
  private void updateThreadStacks(MethodContour contour, ThreadID thread) {
    Stack<MethodContour> stack = threadStacks.get(thread);
    if (stack==null)
      threadStacks.put(thread, stack=new Stack<MethodContour>());
    stack.push(contour);
  }
  
  /**
   * Remove a method contour from the contour model.
   * This is called when methods return.
   * @param methodID 
   */
  public void removeMethodContour(ContourID methodID) {
    // Call default implementation...
    remove(getContour(methodID));
    
    // TODO Plug-in change
//    // THen update the thread stacks.
//    // This has got to be the top of ONE of the stacks.
//    for (Stack<MethodContour> stack : threadStacks.values()) {
//    	// TODO Plug-in change
//      if (!stack.isEmpty() && stack.peek().id().equals(methodID)) {
//        stack.pop();
//        return; // short-circuit here
//      }
//    }
//    
//    // If we got this far, there was a problem.
//    throw new IllegalStateException("Contour not found in call stacks: "
//        + methodID);
  }
  
  /**
   * Peek at the top method contour on a specific thread.
   * @param thread
   * @return the method contour on top of that thread's stack
   * @see #removeMethodContour(ContourID)
   */
  public MethodContour peek(ThreadID thread) {
  	// TODO Plug-in change
  	if (threadStacks.containsKey(thread)) {
  		return threadStacks.get(thread).peek();
  	}
  	else {
  		throw new IllegalArgumentException("No such thread " + thread);
  	}
  }
  
  
}