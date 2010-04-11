package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;

class ExceptionEventAdapter implements EventAdapter, ExceptionEvent.Exporter {

	// TODO Plug-in change
	private long eventNumber;
  private Value exceptionObject;
  private VariableID catchingVar;
  private ContourID catchingContourID;
  private ThreadID threadID;
  
  public void apply(JavaInteractiveContourModel cm) {
    // Sanity check
    assert exceptionObject!=null;
    // assert catchingVar!=null; (Can be null if this is uncaught)
    assert threadID!=null;
    
    MethodContour top;
    
    // Start recording
    cm.startTransactionRecording();
    
    // If this is an uncaught exception, remove all methods
    if (catchingContourID==null) {
      top = cm.peek(threadID);
      while (top!=null) {
        cm.removeMethodContour(top.id());
        top = cm.peek(threadID);
      }
    }
    
    else {
      // Remove the appropriate methods from the stack.
      top = cm.peek(threadID);
      assert top != null;
      while (!top.id().equals(catchingContourID)) {
        cm.removeMethodContour(top.id());
        top = cm.peek(threadID);
        assert top != null;
      }
    }
    
    // Set the exception value in the contour model
    cm.setValue(catchingContourID, catchingVar, exceptionObject);
    
    // Stop recording
    // TODO Plug-in change
    cm.endTransactionRecording(eventNumber);
      
    // Cleanup references
    exceptionObject = null;
    catchingVar = null;
    threadID = null;
    catchingContourID = null;
  }

  public void addCatcher(ContourID catcher) {
    this.catchingContourID = catcher;
  }

  public void addException(Value exception) {
    this.exceptionObject = exception;
  }

  public void addVariable(VariableID v) {
    this.catchingVar = v;
  }

  // TODO Plug-in change
  public void addNumber(long n) {
  	eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {
    this.threadID = thread;
  }
}
