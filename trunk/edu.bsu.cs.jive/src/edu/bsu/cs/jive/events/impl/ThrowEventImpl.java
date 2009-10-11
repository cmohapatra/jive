package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ThrowEvent;

/**
 * Simple implementation of an exception event
 * @author jkczyz
 */
final class ThrowEventImpl extends AbstractEventImpl implements
		ThrowEvent {

	private final Thrower thrower;
	private final Value exception;
	private final boolean framePopped;
	
	public ThrowEventImpl(ThrowEvent.Importer importer) {
		super(importer);
		this.thrower = importer.provideThrower();
		this.exception = importer.provideException();
		this.framePopped = importer.provideFramePopped();
	}
	
	public void export(ThrowEvent.Exporter exporter) {
		super.export(exporter);
		exporter.addThrower(thrower);
		exporter.addException(exception);
		exporter.addFramePopped(framePopped);
	}

	public Object accept(Visitor visitor, Object arg) {
		return visitor.visit(this,arg);
	}
	
  @Override
  public String toString() {
  	return this.getClass().getName() + "(" + paramString() + ")";
  }

  /* (non-Javadoc)
   * @see edu.bsu.cs.jive.events.ThrowEvent#getThrower()
   */
  public Thrower getThrower() {
  	return thrower;
  }
  
	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent#getException()
	 */
	public Value getException() {
		return exception;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.ThrowEvent#wasFramePopped()
	 */
	public boolean wasFramePopped() {
		return framePopped;
	}
}
