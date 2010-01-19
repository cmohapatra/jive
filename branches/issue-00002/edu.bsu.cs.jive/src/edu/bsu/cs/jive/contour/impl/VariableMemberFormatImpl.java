package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Default implementation of a variable member format.
 * 
 * @author pvg
 */
public class VariableMemberFormatImpl extends AbstractContourMemberFormatImpl
    implements ContourMemberFormat.VariableFormat {

	private final VariableID id;
	
  public VariableMemberFormatImpl(ContourMemberFormat.VariableFormat.Importer importer) {
    super(importer);
    this.id = importer.provideID();
  }
  
  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }
  
  public void export(ContourMemberFormat.VariableFormat.Exporter e) {
  	super.export(e);
  	e.addID(id);
  }

}
