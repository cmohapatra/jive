package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * An event corresponding to an exception's being caught.
 * The exception may be caught by a method or by the VM if no method
 * caught it.
 *
 * @author pvg
 * @deprecated As of JIVE Platform 0.4, replaced by {@link ThrowEvent} and {@link CatchEvent}.
 */
public interface ExceptionEvent extends Event {

	/**
	 * Get the exception itself.
	 * This is encoded as a value since the object contour for the
	 * exception may or may not be available.
	 * @return the value of the exception
	 */
	//public Value getException();
	
	/**
	 * Get the method that caught the exception.
	 * If no method caught the exception, this will return null.
	 * @return the catching method contour or null if it is uncaught.
	 */
	//public ContourID getCatcher();
	
	/**
	 * Get the variable of the contour that holds the exception.
	 * This is only meaningful of {@link #getCatcher()} returns 
	 * a proper contour.
	 * @return the variable of the contour that holds the exception
	 *  or null if there is no catcher.
	 */
	//public VariableID getVariable();
	
	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(Exporter exporter);
	
	/**
	 * Importer (builder) for an exception event.
	 * @author pvg
	 */
	public interface Importer extends Event.Importer {
		
		/**
		 * Provides the exception that was caught.
		 * 
		 * @return the exception
		 */
		public Value provideException();
		
		/**
		 * Provides the catcher of the exception.
		 * 
		 * @return the catcher
		 */
		public ContourID provideCatcher();
		
		/**
		 * Provides the variable identifier holding the exception that was caught.
		 * 
		 * @return the variable identifier
		 */
		public VariableID provideVariable();
	}
	
	/**
	 * Exporter (reverse-builder) for an exception event.
	 *
	 * @author pvg
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the exception that was caught.  This is called by
		 * {@code ExceptionEvent#export(Exporter)}.
		 * 
		 * @param exception the exception
		 */
		public void addException(Value exception);
		
		/**
		 * Adds the catcher of the exception.  This This is called by
		 * {@code ExceptionEvent#export(Exporter)}.
		 * 
		 * @param catcher the catcher
		 */
		public void addCatcher(ContourID catcher);
		
		/**
		 * Adds the variable identifier holding the exception that was caught.  This
		 * is called by {@code ExceptionEvent#export(Exporter)}.
		 * 
		 * @param v the variable identifier
		 */
		public void addVariable(VariableID v);
	}
	
}
