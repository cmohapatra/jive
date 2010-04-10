package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Value;


/**
 * Marks a contour member implementation that supports the changing of
 * its value.
 * This interface should only be used by the underlying implementation engine,
 * not at a higher level (e.g. GUI); hence its inclusion in this package.
 * 
 * @author pvg
 */
public interface MutableContourMember extends ContourMember {

  /**
   * Change the value of this contour member.
   * @param value the new value
   */
  public void setValue(Value value);
}
