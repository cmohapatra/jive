package edu.bsu.cs.jive.events.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CallEvent;

class CallEventImpl extends AbstractEventImpl implements CallEvent {

  private final Caller caller;
  private final Target target;
  private final List<Value> actuals;
  
  public CallEventImpl(CallEvent.Importer importer) {
    super(importer);
    caller=importer.provideCaller();
    target=importer.provideTarget();
    actuals=new ArrayList<Value>(importer.provideActualParams());
  }
  
  public void export(CallEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addCaller(caller);
    exporter.addTarget(target);
    exporter.addActualParams(Collections.unmodifiableList(actuals));
  }

  public final Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }
  
  @Override
  public String toString() {
  	// TODO Plug-in change
    String s = String.format("%s(%s,caller=%s,target=%s,actuals=",
        this.getClass().getName(), paramString(), caller, target);
    for (Value value : actuals) 
      s += value + ",";
    return s + ")";
  }

}
