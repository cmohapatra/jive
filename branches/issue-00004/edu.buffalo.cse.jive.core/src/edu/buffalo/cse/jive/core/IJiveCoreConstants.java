package edu.buffalo.cse.jive.core;

import edu.buffalo.cse.jive.internal.core.JiveCorePlugin;

/**
 * Constants used by the JIVE core interface, possibly across many classes,
 * which may also be used by other plug-ins.
 * 
 * @author Jeffrey K Czyz
 */
public interface IJiveCoreConstants {
	
	/**
	 * The prefix of all non-internal constants.
	 */
	public static final String PLUGIN_ID_PREFIX = JiveCorePlugin.PLUGIN_ID;
	
	
	/**
	 * The attribute key used to obtain the class exclusion filters from the
	 * launch configuration.
	 */
	public static final String EXCLUSION_FILTERS_KEY = PLUGIN_ID_PREFIX + ".exclusionFilters";
	
}
