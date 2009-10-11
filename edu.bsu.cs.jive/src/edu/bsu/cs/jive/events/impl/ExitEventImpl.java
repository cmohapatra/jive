package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.events.ExitEvent;

public class ExitEventImpl extends AbstractEventImpl 
implements ExitEvent {

  ExitEventImpl(ExitEvent.Importer importer) {
    super(importer);
  }
  
  public Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }
  
  public void export(ExitEvent.Exporter exporter) {
    super.export(exporter);
  }

	// TODO Plug-in change
  @Override
  public String toString() {
  	return this.getClass().getName() + "(" + paramString() + ")";
  }
}
