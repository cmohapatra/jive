package edu.bsu.cs.jive.events;

/**
 * An end-of-statement event.
 * Such events are produced when a statement is completed.
 * EOS events do not necessarily have to be recorded, since there are
 * generally a lot of them.
 *
 * @author pvg
 */
public interface EOSEvent extends Event {

	// TODO Plug-in change
	/**
	 * Get the identifying string of the file in which the event occurred.
	 * This is encoded as per a Java codebase, e.g. 
	 * &quot;my/package/Test.java.&quot;
	 * @return file identifier
	 */
	public String getFilename();
	
	// TODO Plug-in change
	/**
	 * Get the line number that just finished.
	 * @return line number of the file
	 * @see #getFilename()
	 */
	public int getLineNumber();
	
	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(EOSEvent.Exporter exporter);
	
	/**
	 * Importer (builder) for an EOS event.
	 *
	 * @author pvg
	 */
	public interface Importer extends Event.Importer {
		
    /**
     * Provides the fully-qualified filename in which the line was executed.
     * 
     * @return the filename
     */
    public String provideFilename();
		
    /**
     * Provides the line number that was executed.
     * 
     * @return the line number
     */
    public int provideLineNumber();
	}
	
	/**
	 * Exporter (reverse-builder) for an EOS event.
	 * @author pvg
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the filename in which the line was executed.  This is called by
		 * {@code EOSEvent#export(Exporter)}
		 * 
		 * @param filename the filename
		 */
		public void addFilename(String filename);
		
		/**
		 * Adds the line number that was executed.  This is called by
		 * {@code EOSEvent#export(Exporter)}
		 * 
		 * @param lineNumber the line number
		 */
		public void addLineNumber(int lineNumber);
	}
}
