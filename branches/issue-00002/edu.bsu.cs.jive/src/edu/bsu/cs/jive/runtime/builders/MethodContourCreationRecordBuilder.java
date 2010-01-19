package edu.bsu.cs.jive.runtime.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;

import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.contour.impl.VariableMemberFormatImpl;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;

public class MethodContourCreationRecordBuilder implements MethodContourCreationRecord.Importer {

  public static MethodContourCreationRecordBuilder create(
      ContourID contourID, Method method, ThreadID t) {
    return new MethodContourCreationRecordBuilder(contourID, method, t);
  }

  private final ContourID id;
  
  private final ThreadID threadID;
  
  private final List<ContourMemberFormat> memberFormats;
  
  private MethodContourCreationRecordBuilder(ContourID id, Method method, ThreadID threadID) {
    this.id = id;
    this.threadID = threadID;
    memberFormats = createMemberFormats(method);
  }

  public String provideIDString() {
    assert id!=null;
    return id.toString();
  }

  public List<ContourMemberFormat> provideMemberFormats() {
    return memberFormats;
  }

  public ThreadID provideThread() {
    return threadID;
  }

  private final List<ContourMemberFormat> createMemberFormats(Method method) {
    List<ContourMemberFormat> result = new ArrayList<ContourMemberFormat>();
  
    try {
      // TODO Plug-in change
      for (LocalVariable v : getLocalVariables(method)) {
        result.add(new VariableMemberFormatImpl(new VariableMemberFormatBuilder(v, method)));
      }
    } catch (AbsentInformationException e) {
    	// TODO Plug-in change
    	// Do nothing since debug information is not available.  This may occur
  		// while monitoring code not compiled with the debug option (javac -g).
    }

    // TODO Plug-in change
    result.add(new VariableMemberFormatImpl(new VariableMemberFormatBuilder("", "rpdl")));
    return result;
  }
  
  // TODO Plug-in change
  @SuppressWarnings("unchecked")
	private List<LocalVariable> getLocalVariables(Method method) throws AbsentInformationException {
  	try {
  		return method.variables();
  	}
  	catch (ArrayIndexOutOfBoundsException e1) {
  		try {
  			return method.variables();
  		}
  		catch (ArrayIndexOutOfBoundsException e2) {
  			return Collections.emptyList();
  		}
  	}
  }

}
