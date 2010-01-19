package edu.bsu.cs.jive.contour.java;

/**
 * An instance contour.
 * 
 * @author pvg
 */
public interface InstanceContour extends JavaContour {

  /**
   * Test if this is a virtual contour or not.
   * @return true if and only if this is a virtual instance contour
   */
  public boolean isVirtual();
  
  /**
   * A builder for instance contours.
   * 
   * @author pvg
   */
  public interface Importer extends JavaContour.Importer {
    public boolean provideVirtualProperty();
  }
  
  /**
   * A reverse-builder for instance contours.
   * 
   * @author pvg
   */
  public interface Exporter extends JavaContour.Exporter {
    public void addVirtualProperty(boolean virtual);
  }
  
  /**
   * Export this contour to the given reverse-builder.
   * @param exporter
   */
  public void export(Exporter exporter);
}
