package edu.buffalo.cse.jive.sequence.java;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * A <code>JavaExecutionActivation</code> representing the execution of a
 * thread.
 * 
 * @see JavaExecutionActivation
 * @author Jeffrey K Czyz
 */
public interface ThreadActivation extends JavaExecutionActivation {
	
	// TODO Determine if we should use importers
	/**
	 * A builder for thread activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Importer extends JavaExecutionActivation.Importer {
		public ThreadID provideThread();
	}
	
	// TODO Determine if we should use exporters
	/**
	 * A reverse-builder for thread activations.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Exporter extends JavaExecutionActivation.Exporter {
		public void addThread();
	}
	
	/**
	 * Returns the identifier of the thread that the activation represents.
	 * 
	 * @return the thread identifier of the activation
	 */
	public ThreadID thread();
}
