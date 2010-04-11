package edu.bsu.cs.jive.runtime.builders;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;

import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.contour.Type;
import edu.bsu.cs.jive.util.TypeFactory;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Builder for variable member formats.
 * 
 * @author pvg
 */
class VariableMemberFormatBuilder implements
    ContourMemberFormat.VariableFormat.Importer {

  private final String name;

  private final Type type;
  
  private final VariableID id;

  /**
   * Create a variable member builder from a JDI field.
   * 
   * @param field
   * @throws ClassNotLoadedException
   *           if the class for the field's type is not loaded
   */
  public VariableMemberFormatBuilder(Field field)
      throws ClassNotLoadedException {
    this.name = field.name();
    
    // Using typeName() instead of type().name() 
    // prevents ClassNotLoadedExceptions.
    this.type = TypeFactory.instance().getType(field.typeName());
    
    // A field belongs to a class, and a class cannot have two variables
    // with the same name.  Hence, each variable will have a unique name.
    this.id = VariableIDFactory.instance().create(field);
  }
  
  /**
	 * Create a variable member builder from a JDI local variable.
	 * 
	 * @param v a variable within m
	 * @param m the method containing v
	 * @throws AbsentInformationException
	 *           if the method cannot be queried for info on its variables
	 */
	public VariableMemberFormatBuilder(LocalVariable v, Method m)
			throws AbsentInformationException {
		this.name = v.name();
		this.type = TypeFactory.instance().getType(v.typeName());
		this.id = VariableIDFactory.instance().create(v, m);
	}
	
 // TODO Plug-in change
	/**
	 * Creates a variable member builder from the given name and type.  This is
	 * neccessary for creating return point and dynamic link (rpdl) variables.
	 * 
	 * @param name the name of the variable
	 * @param type the type of the variable as a string
	 */
	public VariableMemberFormatBuilder(String name, String type) {
		this.name = name;
		this.type = TypeFactory.instance().getType(type);
		this.id = VariableIDFactory.instance().create(name);
	}

  public String provideName() {
    return name;
  }

  public Type provideType() {
    return type;
  }

  @Override
  public String toString() {
    return String.format("%s(name=\"%s\",type=%s)", this.getClass().getName(),
        name, type.toString());
  }

	public VariableID provideID() {
		return id;
	}
}
