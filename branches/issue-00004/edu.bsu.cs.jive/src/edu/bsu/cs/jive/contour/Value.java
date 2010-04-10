package edu.bsu.cs.jive.contour;

import edu.bsu.cs.jive.util.ContourID;


/**
 * The encoding of a value within a contour model.
 * Values for variables may be encoded literals, references to contours,
 * references outside of the model.
 * Values for other elements of contours' member tables, which are
 * methods and inner classes, are the definitions of those
 * methods and inner classes.
 *
 * @see edu.bsu.cs.jive.events.Event
 * @author pvg
 */
public interface Value {

	/**
	 * A value that is a reference to a contour.
	 * @author pvg 
	 */
	public interface ContourReference extends Value {
		
		 /**
	   * A reverse-builder for contour references.
	   * 
	   * @author pvg
	   */
    public interface Exporter {
      /**
       * Export the identifier of the contour referenced by this value
       * 
       * @param contourID contour identifier
       */
      public void addContourID(ContourID contourID);
    }

    public void export(Exporter e);
    
    
    // TODO Plug-in change
    /**
     * Returns the identifier of the contour referenced by the value.
     * 
     * @return the contour identifier
     */
    public ContourID getContourID();
  }
	
	/**
	 * Representation of a null reference.
	 * @author pvg
	 */
	public interface Null extends Value {
	}
	
	/**
	 * A value that is described as text.
	 * @author pvg
	 */
	public interface Encoded extends Value {
		/**
		 * Get the text description of this encoded value
		 * @see java.lang.Object#toString()
		 * @return text description of this encoded value
		 */
		public String toString();
	}
	
	/**
	 * Representation of an uninitialized value.
	 * This is the value held by variables that have not yet
	 * come into scope, for example.
	 * @author pvg
	 */
	public interface Uninitialized extends Value {
	}
  
  /**
   * The value of a method declaration in the contour model.
   * <p>
   * The "value" of a method is, semantically, its definition.
   * However, this is unwieldy to include in the member table
   * of a contour.  Hence, the value is abstracted to be "something"
   * that shows up in the member table, usually "methodname.cf" (cf for
   * contour format).
   * @author pvg
   */
  public interface MethodDefinition extends Value {
  }
	
  /**
   * The value of an inner class declaration in the contour model.
   * @see MethodDefinition
   * @author pvg
   */
  public interface InnerClassDefinition extends Value {}
  
	/**
	 * A visitor for all types of values.
	 * @author pvg
	 */
	public interface Visitor {
		public Object visit(ContourReference value, Object arg);
		public Object visit(Encoded value, Object arg);
		public Object visit(Null value, Object arg);
		public Object visit(Uninitialized value, Object arg);
    public Object visit(MethodDefinition value, Object arg);
    public Object visit(InnerClassDefinition value, Object arg);
	}
	
	/**
	 * Accept a visitor
	 * @param v visitor
	 * @param arg the visitor argument
	 * @return result of visitation
	 */
	public Object accept(Visitor v, Object arg);
}
