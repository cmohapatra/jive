package edu.bsu.cs.jive.contour.java;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * A method contour, representing a method's activation.
 * 
 * @author pvg
 */
public interface MethodContour extends JavaContour {

  /**
   * Export this method contour.
   * @param exporter
   */
  public void export(MethodContour.Exporter exporter);

  /**
   * A reverse-builder for method contours.
   * 
   * @author pvg
   */
  public interface Exporter extends JavaContour.Exporter {
  	
    /**
     * Export the thread on which this method contour has executed.
     * Every method must execute on exactly one Java thread.
     * @param thread
     */
    public void addThread(ThreadID thread);
  }
  
  /**
   * A builder for method contours.
   * 
   * @author pvg
   */
  public interface Importer extends JavaContour.Importer {
  	
    /**
     * Provide the thread on which this method has executed.
     * @return thread
     */
    public ThreadID provideThread();
  }
}
