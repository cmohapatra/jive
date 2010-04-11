package edu.buffalo.cse.jive.internal.core.factories;

import java.util.HashMap;
import java.util.Map;

import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * Factory class for creating JIVE thread identifiers.
 * 
 * @author Jeffrey K Czyz
 */
public class ThreadIDFactory {

	/**
	 * Maps a JDI identifier of a {@code ThreadReference} to a JIVE thread
	 * identifier.
	 */
	private Map<Long, ThreadID> idCache = new HashMap<Long, ThreadID>();
	
	/**
	 * Creates a JIVE thread identifier from a JDI thread reference.  This
	 * method may return a cached identifier to avoid object creation.
	 * 
	 * @param thread a JDI thread identifier
	 * @return a JIVE thread identifier
	 */
	public ThreadID create(ThreadReference thread) {
		long id = thread.uniqueID();  // JDI identifier
		ThreadID result = idCache.get(id);  // JIVE identifier
		
		if (result == null) {
			result = new ThreadIDImpl(thread);
			idCache.put(id, result);
		}
		
		return result;
	}
}

/**
 * An implementation of a {@code ThreadID}.
 * 
 * @author jkczyz
 */
class ThreadIDImpl implements ThreadID {
	
	/**
	 * The unique JDI identifier for the thread.
	 */
	private long id;
	
	/**
	 * The name of the thread as given at creation time.
	 */
	private String name;
	
	/**
	 * Creates a thread identifier from the supplied {@code ThreadReference}.
	 * 
	 * @param thread a thread reference to model
	 */
	public ThreadIDImpl(ThreadReference thread) {
		id = thread.uniqueID();
		name = thread.name();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.util.ThreadID#export(edu.bsu.cs.jive.util.ThreadID.Exporter)
	 */
	public void export(Exporter exporter) {
		exporter.addId(id);
		exporter.addName(name);
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.util.ThreadID#getId()
	 */
	public long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.util.ThreadID#getName()
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if ((other == null) || (other.getClass() != this.getClass())) {
			return false;
		}
		else {
			return id == ((ThreadIDImpl) other).id;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + "(id = " + id + ")";
	}
}
