package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

class AssignEventImpl extends AbstractEventImpl implements AssignEvent {

  private final Value value;
  private final ContourID contour;
  private final VariableID var;
  
  public AssignEventImpl(AssignEvent.Importer importer) {
    super(importer);
    value=importer.provideNewValue();
    contour=importer.provideContourID();
    var=importer.provideVariableID();
  }
  
  public void export(AssignEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addContourID(contour);
    exporter.addNewValue(value);
    exporter.addVariableID(var);
  }

  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }
  
  @Override
  public String toString() {
  	// TODO Plug-in change
    return this.getClass().getName() + "(" + paramString() 
  	+ ", contourID=" + contour
    + ", variableID=" + var
    + ", new_value=" + value 
    + ")";
  }

}
