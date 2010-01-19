package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.Value;

public class InnerClassDefinitionValue implements Value.InnerClassDefinition {

	public Object accept(Visitor v, Object arg) {
		return v.visit(this,arg);
	}
	
	@Override
	public String toString() {
		//TODO: this should incorporate the class' name, but this is sufficient for now
		return "inner class";
	}

}
