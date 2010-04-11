package edu.bsu.cs.jive.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.runtime.builders.VariableIDFactory;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ContourIDFactory;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.ThreadIDFactory;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Default implementation of a contour manager.
 * 
 * @author pvg
 */
public class ContourUtilsImpl implements ContourUtils {

	private static final long STARTING_EVENT_NUMBER = 1;

	/** The starting (lowest) instance count */
	private static final long STARTING_INSTANCE_COUNT = 1;
	
	/** The starting (lowest) method activation count */
	private static final long STARTING_ACTIVATION_COUNT = 1;

	private long eventNumber = STARTING_EVENT_NUMBER;

	/**
	 * Delegate for thread stacks operations
	 */
	private ThreadStacks threadStacks = new ThreadStacks();

	/**
	 * Maps names of classes to their static contours' identifiers. Each class
	 * name should point to a unique static contour (as long as the class name is
	 * not for a non-static inner class).
	 */
	private final Map<String, ContourID> staticMap = new HashMap<String, ContourID>();

	/**
	 * Maps thread reference unique identifiers to thread identifier objects.
	 * 
	 * @see ThreadReference#uniqueID()
	 */
	private final Map<Long, ThreadID> threadMap = new HashMap<Long, ThreadID>();
	
	public boolean staticContourExistsFor(String clazz) {
		return staticMap.containsKey(clazz);
	}

	public long nextEventNumber() {
		return eventNumber++;
	}

	public ThreadID threadID(ThreadReference ref) {
		ThreadID result = threadMap.get(ref.uniqueID());
		if (result == null) {
			result = ThreadIDFactory.instance().create(new ThreadIDBuilder(ref));
			assert result != null;
			threadMap.put(ref.uniqueID(), result);
		}
		return result;
	}

	public ContourID getStaticContourID(String clazz) {
		if (!staticMap.containsKey(clazz))
			throw new IllegalArgumentException("No static contour ID exists for "
					+ clazz);
		else
			return staticMap.get(clazz);
	}

	public ContourID createStaticContourID(final String name) {
		if (staticContourExistsFor(name))
			throw new IllegalArgumentException(String.format(
					"A contour ID already exists for %s; it\'s %s", name,
					getStaticContourID(name)));

		else {
			ContourID result = ContourIDFactory.instance().create(
					new ContourID.Importer() {

						public String provideIDString() {
							// The identifier for a static contour is its own class' name
							// by default.
							return name;
						}

					});
			assert !staticMap.containsValue(result) : "Duplicate entry in staticMap!";

			staticMap.put(name, result);
			return result;
		}
	}

	private Map<ClassNameAndObjectID, ContourID> instanceMap = new TreeMap<ClassNameAndObjectID, ContourID>();

	/** Maps class names to their instance counts */
	private Map<String, Long> instanceCountMap = new TreeMap<String, Long>();

	public List<ContourID> createInstanceContourIDs(ObjectReference object) {
		final List<ContourID> result = new ArrayList<ContourID>();
		final long objectID = object.uniqueID();
		ClassType type = (ClassType) object.referenceType();

		while (type != null) {
			final String clazzName = type.name();
			final long instanceCount = instanceCount(clazzName);
			ClassNameAndObjectID key = ClassNameAndObjectID.create(clazzName,
					objectID);
			ContourID contourID = ContourIDFactory.instance().create(
					new ContourID.Importer() {
						private final String idString = clazzName + ":" + instanceCount;

						public String provideIDString() {
							return idString;
						}
					});

			// Update data model
			instanceMap.put(key, contourID);
			
			// Update result list
			result.add(contourID);

			type = type.superclass();
		}

		return result;
	}

	/**
	 * Get the next available instance count for the named class. Subsequent calls
	 * return increasing values.
	 * 
	 * @param className
	 * @return instance count
	 */
	private final long instanceCount(String className) {
		Long lastCount = instanceCountMap.get(className);
		long count;
		if (lastCount == null)
			count = STARTING_INSTANCE_COUNT;
		else
			count = lastCount + 1;

		instanceCountMap.put(className, count);
		return count;
	}

	public ContourID getInstanceContourID(ObjectReference object,
			String clazz) {
		assert object!=null;
		assert clazz!=null;
		assert isSuperclass(object, clazz)
		: String.format("%s is not the class or a superclass of %s, whose class is %s",
				clazz, object, object.type());

		ClassNameAndObjectID key = ClassNameAndObjectID.create(clazz, object
				.uniqueID());
		ContourID result = instanceMap.get(key);
		if (result == null)
			throw new IllegalArgumentException("Object has no contour ID!");
		else
			return result;
	}

	public ContourID getInstanceContourID(ObjectReference object) {
		return getInstanceContourID(object, object.referenceType().name());
	}

