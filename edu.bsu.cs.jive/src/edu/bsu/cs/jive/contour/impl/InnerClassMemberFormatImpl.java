package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMemberFormat;

/**
 * Default implementation of an inner class member format.
 * 
 * @author pvg
 */
public class InnerClassMemberFormatImpl extends AbstractContourMemberFormatImpl 
implements ContourMemberFormat.InnerClassFormat {
  
  public InnerClassMemberFormatImpl(ContourMemberFormat.InnerClassFormat.Importer i) {
    super(i);
  }

  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }

}
