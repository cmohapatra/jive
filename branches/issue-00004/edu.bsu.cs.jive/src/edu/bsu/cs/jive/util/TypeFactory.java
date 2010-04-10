package edu.bsu.cs.jive.util;

import java.util.HashMap;
import java.util.Map;

import edu.bsu.cs.jive.contour.Type;

/**
 * A factory for the Type interface.
 * @see edu.bsu.cs.jive.contour.Type
 * @author pvg
 */
public final class TypeFactory {

  private static final TypeFactory SINGLETON = new TypeFactory();
  
  /**
   * Get an instance of the type factory.
   * @return the type factory
   */
  public static TypeFactory instance() { return SINGLETON; }
  
  private Map<String,Type> map = new HashMap<String,Type>();
  
  /**
   * Get a type object for the given text description
   * @param typeName (e.g. int, MyClass, java.lang.Object)
   * @return type object wrapper
   */
  public Type getType(String typeName) {
    // This is not synchronized since having two type objects for the
    // same logical type is not the end of the world.
    // Comparison should be through equals() anyway.
    Type result = map.get(typeName);
    if (result==null)
      map.put(typeName, result = new SimpleType(typeName));
    return result;
  }
}

final class SimpleType implements Type {

  private final String name;
  
  public SimpleType(String name) {
    this.name=name;
  }
  
  public String getName() {
    return name;
  }
  
  @Override public int hashCode() {
    return name.hashCode();
  }
  
  @Override public boolean equals(Object o) {
    if (o instanceof SimpleType) {
      return ((SimpleType)o).name.equals(this.name);
    }
    else return false;
  }
  
  @Override public String toString() {
    return name;
  }
}
