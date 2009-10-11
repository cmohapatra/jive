package edu.buffalo.cse.jive.ui.search;

import java.util.StringTokenizer;

import edu.bsu.cs.jive.util.ContourID;

/**
 * A utility class used to break a {@code ContourID} into its various
 * components.
 * 
 * @see ContourID
 * @author Jeffrey K Czyz
 */
public class ContourIDTokenizer {
	
	/**
	 * The fully-qualified class name associated with the {@code ContourID}.
	 */
	private String className;
	
	/**
	 * The instance number of the {@code #className} associated with the
	 * {@code ContourID}.
	 */
	private String instanceNumber;
	
	/**
	 * The method name associated with the {@code ContourID}.
	 */
	private String methodName;
	
	/**
	 * The call number of the {@code #methodName} associated with the
	 * {@code ContourID}.
	 */
	private String callNumber;

	/**
	 * Constructs a tokenizer for the supplied {@code ContourID} and tokenizes it.
	 * 
	 * @param id the contour ID to be parsed
	 */
	public ContourIDTokenizer(ContourID id) {
		tokenize(id);
	}
	
	/**
	 * Tokenizes the supplied {@code ContourID} into its various components.
	 * 
	 * @param id the contour ID to tokenize
	 */
	private void tokenize(ContourID id) {
		StringTokenizer tokenizer = new StringTokenizer(id.toString(), ":#", true);
		className = tokenizer.nextToken();
		
		if (tokenizer.hasMoreTokens()) {
			String delimiter = tokenizer.nextToken();
			if (delimiter.equals(":")) {
				instanceNumber = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()) {
					delimiter = tokenizer.nextToken();
				}
				else { // ID for instance contour
					return;
				}
			}
			
			// ID for method contour
			if (delimiter.equals("#")) {
				methodName = tokenizer.nextToken();
				callNumber = tokenizer.nextToken();
			}
		}
		else { // ID for static contour
			return;
		}
	}
	
	/**
	 * Returns the fully-qualified class name associated with the
	 * {@code ContourID}.
	 * 
	 * @return the fully-qualified class name 
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the instance number associated with the {@code ContourID}.  This
	 * number is unique per class.  Returns <code>null</code> if the contour ID
	 * references a static contour or a static method contour. 
	 * 
	 * @return the instance number or <code>null</code> if there is none
	 */
	public String getInstanceNumber() {
		return instanceNumber;
	}

	/**
	 * Returns the method name associated with the {@code ContourID}, or
	 * <code>null</code> if the contour ID references a static or an instance
	 * contour.
	 * 
	 * @return the method name or <code>null</code> if there is none
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * Returns the call number associated with the {@code ContourID}.  This
	 * number is unique per class (not per instance).  Returns <code>null</code>
	 * if the contour ID references a static or an instance contour.
	 * 
	 * @return the call number or <code>null</code> if there is none
	 */
	public String getCallNumber() {
		return callNumber;
	}
}
