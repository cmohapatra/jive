package edu.bsu.cs.jive.contour.java.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourFormat;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourMemberFormat;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.contour.Type;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.ContourMemberFormat.InnerClassFormat;
import edu.bsu.cs.jive.contour.ContourMemberFormat.MethodFormat;
import edu.bsu.cs.jive.contour.ContourMemberFormat.VariableFormat;
import edu.bsu.cs.jive.contour.impl.InnerClassDefinitionValue;
import edu.bsu.cs.jive.contour.impl.InnerClassMemberImpl;
import edu.bsu.cs.jive.contour.impl.MethodDefinitionValue;
import edu.bsu.cs.jive.contour.impl.MethodMemberImpl;
import edu.bsu.cs.jive.contour.impl.UninitializedValue;
import edu.bsu.cs.jive.contour.impl.VariableMemberImpl;
import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.bsu.cs.jive.contour.java.JavaContour;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.StaticContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Factory class for creating Java contours.
 * 
 * @author pvg
 */
public class JavaContourFactory {

	// TODO Plug-in change
	// This is a memory leak
//  public static JavaContourFactory instance(ContourModel cm) { 
//    JavaContourFactory cf = cache.get(cm);
//    if (cf==null) 
//      cache.put(cm, cf=new JavaContourFactory(cm));
//    return cf;
//  }
//  
//  private static Map<ContourModel,JavaContourFactory> cache =
//    new HashMap<ContourModel,JavaContourFactory>();
  
  private final ContourModel cm;
  
  // TODO Plug-in change
  public JavaContourFactory(ContourModel cm) {
    assert cm!=null;
    this.cm=cm;
  }
  
  /**
   * Create an instance contour
   * @param cf the contour format for the instance contour
   * @param id contour identifier
   * @param virtual indicates if this is a virtual instance
   * @return the new instance contour
   */
  public InstanceContour createInstanceContour(
      final ContourFormat cf,
      final ContourID id,
      final boolean virtual) {
    InstanceContour.Importer builder = new InstanceContour.Importer() {
      public ContourID provideID() {
        return id;
      }
      public List<ContourMember> provideMembers() {
        CFMembersAdapter adapter = new CFMembersAdapter();
        cf.export(adapter);
        return adapter.list();
      }
      public boolean provideVirtualProperty() {
        return virtual;
      }
      public ContourModel provideContainingModel() {
        return cm;
      }
    };
    return new InstanceContourImpl(builder);
  }
  
  /**
   * Create an instance contour from a contour creation record.
   * @param ccr contour creation record
   * @param virtual indicates if this is a virtual contour or not
   * @return the new contou 
   */
  public final InstanceContour createInstanceContour(
      ContourCreationRecord ccr,
      boolean virtual) {
    CCRMediator bridge = new CCRMediator();
    ccr.export(bridge);
    return bridge.getInstanceContour(virtual);
  }
  
  /**
   * Builds instance, static, and method contours 
   * out of contour creation records.
   * This class uses the other methods in 
   * {@link JavaContourFactory} to create the contours from the ccrs.
   * 
   * @author pvg
   */
  private class CCRMediator implements ContourCreationRecord.Exporter {

    ContourFormat cf;
    ContourID id;
    
    public void addContourFormat(ContourFormat cf) {
      this.cf=cf;
    }

    public void addContourID(ContourID id) {
      this.id=id;
    }
    
    public InstanceContour getInstanceContour(boolean virtual) {
      return createInstanceContour(cf,id,virtual);
    }
    
    public MethodContour getMethodContour() {
      return createMethodContour(cf,id, null);
    }
    
    public StaticContour getStaticContour() {
      return createStaticContour(cf,id);
    }
    
  }
  
  private class MethodCCRMediator extends CCRMediator implements
  MethodContourCreationRecord.Exporter {
    
    ThreadID thread;

    public void addThread(ThreadID thread) {
      this.thread=thread;
    }
  }
 
  
  /**
   * Create a new static contour.
   * @param cf the static contour's contour format
   * @param id contour identifier 
   * @return the new static contour
   */
  public final StaticContour createStaticContour(
      final ContourFormat cf,
      final ContourID id) {
    StaticContour.Importer builder = new StaticContour.Importer() {
      public ContourModel provideContainingModel() {
        return cm;
      }
      public ContourID provideID() {
        return id;
      }
      public List<ContourMember> provideMembers() {
        CFMembersAdapter adapter = new CFMembersAdapter();
        cf.export(adapter);
        return adapter.list();
      }
    };
    return new StaticContourImpl(builder);
  }
  
