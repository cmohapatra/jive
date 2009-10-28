package edu.buffalo.cse.jive.internal.ui;

/**
 * Constants used internally by the JIVE user interface, possibly across many
 * classes. 
 * 
 * @author Jeffrey K Czyz
 */
public interface IInternalJiveUIConstants {
	
	/**
	 * The prefix of all internal constants. 
	 */
	public String INTERNAL_ID_PREFIX = "edu.buffalo.cse.jive.internal.ui";

	/**
	 * The folder ID of the <code>IFolderLayout</code> used to hold views
	 * related to the current launches.
	 */
	public String LAUNCH_FOLDER_ID = INTERNAL_ID_PREFIX + ".launchFolder";
	
	/**
	 * The folder ID of the <code>IFolderLayout</code> used to hold views for
	 * the console or other miscellaneous views.
	 */
	public String CONSOLE_FOLDER_ID = INTERNAL_ID_PREFIX + ".consoleFolder";
	
	/**
	 * The folder ID of the <code>IFolderLayout</code> used to hold views
	 * related to the execution state of the active target.
	 */
	public String EXECUTION_STATE_FOLDER_ID = INTERNAL_ID_PREFIX + ".executionStateFolder";
	
	/**
	 * The folder ID of the <code>IFolderLayout</code> used to hold views
	 * related to the execution history of the active target.
	 */
	public String EXECUTION_HISTORY_FOLDER_ID = INTERNAL_ID_PREFIX + ".executionHistoryFolder";
}
