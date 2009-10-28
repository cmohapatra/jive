package edu.bsu.cs.jive.contour.jivelog_adapter;

import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * Adapts jivelog execution events into modifications on a contour model.
 * <p>
 * This class delegates to the other, event-specific classes in this package.
 * 
 * @author pvg
 */
public class JiveLogToContourModelAdapter implements EventSource.Listener {

  /** The contour model to which contours are added. */
  private final JavaInteractiveContourModel cm;
  
  /**
   * Create a new jivelog event interpreter for the given contour model
   * @param cm contour model
   */
  public JiveLogToContourModelAdapter(JavaInteractiveContourModel cm) {
    this.cm=cm;
  }
  
  /**
   * Called when a jivelog event is fired by an event source.
   * This is the main entrypoint for this class:  once an event is received,
   * it will be interpreted into a contour model transaction and added
   * to the contour model.
   */
  public void eventOccurred(EventSource source, Event event) {
    event.accept(eventProcessor, source);
  }
  
  private Event.Visitor eventProcessor = new Event.Visitor() {

    public Object visit(AssignEvent event, Object arg) {
      event.export(assignEventAdapter);
      assignEventAdapter.apply(cm);
      return null;
    }

    public Object visit(CallEvent event, Object arg) {
      event.export(callEventAdapter);
      callEventAdapter.apply(cm);
      return null;
    }

    public Object visit(EOSEvent event, Object arg) {
      event.export(eosEventAdapter);
      eosEventAdapter.apply(cm);
      return null;
    }

    public Object visit(ExceptionEvent event, Object arg) {
      event.export(exEventAdapter);
      exEventAdapter.apply(cm);
      return null;
    }

    public Object visit(ExitEvent event, Object arg) {
      // Nothing special required here; there will just be no more cm mods.
      return null;
    }
    
    public Object visit(StartEvent event, Object arg) {
      // Nothing special required here.
      return null;
    }

    public Object visit(LoadEvent event, Object arg) {
      event.export(loadEventAdapter);
      loadEventAdapter.apply(cm);
      return null;
    }

    public Object visit(NewEvent event, Object arg) {
      event.export(newEventAdapter);
      newEventAdapter.apply(cm);
      return null;
    }

    public Object visit(ReturnEvent event, Object arg) {
      event.export(returnEventAdapter);
      returnEventAdapter.apply(cm);
      return null;
    }

    public Object visit(CatchEvent event, Object arg) {
      cm.startTransactionRecording();
      cm.endTransactionRecording(event.number());
      return null;
    }

    public Object visit(ThrowEvent event, Object arg) {
      if (event.wasFramePopped()) {				
        Thrower thrower = event.getThrower();
        if (thrower instanceof Thrower.InModel) {
          cm.startTransactionRecording();

          ThreadID thread = event.thread();
          ContourID contour = cm.peek(thread).id();
          assert ((Thrower.InModel) thrower).contour().equals(contour);
          cm.removeMethodContour(contour);

          cm.endTransactionRecording(event.number());
        }
      }

      return null;
    }

  };
  private final AssignEventAdapter assignEventAdapter = new AssignEventAdapter();
  private final ExceptionEventAdapter exEventAdapter = new ExceptionEventAdapter();
  private final CallEventAdapter callEventAdapter = new CallEventAdapter();
  private final NewEventAdapter newEventAdapter = new NewEventAdapter();
  private final LoadEventAdapter loadEventAdapter = new LoadEventAdapter();
  private final ReturnEventAdapter returnEventAdapter = new ReturnEventAdapter();
  private final EOSEventAdapter eosEventAdapter = new EOSEventAdapter();
}
