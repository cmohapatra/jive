package edu.buffalo.cse.jive.sequence.java;

/**
 * A <code>JavaExecutionActivation</code> representing the execution of a
 * method that eventually resulted in a <code>MethodActivation</code>, but
 * itself has neither an identifier nor a context.
 * 
 * @see JavaExecutionActivation
 * @author Jeffrey K Czyz
 */
public interface FilteredMethodActivation extends MethodActivation {

	/**
	 * Returns a description of the filtered method activation.
	 * 
	 * @return a description of the activation
	 */
	public String description();
	
	// TODO Determine if we should use importers
	/**
	 * A builder for filtered method activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Importer extends MethodActivation.Importer {
		public String provideDescription();
	}
	// TODO Determine if we should use exporters
	/**
	 * A reverse-builder for filtered method activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Exporter extends MethodActivation.Exporter {
		public void addDescription(String description);
	}
	
	// TODO Determine if anything else should be added
}
