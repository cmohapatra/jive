package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMemberFormat;

/**
 * Default implementation of a method definition member format.
 * 
 * @author pvg
 */
public class MethodMemberFormatImpl extends AbstractContourMemberFormatImpl
implements ContourMemberFormat.MethodFormat {

  public MethodMemberFormatImpl(ContourMemberFormat.MethodFormat.Importer i) {
    super(i);
  }
  
  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }

}
