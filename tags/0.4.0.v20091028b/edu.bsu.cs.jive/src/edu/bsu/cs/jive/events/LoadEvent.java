package edu.bsu.cs.jive.events;

import java.util.List;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.util.ContourID;

/**
 * An event corresponding to a class' loading.
 * <p>
 * A load event may be fired for one class' loading, or it can represent
 * the loading of a set of classes.
 * For example, the first class loaded usually has the <tt>main</tt> method
 * in it; this means that all superclasses of the class are also loaded
 * and hence get static contours.
 *
 * @author pvg
 */
public interface LoadEvent extends Event {

	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(Exporter exporter);
	
	/**
   * A builder for a load event.
   * @author pvg
   */
	public interface Importer extends Event.Importer {
    /**
     * Provide the list of contour creation records for this load event.
     * These records are provided in order of increasing generality;
     * i.e., the most specific subclass static contour format is
     * the first thing in the list.
     * @return list of contour creation records, where each record
     *  represents a static contour format.
     */
    public List<ContourCreationRecord> provideContourCreationRecords();
    
    
    /**
     * Provide the identifier of the contour into which the 
     * most generic contour of this load event should be nested.
     * If there is no such contour (i.e. the last contour creation
     * record is for java.lang.Object), then this provides null.
     * @return id of the contour into which the contour creation record
     *  stack should be nested
     * @see #provideContourCreationRecords()
     */
    public ContourID provideEnclosingContourID();
	}
	
	/**
   * A reverse-builder for a load event.
   * @author pvg
   */
	public interface Exporter extends Event.Exporter {
    /**
     * Supply the contour creation records list.
     * These are provided in order of increasing generality, so that
     * the last one in the list is the topmost (parentmost) contour
     * (which may be enclosed in {@link #addEnclosingContourID(ContourID)}.
     * @param ccrs
     * @see Importer#provideContourCreationRecords()
     */
    public void addContourCreationRecords(List<ContourCreationRecord> ccrs);
    
    /**
     * Supply the enclosing contour id.
     * @param enclosingID
     * @see Importer#provideEnclosingContourID()
     */
    public void addEnclosingContourID(ContourID enclosingID);
	}
}
