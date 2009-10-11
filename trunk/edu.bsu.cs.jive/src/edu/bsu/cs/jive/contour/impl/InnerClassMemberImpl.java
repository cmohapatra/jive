package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Value;

/**
 * Default implementation for an inner class member.
 *
 * @author pvg
 */
public final class InnerClassMemberImpl extends AbstractContourMemberImpl 
implements ContourMember.InnerClass {

	public InnerClassMemberImpl(ContourMember.InnerClass.Importer importer) {
		super(importer);
	}
	
	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}
  
  @Override
  public void setValue(Value value) {
    throw new UnsupportedOperationException(
        "One should not change the value of an inner class definition!");
  }
	
}