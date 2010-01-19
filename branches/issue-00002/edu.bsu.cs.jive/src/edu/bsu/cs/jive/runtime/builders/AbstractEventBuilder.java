package edu.bsu.cs.jive.runtime.builders;

import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.util.ThreadID;

abstract class AbstractEventBuilder implements Event.Importer {

  private final long n;
  private final ThreadID threadID;
  
  public AbstractEventBuilder(long n, ThreadID threadID) {
    this.n = n;
    this.threadID=threadID;
  }
  
  public long provideNumber() {
    return n;
  }

  public ThreadID provideThreadID() {
    return threadID;
  }

}
