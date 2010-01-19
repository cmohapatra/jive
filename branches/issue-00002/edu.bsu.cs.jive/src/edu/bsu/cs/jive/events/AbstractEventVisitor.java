package edu.bsu.cs.jive.events;


/**
 * Abstract JiveLog event visitor that provides empty implementations
 *  for all methods.
 * 
 * @author pvg
 */
public abstract class AbstractEventVisitor implements Event.Visitor {

  public Object visit(AssignEvent event, Object arg) {
    return null;
  }

  public Object visit(CallEvent event, Object arg) {
    return null;
  }
  
  public Object visit(CatchEvent event, Object arg) {
  	return null;
  }

  public Object visit(EOSEvent event, Object arg) {
    return null;
  }

  public Object visit(ExceptionEvent event, Object arg) {
    return null;
  }

  public Object visit(ExitEvent event, Object arg) {
    return null;
  }

  public Object visit(LoadEvent event, Object arg) {
    return null;
  }

  public Object visit(NewEvent event, Object arg) {
    return null;
  }

  public Object visit(ReturnEvent event, Object arg) {
    return null;
  }
  
  public Object visit(StartEvent event, Object arg) {
    return null;
  }
  
  public Object visit(ThrowEvent event, Object arg) {
  	return null;
  }
}
