package edu.buffalo.cse.jive.sequence.java;

import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;

/**
 * An <code>ExecutionOccurrence</code> particular to Java programs, such as
 * thread and method activations.
 * 
 * @see ExecutionOccurrence
 * @see ThreadActivation
 * @see MethodActivation
 * @see FilteredMethodActivation
 * @author Jeffrey K Czyz
 */
public interface JavaExecutionActivation extends ExecutionOccurrence {
	
	// TODO Determine if we should use importers and exporters
//	public interface Importer extends ExecutionOccurrence.Importer {
//		
//	}
//	
//	public interface Exporter extends ExecutionOccurrence.Exporter {
//		
//	}
	
	/**
	 * A visitor for a <code>JavaExecutionActivation</code>.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Visitor {
		
		/**
		 * Visits a <code>ThreadActivation</code>.
		 * 
		 * @param activation the thread activation to visit
		 * @param arg the argument to the visitor
		 * @return the result of the visitor
		 */
		public Object visit(ThreadActivation activation, Object arg);
		
		/**
		 * Visits a <code>MethodActivation</code>.
		 * 
		 * @param activation the method activation to visit
		 * @param arg the argument to the visitor
		 * @return the result of the visitor
		 */
		public Object visit(MethodActivation activation, Object arg);
		
		/**
		 * Visits a <code>FilteredMethodActivation</code>.
		 * 
		 * @param activation the filtered method activation to visit
		 * @param arg the argument to the visitor
		 * @return the result of the visitor
		 */
		public Object visit(FilteredMethodActivation activation, Object arg);
	}
	
	/**
	 * Processes a visitor on the activation. 
	 * 
	 * @param visitor the visitor to process
	 * @param arg the argument to the visitor
	 * @return the result of the visitor
	 */
	public Object accept(Visitor visitor, Object arg);
}
