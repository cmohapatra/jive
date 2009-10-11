package edu.bsu.cs.jive.runtime;

import com.sun.jdi.ClassType;

/**
 * Checks if classes are inner classes or not.
 *
 * @author pvg
 */
public class InnerClassTester {

	/**
	 * Check if the provided class type is an inner class or not.
	 * 
	 * @param type
	 * @return true if the type is an inner class.
	 */
	public static boolean isInnerClass(ClassType type) {
		//TODO Plug-in change
//		// The current heuristic used is that, if there's a dollar-sign in the name,
//		// then it's most likely an inner class.
//		return type.name().contains("$");
		return type.name().contains("$") && !type.name().startsWith("$");  // In case of $Proxy0
	}
	
}
