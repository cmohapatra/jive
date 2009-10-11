package edu.bsu.cs.jive.contour;

import java.util.List;

import edu.bsu.cs.jive.util.ContourID;

/**
 * A contour within a contour model.
 * <p>
 * This should not be confused with the contour table in the jivelog database.
 * That table contains the list of known contours in a stateless fashion.
 * This contour interface represents a stateful contour, a contour whose
 * variables have values and that is (probably) nested within some containing
 * contour.
 * <p>
 * The contour model implementation aims towards language independence.
 * Subinterfaces may supply language-specific semantics.
 * 
 * @author pvg
 */
public interface Contour {
	
  /**
   * Export this contour to the given reverse-builder.
   * @param exporter
   */
  public void export(Exporter exporter);
  
  /**
	 * Reverse-builder for contours.
	 * @author pvg
	 */
	public interface Exporter {
    /**
     * Export the contour identifier for this contour
     * @param id contour identifier
     */
		public void addID(ContourID id);
    
    /**
     * Export a contour member.
     * This method is called once for each member in the contour.
     * @param member
     */
		public void addMember(ContourMember member);
    
    /**
     * Called when the exportation is finished.
     */
    public void exportFinished();
	}
  
  /**
	 * Builder for contours.
	 * @author pvg
	 */
	public interface Importer {
    /**
     * Read the contour identifier 
     * @return contour identifier
     */
		public ContourID provideID();
    
    /**
     * Read the member list of this contour
     * @return list of members
     */
		public List<ContourMember> provideMembers();
    
    /**
     * Provide the contour model of which this contour is a part.
     * @return containing model
     */
    public ContourModel provideContainingModel();
	}
  
	/**
	 * Get the contour identifier for this contour.
	 * This identifier is unique with respect to this contour model.
	 * @return contour identifier.
	 */
	public ContourID id();
  
  /**
   * Get the model that contains this contour.
   * @return containing contour model
   */
  public ContourModel containingModel();
}

