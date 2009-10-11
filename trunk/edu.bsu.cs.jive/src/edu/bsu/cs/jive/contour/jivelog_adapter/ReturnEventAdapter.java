package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.bsu.cs.jive.events.ReturnEvent.Returner.InModel;
import edu.bsu.cs.jive.events.ReturnEvent.Returner.OutOfModel;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * Adapt jivelog return events into contour model operations.
 * 
 * @author pvg
 */
class ReturnEventAdapter implements EventAdapter, ReturnEvent.Exporter {

	// TODO Plug-in change
	private long eventNumber;
	
  private ContourID contourToRemove;
  
  public void addPreviousContext(Returner returner) {
    returner.accept(returnerVisitor,null);
  }
  
  private Returner.Visitor returnerVisitor = new Returner.Visitor() {

    public Object visit(InModel returner, Object arg) {
      contourToRemove = returner.contour();
      return null;
    }

    public Object visit(OutOfModel returner, Object arg) {
    	// TODO Plug-in change
      assert contourToRemove==null;
      return null;
    }
    
  };

  public void addReturnValue(Value value) {}

  // TODO Plug-in change
  public void addNumber(long n) {
  	eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {}

  public void apply(JavaInteractiveContourModel cm) {
    if (contourToRemove!=null) {
      cm.startTransactionRecording();
      cm.removeMethodContour(contourToRemove);
      // TODO Plug-in change
      cm.endTransactionRecording(eventNumber);
    }
    
    // cleanup references
    contourToRemove=null;
  }
  
  

}
