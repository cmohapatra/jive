package edu.buffalo.cse.jive.ui.search;

/**
 * A pattern used for encapsulating a common {@code IJiveSearchQuery} input or
 * portion of input.  The input is broken into four parts:  class name, instance
 * number, method name, and variable name.  An empty string supplied as part of
 * the pattern will be converted to <code>null</code>.
 * <p>
 * This class is convenient for passing query input strings to query objects
 * at construction time.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveSearchPattern {
	
	/**
	 * A fully-qualified class name.
	 */
	private String className;
	
	/**
	 * An instance number of the class.
	 */
	private String instanceNumber;
	
	/**
	 * A method name of a method of the class.
	 */
	private String methodName;
	
	/**
	 * A member or local variable name.
	 */
	private String variableName;
	
	/**
	 * Creates a new pattern using the supplied builder.
	 * 
	 * @param builder the builder for the pattern
	 * @return a new pattern as described by the builder
	 */
	public static JiveSearchPattern createPattern(Importer builder) {
		return new JiveSearchPattern(
				builder.provideClassName().equals("") ? null : builder.provideClassName(),
				builder.provideInstanceNumber().equals("") ? null : builder.provideInstanceNumber(),
				builder.provideMethodName().equals("") ? null : builder.provideMethodName(),
				builder.provideVariableName().equals("") ? null : builder.provideVariableName());
	}
	
	/**
	 * A builder for a {@code JiveSearchPattern}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public interface Importer {
		
		/**
		 * Provides the class name for the pattern.
		 * 
		 * @return the class name
		 */
		public String provideClassName();
		
		/**
		 * Provides the instance number for the pattern.
		 * 
		 * @return the instance number
		 */
		public String provideInstanceNumber();
		
		/**
		 * Provides the method name for the pattern.
		 * 
		 * @return the method name
		 */
		public String provideMethodName();
		
		/**
		 * Provides the variable name for the pattern.
		 * 
		 * @return the variable name
		 */
		public String provideVariableName();
	}
	
	/**
	 * Constructs the search pattern with the supplied input.
	 * 
	 * @param type the fully-qualified class name
	 * @param instance the instance number
	 * @param method the method name
	 * @param variable the variable name
	 */
	protected JiveSearchPattern(String type, String instance, String method, String variable) {
		className = type;
		instanceNumber = instance;
		methodName = method;
		variableName = variable;
	}
	
	/**
	 * Returns the fully-qualified class name of the pattern, or
	 * <code>null</code> if none exists.
	 * 
	 * @return the fully-qualified class name
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Returns the instance number for the pattern, or <code>null</code> if none
	 * exists.
	 * 
	 * @return the instance number
	 */
	public String getInstanceNumber() {
		return instanceNumber;
	}
	
	/**
	 * Returns the method name of the pattern, or <code>null</code> if none
	 * exists.
	 * 
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * Returns the variable name of the pattern, or <code>null</code> if none
	 * exists.
	 * 
	 * @return the variable name
	 */
	public String getVariableName() {
		return variableName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = className;
		
		if (instanceNumber != null) {
			result += ":" + instanceNumber;
		}
		
		if (methodName != null) {
			result += "#" + methodName;
		}
		
		if (variableName != null) {
			result += "." + variableName;
		}
		
		return result;
	}
}
