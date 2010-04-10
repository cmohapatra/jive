package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.StringSeparator;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Simple implementation of an exception event
 * @author pvg
 */
final class ExceptionEventImpl extends AbstractEventImpl implements
		ExceptionEvent {

	private final Value exception;
	private final VariableID v;
	private final ContourID catcher;
	
	public ExceptionEventImpl(ExceptionEvent.Importer importer) {
		super(importer);
		this.exception=importer.provideException();
		this.v=importer.provideVariable();
		this.catcher=importer.provideCatcher();
	}
	
	public void export(ExceptionEvent.Exporter exporter) {
		super.export(exporter);
		exporter.addCatcher(catcher);
		exporter.addVariable(v);
		exporter.addException(exception);
	}

	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}
	
	// TODO Plug-in change
  @Override
  public String toString() {
  	return this.getClass().getName() + "(" + paramString() + ")";
  }

}
