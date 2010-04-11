package edu.bsu.cs.jive.events;

import java.util.List;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.util.ContourID;

/**
 * An event corresponding to the creation of an object.
 * <p>
 * When an object is created, contours are introduced for it and all of
 * its superclass objects.  
 * 
 * @author pvg
 */
public interface NewEvent extends Event {

	/**
	 * Get the object contours for this event.
	 * These are returned with the most specific contour first,
	 * so generality increases with the list index.
	 * @return list of object contours
	 */
  // Obtainable through exporter?
	//public List<ContourID> getContours();
	
	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(Exporter exporter);
	
	/**
	 * An importer (builder) for a new event.
	 * @author pvg
	 */
	public interface Importer extends Event.Importer {
    /**
     * Provide the list of contour creation records corresponding to this
     * new event.  The contours are specified in containment order,
     * from most specific to most general (i.e. from child to parent).
     * 
     * @return list of contour creation records for this event
     */
    public List<ContourCreationRecord> provideContourCreationRecords();
    
    /**
     * Provide the identifier of the contour into which this stack 
     * of contours should be nested.
     * If this returns null, then the created contours are top-level contours.
     * @return enclosing contour id
     */
    public ContourID provideEnclosingContourID();
	}
	
	/**
	 * An exporter (reverse-builder) for a new event.
	 * @author pvg
	 */
	public interface Exporter extends Event.Exporter {
    
    /**
     * Add the list of created contours.
     * These are specified in order from most specific (deepest nested)
     * to most general.
     * @param creationRecords created contour list
     * @see Importer#provideContourCreationRecords()
     */
    public void addContourCreationRecords(
        List<ContourCreationRecord> creationRecords);
    
    /**
     * Add the enclosing contour ID.
     * @see Importer#provideEnclosingContourID()
     * @param enclosingID
     */
    public void addEnclosingContourID(ContourID enclosingID);
	}
	
}
