package edu.bsu.cs.jive.events.impl;

import edu.bsu.cs.jive.events.EOSEvent;

/**
 * Default implementation of an end-of-statement event.
 * 
 * @author pvg
 */
class EOSEventImpl extends AbstractEventImpl implements EOSEvent {

  private final int line;
  private final String file;
  
  // TODO Plug-in change
  public String getFilename() {
  	return file;
  }
  
  // TODO Plug-in change
  public int getLineNumber() {
  	return line;
  }
  
  public EOSEventImpl(EOSEvent.Importer importer) {
    super(importer);
    line = importer.provideLineNumber();
    file = importer.provideFilename();
  }
  
  public void export(EOSEvent.Exporter exporter) {
    super.export(exporter);
    exporter.addLineNumber(line);
    exporter.addFilename(file);
  }

  public final Object accept(Visitor visitor, Object arg) {
    return visitor.visit(this,arg);
  }

  @Override
  public String toString() {
  	// TODO Plug-in change
  	return this.getClass().getName() + "(" + paramString() + ", file=" + file + ", line=" + line + ")";
  }
}
