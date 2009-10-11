package edu.buffalo.cse.jive.sequence.java;

/**
 * A <code>JavaExecutionActivation</code> representing the execution of a
 * method.
 * 
 * @see JavaExecutionActivation
 * @author Jeffrey K Czyz
 */
public interface MethodActivation extends JavaExecutionActivation {

	// TODO Determine if we should use importers
	/**
	 * A builder for method activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Importer extends JavaExecutionActivation.Importer {
		
	}
	
	// TODO Determine if we should use exporters
	/**
	 * A reverse-builder for method activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Exporter extends JavaExecutionActivation.Exporter {
		
	}
	
	// TODO Determine if anything else should be added
}
