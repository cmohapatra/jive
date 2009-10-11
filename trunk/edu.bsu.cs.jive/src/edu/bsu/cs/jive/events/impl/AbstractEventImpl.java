package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;

/**
 * Default implementation of the abstract superclass of execution events.
 * 
 * @author pvg
 */
public abstract class AbstractEventImpl implements Event {
  
  private final long n;
  private final ThreadID threadID;
  
  protected AbstractEventImpl(Importer importer) {
    this.n=importer.provideNumber();
    this.threadID=importer.provideThreadID();
  }
  
  /**
   * Export this event's information to the given exporter (reverse-builder).
   * Subclasses should call this implementation when they override
   * their own interfaces' export methods.
   * @param exporter
   */
  protected final void export(Exporter exporter) {
    exporter.addNumber(n);
    exporter.addThreadID(threadID);
  }
  
  // TODO Plug-in change
  public long number() {
  	return n;
  }

  // TODO Plug-in change
  public ThreadID thread() {
  	return threadID;
  }

  /**
   * Provide a string version of this abstract object's contents.
   * This can be appended into the {@link #toString()} methods of subclasses.
   * @return string version of this object's state
   */
  protected final String paramString() {
  	return "n=" + n + ",threadID=" + threadID;
  }
}
