package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.events.StartEvent;

class StartEventImpl extends AbstractEventImpl implements StartEvent {

  public StartEventImpl(StartEvent.Importer importer) {
    super(importer);
  }
  
  public void export(edu.bsu.cs.jive.events.StartEvent.Exporter exporter) {
    super.export(exporter);
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
