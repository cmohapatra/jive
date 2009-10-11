package edu.bsu.cs.jive.contour.jivelog_adapter;

import java.util.List;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * Adapt load events into contour model transactions.
 * 
 * @author pvg
 */
class LoadEventAdapter implements LoadEvent.Exporter, EventAdapter {

	// TODO Plug-in change
	private long eventNumber;
	
  private List<ContourCreationRecord> ccrs;

  private ContourID parent;

  public void addContourCreationRecords(List<ContourCreationRecord> ccrs) {
    this.ccrs = ccrs;
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
    cm.startTransactionRecording();
    ContourID parentID = this.parent;

    // We must traverse the list from back to front, from most general
    // to most specific.
    for (int i = ccrs.size() - 1; i >= 0; i--) {
      ContourCreationRecord ccr = ccrs.get(i);
      Contour c = cm.addStaticContour(ccr, parentID);
      parentID = c.id();
    }

    // TODO Plug-in change
    cm.endTransactionRecording(eventNumber);
    
    // clean up references
    this.parent=null;
    this.ccrs = null;
  }

}
