package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * An event corresponding to the assigning of a value to a variable.
 * <p>
 * In earlier versions, this was called a "SET" event.
 *
 * @author pvg
 */
public interface AssignEvent extends Event {

	/**
	 * Get the new value assigned to a variable.
	 * @return the variable's new value
	 */
	//public Value getNewValue();
	
	// TODO Plug-in change:  this comment is wrong
	/**
	 * Get the method contour that contains the variable that changed.
	 * The method contour must be in the model, since we only track
	 * changes to monitored contours.
	 * @return method contour containing the changed variable
	 */
	//public ContourID getContourID();

	/**
	 * Get the variable that has changed value.
	 * This variable resides in the {@link #getContourID() contour}.
	 * @return variable that changed
	 */
	//public VariableID getVariableID();
	
  /**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(Exporter exporter);
	
	/**
	 * Importer (builder) for assign events.
	 * @author pvg
	 */
	public interface Importer extends Event.Importer {
    /**
     * Provide the updated value for the variable.
     * @return new value
     */
		public Value provideNewValue();
    
    /**
     * Provide the identifier for the contour that contains the 
     * variable whose value is changing.
     * @return contour identifier for enclosing contour
     */
		public ContourID provideContourID();
    
    /**
     * Provide the identifier for the variable whose value is changing.
     * @return the identifier for the variable whose value is changing
     */
		public VariableID provideVariableID();
	}
	
	/**
	 * Exporter (reverse-builder) for assign events.
	 * @author pvg
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the new value assigned to the variable.  This method is called by
		 * {@code AssignEvent#export(Exporter)}.
		 * 
		 * @param v the new value
		 */
		public void addNewValue(Value v);
		
		/**
		 * Adds the identifier for the contour that contains the variable whose
		 * value is changing.
		 * 
		 * @param contourID the variables context
		 */
		public void addContourID(ContourID contourID);
		
		/**
		 * Adds the identifier of the variable that has changed value.
		 * 
		 * @param variableID the variable identifier
		 */
		public void addVariableID(VariableID variableID);
	}
}
