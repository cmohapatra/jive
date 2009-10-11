package edu.bsu.cs.jive.util;

/**
 * Uniquely identifies a thread.
 * <p>
 * Each thread in Java has a unique identifier that never changes,
 * but a thread's name can change.
 * 
 * @author pvg
 */
public interface ThreadID {

	/**
	 * Get the unique identifier for this thread.
	 * This is the same identifier used by a {@link java.lang.Thread}.
	 * @return thread identification number
	 * @see Thread#getId()
	 */
	public long getId();
	
	/**
	 * Get the name for this thread.
	 * This name describes the thread, but it is not necessarily either
	 * unique or constant.
	 * @return thread name
	 * @see Thread#getName()
	 */
	public String getName();
	
	/**
	 * Write this thread id to the given exporter.
	 * @param exporter
	 */
	public void export(Exporter exporter);
	
	
	/**
	 * A builder for thread IDs.
	 * 
	 * @author pvg
	 */
	public interface Importer {
		public long provideId();
		public String provideName();
	}
	
	/**
	 * A reverse-builder for thread IDs.
	 * 
	 * @author pvg
	 */
	public interface Exporter {
		public void addId(long id);
		public void addName(String name);
	}
}
