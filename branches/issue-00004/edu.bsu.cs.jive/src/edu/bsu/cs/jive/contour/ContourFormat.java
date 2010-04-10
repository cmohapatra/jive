package edu.bsu.cs.jive.contour;

import java.util.List;

/**
 * A contour format describes the structure of a contour
 * without specifying any non-constant values within it.
 * 
 * @author pvg
 */
public interface ContourFormat {

	/**
	 * Get the member formats involved in this contour format.
	 * @return list of contour member formats
	 */
	//public List<ContourMemberFormat> memberFormats();
  // See if we can do the above with only importers/exporters
  
	/**
	 * Export this contour format.
	 * @param exporter
	 */
	public void export(Exporter exporter);
	
	/**
	 * Builder for contour formats.
	 * @author pvg
	 */
  public interface Importer {
    /**
     * Import the list of member format for this cf.
     * @return list of member formats
     */
  	public List<ContourMemberFormat> provideMemberFormats();
  }
  
  /**
   * Reverse-builder for contour formats
   * @author pvg
   */
  public interface Exporter {
    /**
     * Export a member format.
     * @param mf a member format
     */
  	public void provideMemberFormat(ContourMemberFormat mf);
    /**
     * Called when the exportation is finished.
     */
    public void exportFinished();
  }
  
}
