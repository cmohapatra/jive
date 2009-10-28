package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Default implementation for a variable member in a contour.
 *
 * @author pvg
 */
public class VariableMemberImpl extends AbstractContourMemberImpl 
implements ContourMember.Variable {

  private final VariableID id;
  
	public void export(Variable.Exporter exporter) {
	  super.export(exporter);
    exporter.addID(id);
  }

  public VariableID id() {
    return id;
  }

  public VariableMemberImpl(ContourMember.Variable.Importer importer) {
		super(importer);
    this.id = importer.provideID();
    assert this.id!=null;
	}
	
	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}

}
