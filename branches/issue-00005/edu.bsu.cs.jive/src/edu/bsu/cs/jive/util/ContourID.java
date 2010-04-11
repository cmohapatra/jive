package edu.bsu.cs.jive.util;

/**
 * Uniquely identifies a contour within a specific contour model.
 *
 * @author pvg
 */
public interface ContourID extends Comparable<ContourID> {
  
  /**
   * Export this contour id
   * @param exporter
   */
  public void export(Exporter exporter);
  
  /**
   * Importer interface for contour identifiers
   * @author pvg
   */
  public interface Importer {
    /**
     * Provide the string version of this contour identifier.
     * @return stringified identifier
     */
    public String provideIDString();
  }
  
  /**
   * Exporter interface for contour identifiers
   * @author pvg
   */
  public interface Exporter {
    /**
     * @see Importer#provideIDString()
     * @param id stringified ID
     */
    public void addIDString(String id);
  }

}
