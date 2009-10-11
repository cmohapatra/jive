package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.Value;

/**
 * Default implementation of an uninitialized value.
 * <p>
 * This implementation uses a singleton pattern, which implies that
 * all uninitialized values are, in a sense, equal.
 * Hence, care should be taken when using this class in cases
 * where equality must be tested, since the specific semantics
 * of "equality" depend on the situation.
 * 
 *
 * @author pvg
 */
public class UninitializedValue implements Value.Uninitialized {

	private static final UninitializedValue SINGLETON = new UninitializedValue();
	
	public static UninitializedValue instance() { return SINGLETON; }
	
	private UninitializedValue() {}
	
	public Object accept(Visitor v, Object arg) {
		return v.visit(this,arg);
	}
	
	@Override
	public String toString() {
		return "?";
	}
}
