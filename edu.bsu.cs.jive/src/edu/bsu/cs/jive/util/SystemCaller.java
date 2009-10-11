package edu.bsu.cs.jive.util;

import edu.bsu.cs.jive.events.CallEvent;

/**
 * Singleton system caller.
 * 
 * @see edu.bsu.cs.jive.events.CallEvent.Caller.System
 * @author pvg
 */
public final class SystemCaller implements CallEvent.Caller.System {

  private static final SystemCaller SINGLETON = new SystemCaller();
  
  public static SystemCaller instance() { return SINGLETON; }
  
  private SystemCaller() {}
  
  public Object accept(Visitor v, Object arg) {
    return v.visit(this,arg);
  }
  
  @Override public String toString() {
    return SystemCaller.class.getName();
  }

}
