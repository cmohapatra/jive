package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.contour.Value;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * An event corresponding to an exception being caught.
 * 
 * @author Jeffrey K Czyz
 */
public interface CatchEvent extends Event {
	
	/**
	 * Returns the contour ID of the method activation that caught the exception.
	 * 
	 * @return the catcher's contour ID
	 */
	public ContourID getCatcher();
	
	/**
	 * Returns the variable of the catch clause that was assigned the exception if
	 * it is known.
	 * 
	 * @return the variable of the catch clause, or <code>null</code> if unknown
	 */
	public VariableID getVariable();
	
	/**
	 * Returns the exception that was caught.
	 * 
	 * @return the caught exception
	 */
	public Value getException();
	
	/**
	 * Exports the event using the provided exporter.
	 * 
	 * @param exporter the exporter
	 */
	public void export(CatchEvent.Exporter exporter);
	
	/**
	 * Importer (builder) for catch events.
	 */
	public interface Importer extends Event.Importer {
		
		/**
		 * Provides the contour ID of the method activation that caught the
		 * exception.
		 * 
		 * @return the catcher's contour ID
		 */
		public ContourID provideCatcher();
		
		/**
		 * Provides the variable of the catch clause that was assigned the
		 * exception.
		 * 
		 * @return the variable of the catch clause, or <code>null</code> if unknown
		 */
		public VariableID provideVariable();
		
		/**
		 * Provides the exception that was caught.
		 * 
		 * @return the caught exception
		 */
		public Value provideException();
	}
	
	/**
	 * Exporter (reverse-builder) for catch events.
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the contour ID of the method activation that caught the exception.
		 * 
		 * @param catcher the catcher's contour ID
		 */
		public void addCatcher(ContourID catcher);
		
		/**
		 * Adds the variable of the catch clause that was assigned the exception.
		 * 
		 * @param variable the variable of the catch clause, or <code>null</code>
		 *                 if unknown
		 */
		public void addVariable(VariableID variable);
		
		/**
		 * Adds the exception that was caught.
		 * 
		 * @param exception the caught exception
		 */
		public void addException(Value exception);
	}
}
