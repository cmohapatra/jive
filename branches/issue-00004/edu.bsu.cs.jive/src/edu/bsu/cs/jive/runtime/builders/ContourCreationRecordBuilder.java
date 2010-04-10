package edu.bsu.cs.jive.runtime.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.contour.impl.VariableMemberFormatImpl;
import edu.bsu.cs.jive.util.ContourID;

/**
 * Builder for contour creation records.
 * 
 * @see edu.bsu.cs.jive.contour.ContourCreationRecord
 * @author pvg
 */
public class ContourCreationRecordBuilder implements ContourCreationRecord.Importer {

  private final ContourID id;

  private final List<ContourMemberFormat> memberFormats;

  /**
   * Create a new builder for contour creation records. This can be used for
   * static and instance contours based on the parameters.
   * 
   * @param id the contour's identifier
   * @param type the type represented by the contour
   * @param isStatic whether the contour is static
   * @param isInModel whether the contour is in-model
   * @return contour creation record builder
   */
  public static ContourCreationRecordBuilder create(ContourID id,
      ReferenceType type, boolean isStatic, boolean isInModel) {
    // TODO: optimize this by caching contour formats
    return new ContourCreationRecordBuilder(id, type, isStatic, isInModel);
  }

  /**
   * Create a new builder for contour creation records. This can be used for
   * static and instance contours based on the parameters.
   * 
   * @param id the contour's identifier
   * @param type the type represented by the contour
   * @param isStatic whether the contour is static
   * @return contour creation record builder
   */
  public static ContourCreationRecordBuilder create(ContourID id,
      ReferenceType type, boolean isStatic) {
    // TODO: optimize this by caching contour formats
    return new ContourCreationRecordBuilder(id, type, isStatic, true);
  }
  
  private ContourCreationRecordBuilder(ContourID id, ReferenceType type,
      boolean isStatic, boolean isInModel) {
    this.id = id;

    // Initialize the member format list
    memberFormats = createMemberFormats(type, isStatic, isInModel);
  }

  private final List<ContourMemberFormat> createMemberFormats(
      ReferenceType type, boolean isStatic, boolean isInModel) {
  	if (!isInModel) {
  		return Collections.emptyList();
  	}

    //
    // Get all the variable members
    //
  	List<ContourMemberFormat> result = new ArrayList<ContourMemberFormat>();
    // TODO Plug-in change
//    for (Field field : type.fields()) {
    for (Object o : type.fields()) {
    	Field field = (Field) o;
      if (field.isStatic() == isStatic) {
        try {
          result.add(new VariableMemberFormatImpl(
              new VariableMemberFormatBuilder(field)));
        } catch (ClassNotLoadedException cnle) {
          // This is a critical error
          throw new IllegalStateException(cnle);
        }
      }
    }

    // Get the method declarations

    return result;
  }

  public String provideIDString() {
    assert id != null;
    return id.toString();
  }

  public List<ContourMemberFormat> provideMemberFormats() {
    return memberFormats;
  }

}
