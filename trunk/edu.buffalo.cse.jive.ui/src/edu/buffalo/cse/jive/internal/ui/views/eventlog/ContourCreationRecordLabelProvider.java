package edu.buffalo.cse.jive.internal.ui.views.eventlog;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourFormat;
import edu.bsu.cs.jive.util.ContourID;

/**
 * An event exporter used to provide a label for a
 * <code>ContourCreationRecord</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class ContourCreationRecordLabelProvider implements ContourCreationRecord.Exporter {
	
	/**
	 * The string representation of the contour ID.
	 */
	private String fContourID;

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourFormat(edu.bsu.cs.jive.contour.ContourFormat)
	 */
	public void addContourFormat(ContourFormat cf) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addContourID(ContourID id) {
		fContourID = id.toString();
	}
	
	/**
	 * Returns the string representation of the contour ID of the contour
	 * creation record.
	 * 
	 * @return the contour ID
	 */
	public String getContourID() {
		return fContourID;
	}
}
