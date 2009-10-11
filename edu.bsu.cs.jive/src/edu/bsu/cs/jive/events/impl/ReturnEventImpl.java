package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ReturnEvent;

/**
 * Default implementation of a return event.
 * 
 * @author pvg
 */
class ReturnEventImpl extends AbstractEventImpl
implements ReturnEvent {
  
  private final Returner returner;
  private final Value value;

  public ReturnEventImpl(ReturnEvent.Importer builder) {
    super(builder);
    this.returner = builder.providePreviousContext();
    this.value = builder.provideReturnValue();
  }
  
  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }

  public void export(ReturnEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addPreviousContext(returner);
    exporter.addReturnValue(value);
  }

  public Returner getPreviousContext() {
    return returner;
  }

  public Value getReturnValue() {
    return value;
  }
  
  @Override
  public String toString() {
  	// TODO Plug-in change
    return this.getClass().getName() + "("
    + paramString()
    + ", returning_method=" + returner
    + ", value = " + value + ")";
  }

}
