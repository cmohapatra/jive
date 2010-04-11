package edu.bsu.cs.jive.events.impl;

import java.util.Collections;
import java.util.List;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.StringSeparator;

/**
 * Default implementation of an object creation ("new") event.
 * 
 * @author pvg
 */
class NewEventImpl extends AbstractEventImpl implements NewEvent {

  /** @see NewEvent.Importer#provideContourCreationRecords() */
  private List<ContourCreationRecord> creationRecords;
  
  /** @see edu.bsu.cs.jive.events.NewEvent.Importer#provideEnclosingContourID() */
  private ContourID enclosingID;
  
  public NewEventImpl(NewEvent.Importer importer) {
    super(importer);
    creationRecords = importer.provideContourCreationRecords();
    enclosingID = importer.provideEnclosingContourID();
  }
  
  public void export(NewEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addContourCreationRecords(Collections.unmodifiableList(creationRecords));
    exporter.addEnclosingContourID(enclosingID);
  }

  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }
  
  @Override
  public String toString() {
  	// TODO Plug-in change
  	return String.format("%s(%s,enclosingID=%s,creationRecords=[%s]",
  			this.getClass().getName(),
  			paramString(),
  			enclosingID,
  			StringSeparator.toString(creationRecords));
  }

}
