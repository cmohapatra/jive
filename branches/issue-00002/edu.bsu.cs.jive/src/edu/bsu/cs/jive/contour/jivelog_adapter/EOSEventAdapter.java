package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.util.ThreadID;

public class EOSEventAdapter implements EventAdapter, EOSEvent.Exporter {

  private String fname;
  private int line;
  // TODO Plug-in change
  private long eventNumber;
  
  public void addFilename(String filename) {
    this.fname = filename;
  }

  public void addLineNumber(int lineNumber) {
    this.line = lineNumber;
  }

  // TODO Plug-in change
  public void addNumber(long n) {
  	eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {}

  public void apply(JavaInteractiveContourModel cm) {
    //TODO: implement this... unless there really is no cm equivalent for
    // EOS events.
    //
    //cm.setFocus(fname,line);
  	
  	// TODO Plug-in change
  	cm.startTransactionRecording();
  	cm.endTransactionRecording(eventNumber);
    
    // Reset the fields of this adapter (clean up)
    fname = null;
    line = -1;
    // TODO Plug-in change
    eventNumber = -1;
  }

}
