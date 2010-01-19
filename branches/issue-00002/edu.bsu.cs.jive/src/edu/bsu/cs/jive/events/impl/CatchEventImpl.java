package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Simple implementation of a catch event
 * @author jkczyz
 */
final class CatchEventImpl extends AbstractEventImpl implements
		CatchEvent {

	private final Value exception;
	private final VariableID v;
	private final ContourID catcher;
	
	public CatchEventImpl(CatchEvent.Importer importer) {
		super(importer);
		this.exception=importer.provideException();
		this.v=importer.provideVariable();
		this.catcher=importer.provideCatcher();
	}
	
	public void export(CatchEvent.Exporter exporter) {
		super.export(exporter);
		exporter.addCatcher(catcher);
		exporter.addVariable(v);
		exporter.addException(exception);
	}

	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}
	
  @Override
  public String toString() {
  	return this.getClass().getName() + "(" + paramString() + ")";
  }

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent#getCatcher()
	 */
	public ContourID getCatcher() {
		return catcher;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent#getException()
	 */
	public Value getException() {
		return exception;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.CatchEvent#getVariable()
	 */
	public VariableID getVariable() {
		return v;
	}

}
