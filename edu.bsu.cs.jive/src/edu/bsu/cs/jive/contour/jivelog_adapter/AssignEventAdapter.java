package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;

class AssignEventAdapter implements EventAdapter, AssignEvent.Exporter {

	// TODO Plug-in change
	private long eventNumber;
  private ContourID contourID;
  private Value newValue;
  private VariableID variableID;
  
  public void addContourID(ContourID contourID) {
    this.contourID = contourID;
  }

  public void addNewValue(Value v) {
    this.newValue = v;
  }

  public void addVariableID(VariableID variableID) {
    this.variableID = variableID;
  }

  public void apply(JavaInteractiveContourModel cm) {
  	// TODO Plug-in change
//  	// Note to myself: why isn't this calling "startTransactionRecording"?
//    // I guess it's because an assignment always happens as part of a
//    // larger transactions (e.g. between step events).
  	cm.startTransactionRecording();
  	
  	cm.getModelLock().lock();
    cm.setValue(contourID,variableID,newValue);
    cm.getModelLock().unlock();
    // TODO Plug-in change
    cm.endTransactionRecording(eventNumber);
    
    // cleanup
    contourID = null;
    newValue = null;
    variableID = null;
  }

  // TODO Plug-in change
  public void addNumber(long n) {
    eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {
    // ignored
  }

}
