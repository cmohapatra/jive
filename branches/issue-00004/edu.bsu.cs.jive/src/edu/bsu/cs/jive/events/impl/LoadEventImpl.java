package edu.bsu.cs.jive.events.impl;

import java.util.Collections;
import java.util.List;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.StringSeparator;

/**
 * Default implementation of a load event.
 * 
 * @author pvg
 */
class LoadEventImpl extends AbstractEventImpl 
implements LoadEvent {

  /** @see LoadEvent.Importer#provideContourCreationRecords() */
  private final List<ContourCreationRecord> creationRecords;
  
  /** @see LoadEvent.Importer#provideEnclosingContourID() */
  private final ContourID enclosingID;
  
  
  LoadEventImpl(LoadEvent.Importer importer) {
    super(importer);
    creationRecords = importer.provideContourCreationRecords();
    enclosingID = importer.provideEnclosingContourID();
  }
  
  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }

  public void export(edu.bsu.cs.jive.events.LoadEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addContourCreationRecords(Collections.unmodifiableList(creationRecords));
    exporter.addEnclosingContourID(enclosingID);
  }
  
  @Override
  public String toString() {
  	return this.getClass().getName() + "(" + paramString() 
  	+ ", enclosingID=" + enclosingID
  	+ ", creationRecords=" + StringSeparator.toString(creationRecords) 
  	+ ")";
  }

}
