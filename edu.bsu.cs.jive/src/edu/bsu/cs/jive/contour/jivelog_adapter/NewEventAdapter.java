package edu.bsu.cs.jive.contour.jivelog_adapter;

import java.util.List;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * Adapt new events into contour model transactions.
 * 
 * @author pvg
 */
class NewEventAdapter implements NewEvent.Exporter, EventAdapter {

	// TODO Plug-in change
	private long eventNumber;
  private List<ContourCreationRecord> ccrs;
  private ContourID parent;
  
  public void addContourCreationRecords(List<ContourCreationRecord> creationRecords) {
    this.ccrs = new java.util.ArrayList<ContourCreationRecord>(creationRecords);
  }

  public void addEnclosingContourID(ContourID enclosingID) {
    this.parent = enclosingID;
  }

  // TODO Plug-in change
  public void addNumber(long n) {
  	eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {
  }

  public void apply(JavaInteractiveContourModel cm) {
    ContourID parentID = this.parent;
    
    cm.startTransactionRecording();
    
    for (int i=ccrs.size()-1; i>=0; i--) {
      ContourCreationRecord ccr = ccrs.get(i);
      // A contour is virtual as long as its not the most specific one 
      // (i.e. i==0).
      Contour newContour = cm.addInstanceContour(ccr, parentID, i!=0);
      parentID = newContour.id();
    }
    
    // TODO Plug-in change
    cm.endTransactionRecording(eventNumber);
    
    // clean up references
    parent=null;
    ccrs=null;
  }

}
