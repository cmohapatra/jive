package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.Value;

/**
 * Default implementation of a method definition.
 *
 * @author pvg
 */
public class MethodDefinitionValue implements Value.MethodDefinition {

	public Object accept(Visitor v, Object arg) {
		return v.visit(this,arg);
	}

	@Override public String toString() {
		//TODO: This should include the method's name, probably, but this is sufficient for now
		return "method";
	}

}
