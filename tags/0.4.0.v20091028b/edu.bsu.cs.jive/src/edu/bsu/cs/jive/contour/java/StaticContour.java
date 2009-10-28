package edu.bsu.cs.jive.contour.java;

/**
 * A static contour, representing a class' static context. 
 */
public interface StaticContour extends JavaContour {

  /**
   * Export this static contour.
   * @param exporter
   */
  public void export(StaticContour.Exporter exporter);
}
