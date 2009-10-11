/* Created on Jan 24, 2005 */
package edu.bsu.cs.jive.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for thread identifiers.
 * The default thread factory caches thread objects for reuse,
 * using their immutable identification numbers as keys.
 * 
 * @author Paul Gestwicki (pvg@cse.buffalo.edu)
 */
public final class ThreadIDFactory {

	private static final ThreadIDFactory SINGLETON = new ThreadIDFactory();
	
  /**
   * Maps thread identifiers to thread objects.
   */
  private volatile Map<Long,ThreadID> cache = new HashMap<Long,ThreadID>();
  
  /** Protected constructor */
  private ThreadIDFactory() {}
  
  public static ThreadIDFactory instance() {
  	return SINGLETON;
  }
  
  /**
   * Get a threadID object.
   * Since thread objects are immutable, a cache may be used rather than
   * allocating a new object.
   * @param id the thread identifier (immutable)
   * @param name a label for the thread (mutable)
   * @return thread identification object
   */
  public ThreadID create(final long id, final String name) {
    Long key = Long.valueOf(id);
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    else {
      ThreadID thread = new DefaultThreadID(id, name);
      cache.put(key,thread);
      return thread;
    }
  }
  
  /**
   * Get a threadID object.
   * If one does not exist, it is created.
   * @see #create(long, String)
   * @param importer
   * @return thread identification object
   */
  public final ThreadID create(ThreadID.Importer importer) {
  	return create(importer.provideId(), importer.provideName());
  }
  
}

final class DefaultThreadID implements ThreadID {

	private final long id;
	private final String name;
	
	public DefaultThreadID(long id, String name) {
		this.id=id;
		this.name=name;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void export(Exporter exporter) {
		exporter.addId(getId());
		exporter.addName(getName());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ThreadID) {
			return ((ThreadID)o).getId() == getId();
		}
		else return false;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}
	
	@Override
	public String toString() {
		return DefaultThreadID.class.getName() + "(id=" + id + ",name=" + name + ")";
	}
}

