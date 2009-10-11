package edu.bsu.cs.jive.runtime;

import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * ThreadID importer based on JDI thread information.
 *
 * @author pvg
 */
public class ThreadIDBuilder implements ThreadID.Importer {

	private final ThreadReference ref;
	
	/**
	 * Create a new threadID builder.
	 * @param ref reference
	 */
	public ThreadIDBuilder(ThreadReference ref) {
		this.ref=ref;
	}

	public long provideId() {
		return ref.uniqueID();
	}

	public String provideName() {
		return ref.name();
	}
}
