package edu.bsu.cs.jive.runtime.builders;

import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;

import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;

public class ExitEventBuilder extends AbstractEventBuilder 
implements ExitEvent.Importer {

  public ExitEventBuilder(VMDeathEvent e, ContourUtils utils) {
    super(utils.nextEventNumber(), null);
  }
  
  // TODO Plug-in change
  public ExitEventBuilder(VMDisconnectEvent e, ContourUtils utils) {
    super(utils.nextEventNumber(), null);
  }

}
