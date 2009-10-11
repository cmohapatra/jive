package edu.bsu.cs.jive.contour.java;

import edu.bsu.cs.jive.contour.Contour;

/**
 * Superinterface for all Java-specific contour interfaces.
 * <p>
 * The possible types of contours, and their type designation, are:
 * <ul>
 * <li>virtual instance (instance)
 * <li>concrete instance (instance)
 * <li>static (static)
 * <li>static method (method)
 * <li>instance method (method)
 * <li>static inner virtual instance (instance)
 * <li>static inner concrete instance (instance)
 * <li>static inner (static)
 * <li>inner virtual instance (instance)
 * <li>inner concrete instance (instance)
 * </ul>
 * Whether a contour is "inner" or not is revealed by its placement in 
 * the contour model.
 */
public interface JavaContour extends Contour {

  /**
   * Visitor interface for java contour types.
   * 
   * @author pvg
   */
  public interface Visitor {
    public Object visit(InstanceContour contour, Object arg);
    public Object visit(StaticContour contour, Object arg);
    public Object visit(MethodContour contour, Object arg);
  }
  
  /**
   * Accept a visitor
   * @param v the visitor
   * @param arg optional visitation argument
   * @return result
   */
  public Object accept(Visitor v, Object arg);
  
}
