package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.contour.Type;

abstract class AbstractContourMemberFormatImpl 
implements ContourMemberFormat {


  private final String name;
  private final Type type;
  
  protected AbstractContourMemberFormatImpl(ContourMemberFormat.Importer builder) {
    this.name=builder.provideName();
    this.type=builder.provideType();
  }
  
  /**
   * Export this object.
   * @param exporter
   */
  public void export(ContourMemberFormat.Exporter exporter) {
    exporter.addName(name);
    exporter.addType(type);
  }
  
  public String name() {
    return name;
  }

  public Type type() {
    return type;
  }
  
  @Override public String toString() {
    return String.format("%s(name=%s,type=%s)",
        this.getClass().getName(),
        name(),
        type());
  }

}
