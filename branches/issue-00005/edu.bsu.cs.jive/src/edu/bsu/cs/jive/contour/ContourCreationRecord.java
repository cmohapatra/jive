package edu.bsu.cs.jive.contour;

import edu.bsu.cs.jive.contour.impl.ContourFormatImpl;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ContourIDFactory;

/**
 * A record of a contour creation.
 * This class combines:
 * <ul>
 * <li>{@link edu.bsu.cs.jive.util.ContourID}
 * <li>{@link edu.bsu.cs.jive.contour.ContourFormat}
 * </ul>
 * With this information, contour creation can be logged (e.g. to a text
 * file) and recreated in a contour model.
 * 
 * @author pvg
 */
public class ContourCreationRecord {
  
  private ContourID contourID;
  private ContourFormat cf;
  
  /**
   * Create a contour creation record from an importer.
   * @param builder
   */
  public ContourCreationRecord(Importer builder) {
    contourID = ContourIDFactory.instance().create(builder);
    cf = ContourFormatImpl.create(builder);
  }
  
  /**
   * Export this record.
   * @param exporter
   */
  public void export(Exporter exporter) {
    exporter.addContourID(contourID);
    exporter.addContourFormat(cf);
  }
  
  /**
   * A builder for contour creation records.
   * 
   * @author pvg
   */
  public interface Importer extends ContourFormat.Importer, ContourID.Importer {
    // No custom processing required; control will delegate to cf or contourID
  }
  
  /**
   * A reverse-builder for contour creation records.
   * 
   * @author pvg
   */
  public interface Exporter {
    public void addContourFormat(ContourFormat cf);
    public void addContourID(ContourID id);
  }
  
  @Override
  public String toString() {
  	return this.getClass().getName() + "{contourID=" + contourID
  	 + ", cf=" + cf + "}";
  }
  
}
