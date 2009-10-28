package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Type;
import edu.bsu.cs.jive.contour.Value;

/**
 * Default abstract contour member implementation.
 * 
 * @author pvg
 */
abstract class AbstractContourMemberImpl implements ContourMember, MutableContourMember {

	private String name;

	private Type type;

	private Value value;
	
	protected AbstractContourMemberImpl(ContourMember.Importer importer) {
		this.name=importer.provideName();
		this.value=importer.provideValue();
		this.type=importer.provideType();
	}

	public void export(Exporter exporter) {
		exporter.addName(name);
		exporter.addType(type);
		exporter.addValue(value);
	}

	public String name() {
		return name;
	}

	public Type type() {
		return type;
	}

	public Value value() {
		return value;
	}
  
  public void setValue(Value value) { this.value=value; }

	@Override
	public String toString() {
		return String.format("%s(name=%s,type=%s,value=%s)", 
				this.getClass().getName(), name(), type(), value());
	}
}