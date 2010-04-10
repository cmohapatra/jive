package edu.bsu.cs.jive.util;


/**
 * A factory class for contour identifiers.
 * 
 * @author pvg
 */
public class ContourIDFactory {
  
  private static final ContourIDFactory SINGLETON = new ContourIDFactory();
  
  public static ContourIDFactory instance() { return SINGLETON; }
  
  private ContourIDFactory() {}
  
  /**
   * Get an instance of a contour ID from the given builder.
   * @param builder
   * @return contour identifier
   */
  public ContourID create(ContourID.Importer builder) {
    // At some future juncture, this may be optimized with caching, for example.
    return new ContourIDImpl(builder);
  }

}

/**
 * An implementation of a contour identifier.
 * 
 * @author pvg
 */
class ContourIDImpl implements ContourID {

  /** Stringified identifier */
  private final String id;
  
  ContourIDImpl(ContourID.Importer builder) {
    this.id = builder.provideIDString();
  }
  
  public void export(Exporter exporter) {
    exporter.addIDString(id);
  }

  @Override
  public String toString() {
    assert id!=null;
    return id;
  }

  public int compareTo(ContourID o) {
    // Delegate to the string representations
    return id.compareTo(o.toString());
  }
  
  @Override
  public boolean equals(Object o) {
    try {
      ContourIDImpl cidi = (ContourIDImpl)o;
      return cidi.id.equals(this.id);
    } catch (ClassCastException cce) {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return HashUtils.hash(37, id); // I'm 37, I'm not *old*.
  }
}
