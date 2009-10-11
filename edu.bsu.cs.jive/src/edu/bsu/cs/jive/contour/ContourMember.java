package edu.bsu.cs.jive.contour;

import edu.bsu.cs.jive.util.VariableID;

/**
 * A member of a contour. Each member has an entry that contains three fields:
 * name, type, and value.
 * 
 * @author pvg
 */
public interface ContourMember {

	/**
	 * Get the name of this member entry, as a string.
	 * 
	 * @return member name
	 */
	public String name();

	/**
	 * Get the type of this member entry, as a string.
	 * 
	 * @return member type (as a string)
	 */
	public Type type();

	/**
	 * Get the value stored in this member. This should never be null.
	 * 
	 * @return member value
	 */
	public Value value();

	/**
	 * Accept a visitor
	 * 
	 * @param visitor
	 *          the visitor
	 * @param arg
	 *          the visitor argument
	 * @return visitor result
	 */
	public Object accept(Visitor visitor, Object arg);

	/**
	 * Visit any member.
	 * 
	 * @author pvg
	 */
	public interface Visitor {
		public Object visit(MethodDeclaration m, Object arg);

		public Object visit(Variable v, Object arg);

		public Object visit(InnerClass c, Object arg);
	}

  /**
   * A method declaration member within a contour.
   * 
   * @author pvg
   */
	public interface MethodDeclaration extends ContourMember {
   
    public void export(MethodDeclaration.Exporter exporter);
    
  }

  /**
   * A variable declaration member within a contour.
   * 
   * @author pvg
   */
	public interface Variable extends ContourMember {
   
    /**
     * Get the variable ID for this variable.
     * @return variable ID
     */
    public VariableID id();
    
    public void export(Variable.Exporter exporter);
    
    /**
     * A builder for variable declaration members.
     * 
     * @author pvg
     */
    public interface Importer extends ContourMember.Importer {
      public VariableID provideID();
    }
    
    /**
     * A reverse-builder for variable declaration members.
     * 
     * @author pvg
     */
    public interface Exporter extends ContourMember.Exporter {
      public void addID(VariableID id);
    }
    
  }

  /**
   * An inner class declaration within a contour.
   * 
   * @author pvg
   */
	public interface InnerClass extends ContourMember {
   
    public void export(InnerClass.Exporter exporter);
    
  }
	
	 /**
   * A builder for contour members.
   * 
   * @author pvg
   */
	public interface Importer {
		public String provideName();
		public Type provideType();
		public Value provideValue();
	}
	
	 /**
   * A reverse-builder for contour members.
   * 
   * @author pvg
   */
	public interface Exporter {
		public void addName(String name);
		public void addType(Type type);
		public void addValue(Value value);
	}
}
