package edu.bsu.cs.jive.contour;

import edu.bsu.cs.jive.util.VariableID;

/**
 * Specifies the format of a contour member.
 * <p>
 * A contour member format is defined as its name and type, 
 * but not its value.  A contour member combines this with a value.
 *
 * @see edu.bsu.cs.jive.contour.ContourFormat
 * @author pvg
 */
public interface ContourMemberFormat {

	/**
	 * Get the name of this member.
	 * @return member's name
	 */
	public String name();
	
	/**
	 * Get the type of this member.
	 * @return member's type
	 */
	public Type type();
	
	/**
	 * Export this contour member format.
	 * @param exporter
	 */
	public void export(Exporter exporter);
	
	/**
	 * Builder for contour member formats.
	 * @author pvg
	 */
	public interface Importer {
		public String provideName();
		public Type provideType();
	}
	
	/**
	 * Reverse-builder for contour member formats.
	 * @author pvg
	 */
	public interface Exporter {
		public void addName(String name);
		public void addType(Type type);
	}
	
	/**
	 * Visitor for contour member formats.
	 * @author pvg
	 */
	public interface Visitor {
		public Object visit(MethodFormat m, Object arg);
		public Object visit(VariableFormat v, Object arg);
		public Object visit(InnerClassFormat c, Object arg);
	}
	
	/**
	 * Accept a visitor
	 * @param visitor
	 * @param arg
	 * @return visitation result
	 */
	public Object accept(Visitor visitor, Object arg);
	
	/**
	 * Member format interface for method declarations.
	 *
	 * @author pvg
	 */
	public interface MethodFormat extends ContourMemberFormat {}
	
	/**
	 * Member format interface for inner class declarations.
	 *
	 * @author pvg
	 */
	public interface InnerClassFormat extends ContourMemberFormat {}
	
	/**
	 * Member format interface for variable declarations.
	 *
	 * @author pvg
	 */
	public interface VariableFormat extends ContourMemberFormat {
		
		/**
     * A builder for variable contour member formats.
     * 
     * @author pvg
     */
		public interface Importer extends ContourMemberFormat.Importer {
			/**
			 * Provide the contour-unique identifier for this variable.
			 * This is necessary since two variables in the same contour
			 * can have the same name (e.g. in parallel, independent nested
			 * contours).  Note that if contours are used for every scope, this
			 * is not a problem, but we do not want to impose such a restriction
			 * here.
			 * 
			 * @return variable ID for this variable 
			 */
			public VariableID provideID();
		}
		
		/**
     * A reverse-builder for variable contour member variable formats.
     * 
     * @author pvg
     */
		public interface Exporter extends ContourMemberFormat.Exporter {
			/**
			 * Add the contour-unique variable identifier.
			 * @param id the variable id
			 */
			public void addID(VariableID id);
		}
		
		/**
		 * Export to the given reverse-builder.
		 * @param exporter the reverse-builder
		 */
		public void export(VariableFormat.Exporter exporter);
	}

	
}