  /**
   * Create a static contour from a contour creation record.
   * @param ccr
   * @return a new static contour
   */
  public StaticContour createStaticContour(ContourCreationRecord ccr) {
    CCRMediator bridge = new CCRMediator();
    ccr.export(bridge);
    return createStaticContour(bridge.cf, bridge.id);
  }
  
  /**
   * Create a new method contor
   * @param cf the method contour's contour format
   * @param id the contour identifier for this contour
   * @param thread TODO
   * @return the new method contour
   */
  public MethodContour createMethodContour(
      final ContourFormat cf,
      final ContourID id, 
      final ThreadID thread) {
    MethodContour.Importer builder = new MethodContour.Importer() {
      public ContourModel provideContainingModel() {
        return cm;
      }
      public ContourID provideID() { return id; }
      public List<ContourMember> provideMembers() {
        CFMembersAdapter adapter = new CFMembersAdapter();
        cf.export(adapter);
        return adapter.list();
      }
      public ThreadID provideThread() { return thread; }
    };
    return new MethodContourImpl(builder);
  }
  
  /**
   * Create a new method contour
   * @param ccr
   * @return method contour that was created
   */
  public final MethodContour createMethodContour(MethodContourCreationRecord ccr) {
    MethodCCRMediator bridge = new MethodCCRMediator();
    ccr.export(bridge);
    return createMethodContour(bridge.cf, bridge.id, bridge.thread);
  }
}


/**
 * Abstract contour implementation.
 * 
 * @author pvg
 */
abstract class AbstractContour implements JavaContour {

  private final ContourID id;
  private final List<ContourMember> members = new ArrayList<ContourMember>();
  private final ContourModel cm;

  
  public AbstractContour(Contour.Importer builder) {
    members.addAll(builder.provideMembers());
    id = builder.provideID();
    cm = builder.provideContainingModel();
  }

  /**
   * Export the id and members of this object without calling
   * {@link Contour.Exporter#exportFinished()}.
   * @param exporter a reverse-builder
   */
  protected final void exportIDAndMembers(Contour.Exporter exporter) {
    exporter.addID(id);
    for (ContourMember m : members)
      exporter.addMember(m);
  }

  public ContourID id() {
    return id;
  }
  
  public ContourModel containingModel() { return cm; }
  
  @Override
  public String toString() {
  	StringBuffer sb = new StringBuffer(this.getClass().getName());
  	sb.append("{id=");
  	sb.append(id.toString());
  	sb.append(", members=[");
  	for (ContourMember m : members) {
  		sb.append(m.toString());
  		sb.append(",");
  	}
  	sb.append("]}");
  	return sb.toString();
  }
}

/**
 * Default implementation of an instance contour.
 * 
 * @author pvg
 */
final class InstanceContourImpl extends AbstractContour implements InstanceContour {

  private final boolean virtual;
  
  public InstanceContourImpl(InstanceContour.Importer builder) {
    super(builder);
    this.virtual = builder.provideVirtualProperty();
  }
  
  public Object accept(JavaContour.Visitor v, Object arg) {
    return v.visit(this,arg);
  }
  
  public boolean isVirtual() {
    return virtual;
  }
  
  // This shouldn't work, since InstanceContour.Exporter IS a 
  // Contour.Exporter, but the compiler doesn't seem to let me get through
  // without putting this implementation here.
  public void export(Contour.Exporter e) {
    super.exportIDAndMembers(e);
  }
  
  public void export(InstanceContour.Exporter e) {
    super.exportIDAndMembers(e);
    e.addVirtualProperty(virtual);
    e.exportFinished();
  }
}

/**
 * Default implementation of a static contour.
 * 
 * @author pvg
 */
final class StaticContourImpl extends AbstractContour implements StaticContour {

  public StaticContourImpl(StaticContour.Importer builder) {
    super(builder);
  }
  
  public Object accept(Visitor v, Object arg) {
    return v.visit(this,arg);
  }
  
  public void export(StaticContour.Exporter e) {
    super.exportIDAndMembers(e);
    e.exportFinished();
  }
  
}

/**
 * Default implementation of a method contour.
 * 
 * @author pvg
 */
final class MethodContourImpl extends AbstractContour implements MethodContour {

  private final ThreadID thread;
  
  public MethodContourImpl(MethodContour.Importer builder) {
    super(builder);
    this.thread = builder.provideThread();
    assert this.thread!=null;
  }
  
  public Object accept(Visitor v, Object arg) {
    return v.visit(this,arg);
  }
  
  // This shouldn't work, since InstanceContour.Exporter IS a 
  // Contour.Exporter, but the compiler doesn't seem to let me get through
  // without putting this implementation here.
  public void export(Contour.Exporter e) {
    super.exportIDAndMembers(e);
  }
  
  public void export(MethodContour.Exporter e) {
    super.exportIDAndMembers(e);
    e.addThread(thread);
    e.exportFinished();
  }
}

