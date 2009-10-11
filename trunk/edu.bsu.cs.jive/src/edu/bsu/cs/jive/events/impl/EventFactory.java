package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;

/**
 * Factory class for this package.
 *
 * @author pvg
 */
public class EventFactory {

	private static EventFactory SINGLETON = new EventFactory();
	
	public static EventFactory instance() { return SINGLETON; }
	
	private EventFactory() {}
	
	public LoadEvent createLoadEvent(LoadEvent.Importer builder) {
		return new LoadEventImpl(builder);
	}
	
  public NewEvent createNewEvent(NewEvent.Importer builder) {
    return new NewEventImpl(builder);
  }
  
  public CallEvent createCallEvent(CallEvent.Importer builder) {
  	return new CallEventImpl(builder);
  }
  
  public ReturnEvent createReturnEvent(ReturnEvent.Importer builder) {
    return new ReturnEventImpl(builder);
  }
  
  public AssignEvent createAssignEvent(AssignEvent.Importer builder) {
  	return new AssignEventImpl(builder);
  }
  
  public EOSEvent createEOSEvent(EOSEvent.Importer builder) {
  	return new EOSEventImpl(builder);
  }
  
  public ExitEvent createExitEvent(ExitEvent.Importer builder) {
    return new ExitEventImpl(builder);
  }
  
  public ExceptionEvent createExceptionEvent(ExceptionEvent.Importer builder) {
    return new ExceptionEventImpl(builder);
  }
  
  public StartEvent createStartEvent(StartEvent.Importer builder) {
    return new StartEventImpl(builder);
  }
  
  public ThrowEvent createThrowEvent(ThrowEvent.Importer builder) {
  	return new ThrowEventImpl(builder);
  }
  
  public CatchEvent createCatchEvent(CatchEvent.Importer builder) {
  	return new CatchEventImpl(builder);
  }
}