	public boolean instanceContourExistsFor(ObjectReference object) {
		
		// Arrays are currently ignored.
		// TODO: handle arrays
		if (object instanceof ArrayReference) {
			return false;
		}
		
		long id = object.uniqueID();
		ClassType type = (ClassType) object.referenceType();
		while (type != null) {
			ClassNameAndObjectID key = ClassNameAndObjectID.create(type.name(), id);
			ContourID contourID = instanceMap.get(key);
			if (contourID != null)
				return true;
			else
				type = type.superclass();
		}
		return false; // Couldn't find it
	}

	private final boolean isSuperclass(ObjectReference object, String clazz) {
		assert object != null;
		assert clazz != null;

		ClassType type = (ClassType) object.referenceType();
		while (type != null) {
			if (type.name().equals(clazz))
				return true;
			type = type.superclass();
		}

		return false; // clazz is not the name of a class/superclass of the object
	}

	public Stack<ContourID> methodStack(ThreadID threadID) {
		// delegate to thread stacks
		return threadStacks.methodStack(threadID);
	}

	public ContourID peekStack(ThreadID threadID) throws NoStackException {
		// delegate to thread stacks
		return threadStacks.peekStack(threadID);
	}

	public ContourID popStack(ThreadID threadID) throws NoStackException {
		// delegate to thread stacks
		return threadStacks.popStack(threadID);
	}

	public void pushStack(ContourID contourID, ThreadID threadID) {
		// delegate to thread stacks
		threadStacks.pushStack(contourID, threadID);
	}
	
	// TODO Plug-in change
	public int stackSize(ThreadID threadID) throws NoStackException {
		return threadStacks.methodStack(threadID).size();
	}

	// TODO Plug-in change
	public ContourID createMethodContourID(final Method method, final ContourID context)  {
    try {
      return ContourIDFactory.instance().create(
          new ContourID.Importer() {
          	// TODO Plug-in change
            private final String s = methodManager.generateContourIDString(method, context);
            public String provideIDString() {
              return s;
            }
          });
    } catch (IncompatibleThreadStateException itse) {
      itse.printStackTrace();
      throw new IllegalStateException(itse);
    }
	}
	
	private final MethodInvocationCounter methodManager = new MethodInvocationCounter();
	
	private final class MethodInvocationCounter {
		private final Map<String,Long> map = new TreeMap<String,Long>();
		
		// TODO Plug-in change
		/**
		 * Create the key for a method entry event
		 * @param method
		 * @return key
		 */
		private String generateKey(final Method method) {
			// TODO Plug-in change
			return method.declaringType().name()  + "#" + method.name();
		}
		
		// TODO Plug-in change
		public String generateContourIDString(Method method, ContourID context) 
				throws IncompatibleThreadStateException {
			// TODO Plug-in change
			String key = generateKey(method);
			Long prevCount = map.get(key);
			Long count;
			if (prevCount==null) 
				map.put(key, count = new Long(STARTING_ACTIVATION_COUNT));
			else {
				count = prevCount+1;
				map.put(key, count); // update the count
			}
			
//      String baseName;
//      ObjectReference thisObj = e.thread().frame(0).thisObject();
//      if (thisObj==null)
//        baseName = e.method().declaringType().name();
//      else
//        baseName = getInstanceContourID(thisObj).toString();
      
			// TODO Plug-in change
			return context + "#" + method.name() + ":" + count;
		}
	}

	/* These has been moved to VariableIDFactory -- see below
	public VariableID getVariableID(final Field f, final ObjectReference object) {
		assert f != null;
		// object may be null; it means that the field must be static.
		assert f.isStatic() || object!=null;
		
		return new SimpleVariableID( String.format("%s#%s", object == null 
            ?
            getStaticContourID(f.declaringType().name()).toString()
            :
            getInstanceContourID(object, f.declaringType().name()),
            f.name()));
	
	}
  public VariableID getVariableID(final LocalVariable v, 
      final ObjectReference object, final ContourID methodContourID) {
    assert v!=null;
    return new SimpleVariableID(methodContourID.toString() + "#" + v.name()); 
  }
  */
	public VariableID getVariableID(final Field f, final ObjectReference object) {
		return VariableIDFactory.instance().create(f);
	}
  
}

/**
 * Wraps up a class name along with a unique object identifier.
 * 
 * @author pvg
 */
class ClassNameAndObjectID implements Comparable<ClassNameAndObjectID> {

	public static ClassNameAndObjectID create(String className, long objectID) {
		return new ClassNameAndObjectID(className, objectID);
	}

	final String className;

	final long objectID;

	private ClassNameAndObjectID(String className, long objectID) {
		this.className = className;
		this.objectID = objectID;
	}

	public int compareTo(ClassNameAndObjectID o) {
		int result = (int) (this.objectID - o.objectID);
		if (result == 0)
			return this.className.compareTo(o.className);
		else
			return result;
	}

	@Override
	public boolean equals(Object o) {
		try {
			ClassNameAndObjectID other = (ClassNameAndObjectID) o;
			return other.objectID == this.objectID
					&& other.className.equals(this.className);
		} catch (ClassCastException cce) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[" + objectID + "/" + className + "]";
	}
}