/**
 * Builds contour members from contour member format information.
 * The values assigned to the contour members are determined by
 * the {@link UninitializedValueFactory}.
 *
 * @author pvg
 */
class ContourMemberBuilder implements ContourMember.Importer {
  private String name;
  private Type type;
  private final ContourMemberFormat cmf;
  ContourMemberBuilder(ContourMemberFormat cmf) {
    this.cmf=cmf;
    cmf.export(new ContourMemberFormat.Exporter() {
      public void addName(String name) {
        ContourMemberBuilder.this.name=name;
      }
      public void addType(Type type) {
        ContourMemberBuilder.this.type=type;
      }
    });
  }
  public String provideName() {
    return name;
  }
  public Type provideType() {
    return type;
  }
  public Value provideValue() {
    return UninitializedValueFactory.instance().valueFor(cmf);
  }
}

/**
 * Builds variable members.
 * @author pvg
 */
class VariableContourMemberBuilder 
extends ContourMemberBuilder
implements ContourMember.Variable.Importer {
  private VariableID id;
  VariableContourMemberBuilder(ContourMemberFormat.VariableFormat vf) {
    super(vf);
    vf.export(new VariableFormat.Exporter() {
			public void addID(VariableID id) { 
				VariableContourMemberBuilder.this.id = id; 
			}
			public void addName(String name) {
				// This is handled by the superclass constructor.
			}
			public void addType(Type type) {
				// This is handled by the superclass constructor.
			}
    });
  }
  public VariableID provideID() {
  	assert id!=null;
  	return id;
  }
}


/**
 * Produces values that are appropriate for uninitialized variables
 * as well as inner class definitions and method definitions members.
 *
 * @see edu.bsu.cs.jive.contour.ContourMember.Variable
 * @see edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration
 * @see edu.bsu.cs.jive.contour.ContourMember.InnerClass
 * @see edu.bsu.cs.jive.contour.Value
 * @author pvg
 */
final class UninitializedValueFactory {
  private static final UninitializedValueFactory SINGLETON 
  = new UninitializedValueFactory();
  public static UninitializedValueFactory instance() {
    return SINGLETON;
  }
  private UninitializedValueFactory() {}
  
  /**
   * Given a contour member format, determine what type it should have
   * while it is uninitialized.
   * For variables, this will be an uninitialized value.
   * For methods and classes, this will be values that encapsulate the
   * corresponding definitions.
   * @param cmf
   * @return a value for the contour member corresponding to the contour
   *  member format
   * @see ContourMember
   * @see ContourMemberFormat
   */
  public Value valueFor(ContourMemberFormat cmf) {
    return (Value)cmf.accept(valueDeterminer, null);
  }
  
  /**
   * Workhorse visitor for {@link #valueFor(ContourMemberFormat)}.
   */
  private ContourMemberFormat.Visitor valueDeterminer =
    new ContourMemberFormat.Visitor() {

      public Object visit(InnerClassFormat c, Object arg) {
        return new InnerClassDefinitionValue();
      }

      public Object visit(MethodFormat m, Object arg) {
        return new MethodDefinitionValue();
      }

      public Object visit(VariableFormat v, Object arg) {
        // All variables are set to "uninitialized".
        return UninitializedValue.instance();
      }
    
  };
}

/**
 * Adapts a contour format's member formats into a list of contour members
 * that can be included in a contour, for example, through a builder.
 * 
 * @author pvg
 */
final class CFMembersAdapter implements ContourFormat.Exporter {
  /** The list of members, which will hold the result of the adaptation. */
  private final List<ContourMember> members
    = new ArrayList<ContourMember>();

  
  public void exportFinished() {
    // Nothing to do here.
  }
  
  public void provideMemberFormat(ContourMemberFormat mf) {
    members.add( (ContourMember)mf.accept(cmfVisitor, null) );
  }
  
  /**
   * Get the list of contour members created by this adapter.
   * @return list of members
   */
  public List<ContourMember> list() { return members; }
  
  /**
   * Converts contour member formats into contour members.
   * 
   * @see ContourMemberFormat
   * @see ContourMember
   */
  private ContourMemberFormat.Visitor cmfVisitor = new ContourMemberFormat.Visitor() {

    public Object visit(ContourMemberFormat.InnerClassFormat c, Object arg) {
      return new InnerClassMemberImpl(new ContourMemberBuilder(c));
    }

    public Object visit(ContourMemberFormat.MethodFormat m, Object arg) {
      return new MethodMemberImpl(new ContourMemberBuilder(m));
    }

    public Object visit(ContourMemberFormat.VariableFormat v, Object arg) {
      return new VariableMemberImpl(new VariableContourMemberBuilder(v));
    }

  };
}

