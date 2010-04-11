package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Value;

/**
 * Default implementatino of a method member in a contour.
 *
 * @author pvg
 */
public class MethodMemberImpl extends AbstractContourMemberImpl 
	implements ContourMember.MethodDeclaration {

	public MethodMemberImpl(ContourMember.MethodDeclaration.Importer importer) {
		super(importer);
	}
	
	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}
  
  @Override
  public void setValue(Value value) {
    throw new UnsupportedOperationException(
        "One should not change the value of a method declaration!");
  }

}
