package edu.bsu.cs.jive.runtime.builders;

import java.util.List;
import java.util.WeakHashMap;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;

import edu.bsu.cs.jive.util.HashUtils;
import edu.bsu.cs.jive.util.VariableID;

public class VariableIDFactory {

	private static final VariableIDFactory SINGLE = new VariableIDFactory();
	
	public static VariableIDFactory instance() { return SINGLE; }
	
	private VariableIDFactory() {}
	
	private WeakHashMap<String,NamedVariableID> nameMap
	  = new WeakHashMap<String,NamedVariableID>();
	
	/**
	 * Create a variable ID for the given field.
	 * This may reuse equivalent variable IDs, or it may return new equivalent
	 * objects.
	 * 
	 * @param f a JDI field
	 * @return a variable ID
	 */
	public VariableID create(Field f) {
		// A field belongs to a class, in which variable names are necessarily
		// unique.
		return getNamedVariableID(f.name());
	}
	
	/**
	 * Create a variable ID for the given local variable.
	 * This may return a new instance of an equivalent existing instance.
	 * @param v local variable
	 * @param m the method in which v exists.
	 * @return a variable ID
	 * @throws AbsentInformationException if the method cannot be queried for
	 *  its list of locals
	 */
	public VariableID create(LocalVariable v, Method m)
		throws AbsentInformationException{
		String name = v.name();
		List<LocalVariable> locals = m.variablesByName(name);
		
		if (!locals.contains(v))
			throw new IllegalArgumentException("Variable " + v + " is not in " + m);
		
		// It's possible to have multiple local variables with the same name
		// within a method, if they are in separate scopes.
		// Hence, we may need to append something to the name.
		assert locals.size() > 0;
		if (locals.size()>1) 
			name += "(" + locals.indexOf(v) + ")";
		
		return getNamedVariableID(name);
	}
	
	// TODO Plug-in change
	/**
	 * Creates a variable ID with the given name.  This method is neccessary for
	 * creating return point and dynamic link (rpdl) variables.
	 * 
	 * @param name the name of the variable ID
	 * @return a variable ID
	 */
	public VariableID create(String name) {
		return getNamedVariableID(name);
	}
	
	private synchronized VariableID getNamedVariableID(String name) {
		if (nameMap.containsKey(name))
			return nameMap.get(name);
		else {
			NamedVariableID id = new NamedVariableID(name);
			nameMap.put(name, id);
			return id;
		}
	}
	
}

class NamedVariableID implements VariableID {
  private final String name;
  
  public NamedVariableID(String name) {
    this.name = name;
  }
  
  @Override
  public int hashCode() {
    int result = 111; // That's a nice seed.
    result = HashUtils.hash(result, name);
    return result;
  }
  
  @Override
  public boolean equals(Object o) {
    try {
      VariableID v = (VariableID)o;
      // TODO Plug-in change
      return this.toString().equals(v.toString());
    } catch (ClassCastException cce) {
      return false;
    }
  }
  
  @Override
  public String toString() {
    return name;
  }
}