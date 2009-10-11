package edu.bsu.cs.jive.contour.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.bsu.cs.jive.contour.ContourFormat;
import edu.bsu.cs.jive.contour.ContourMemberFormat;

/**
 * Default implementation of a contour format.
 * 
 * @author pvg
 */
public class ContourFormatImpl implements ContourFormat {

  public static ContourFormat create(Importer builder) {
    return new ContourFormatImpl(builder);
  }
  
  /**
   * The member formats for this contour format.
   */
  private final List<ContourMemberFormat> memberFormats;
  
  /**
   * Create a contour format from the given builder.
   * @param builder
   */
  private ContourFormatImpl(Importer builder) {
    this.memberFormats 
      = new ArrayList<ContourMemberFormat>(builder.provideMemberFormats());
  }
  
  public List<ContourMemberFormat> memberFormats() {
    return Collections.unmodifiableList(memberFormats);
  }

  public void export(Exporter exporter) {
    for (ContourMemberFormat mf : memberFormats) {
      exporter.provideMemberFormat(mf);
    }
    exporter.exportFinished();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(getClass().getName()+"[members={");
    Iterator<ContourMemberFormat> iterator = memberFormats.iterator();
    while (iterator.hasNext()) {
      ContourMemberFormat member = iterator.next();
      sb.append(member);
      if (iterator.hasNext()) sb.append(",");
    }
    return sb.append("}]").toString();
  }
}
