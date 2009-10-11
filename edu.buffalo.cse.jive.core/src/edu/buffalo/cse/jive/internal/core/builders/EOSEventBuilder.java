package edu.buffalo.cse.jive.internal.core.builders;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class EOSEventBuilder extends AbstractEventBuilder implements EOSEvent.Importer {

	private String filename;
	
	private int lineNumber;
	
	public static EOSEventBuilder create(ThreadReference thread, Location location, ContourUtils contourManager, StackManager stackManager) throws AbsentInformationException {
		ThreadID threadID = stackManager.threadID(thread);
		String filename = determineFilename(location);
		int lineNumber = determineLineNumber(location);
		
		return new EOSEventBuilder(threadID, filename, lineNumber, contourManager);
	}
	
	private static String determineFilename(Location location) throws AbsentInformationException {
		return location.sourcePath();
	}
	
	private static int determineLineNumber(Location location) {
		return location.lineNumber();
	}
	
	private EOSEventBuilder(ThreadID thread, String filename, int lineNumber, ContourUtils contourManager) {
		super(thread, contourManager);
		this.filename = filename;
		this.lineNumber = lineNumber;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EOSEvent.Importer#provideFilename()
	 */
	public String provideFilename() {
		return filename;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.EOSEvent.Importer#provideLineNumber()
	 */
	public int provideLineNumber() {
		return lineNumber;
	}
}
