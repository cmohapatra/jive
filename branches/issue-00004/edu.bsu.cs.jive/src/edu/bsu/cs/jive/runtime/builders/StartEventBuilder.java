package edu.bsu.cs.jive.runtime.builders;

import com.sun.jdi.event.VMStartEvent;

import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;

public class StartEventBuilder extends AbstractEventBuilder 
implements StartEvent.Importer {

  public StartEventBuilder(VMStartEvent e, ContourUtils utils) {
    super(utils.nextEventNumber(), null);
  }

}
