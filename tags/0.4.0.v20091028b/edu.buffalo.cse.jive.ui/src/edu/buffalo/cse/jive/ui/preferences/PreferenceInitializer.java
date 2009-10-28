package edu.buffalo.cse.jive.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * Class used to initialize default preference values.
 * 
 * @author jkczyz
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * The delimiter used between event filters.
	 */
	public static final String EVENT_FILTER_DELIMITER = ",";
	
	/**
	 * The default event filters for local Java applications.
	 */
	private static final String[] JAVA_APPLICATION_FILTERS = {
		"java.*",
		"javax.*",
		"com.sun.*",
		"sun.*"
	};
	
	/**
	 * The default event filters for Java applets.
	 */
	private static final String[] JAVA_APPLET_FILTERS = {
		"java.*",
		"javax.*",
		"com.sun.*",
		"sun.*"
	};
	
	/**
	 * The default event filters for JUnit applications.
	 */
	private static final String[] JUNIT_APPLICATION_FILTERS = {
		"java.*",
		"javax.*",
		"com.sun.*",
		"sun.*",
		"org.junit.*",
		"org.junit.internal.*",
		"org.junit.runner.*",
		"org.junit.runners.*",
		"org.eclipse.jdt.internal.junit.*",
		"org.eclipse.jdt.internal.junit4.*",
		"$Proxy*"
	};
	
	/**
	 * Converts an array of filters into a single string which is saved as a
	 * preference.
	 * 
	 * @param filters an array of filters
	 * @return a single string containing the input filters
	 */
	public static String convertFilters(String[] filters) {
		String result = "";
		for (String filter : filters) {
			result += filter;
			result += EVENT_FILTER_DELIMITER;
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		
		prefs.setDefault(IJiveUIConstants.PREF_UPDATE_INTERVAL, 2500L);
		
		prefs.setDefault(IJiveUIConstants.PREF_CONTOUR_STATE, IJiveUIConstants.PREF_CONTOUR_STATE_STACK);
		prefs.setDefault(IJiveUIConstants.PREF_SHOW_MEMBER_TABLES, false);
		prefs.setDefault(IJiveUIConstants.PREF_SCROLL_LOCK, false);
		
		prefs.setDefault(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS, false);
		prefs.setDefault(IJiveUIConstants.PREF_EXPAND_LIFELINES, false);
		
		IPreferenceStore store = JiveUIPlugin.getDefault().getPreferenceStore();
		PreferenceConverter.setDefault(store, IJiveUIConstants.PREF_THREAD_COLOR_1, new RGB(166, 202, 240));
		PreferenceConverter.setDefault(store, IJiveUIConstants.PREF_THREAD_COLOR_2, ColorConstants.lightBlue.getRGB());
		PreferenceConverter.setDefault(store, IJiveUIConstants.PREF_THREAD_COLOR_3, ColorConstants.lightGreen.getRGB());
		PreferenceConverter.setDefault(store, IJiveUIConstants.PREF_THREAD_COLOR_4, ColorConstants.lightGray.getRGB());
		PreferenceConverter.setDefault(store, IJiveUIConstants.PREF_THREAD_COLOR_5, ColorConstants.orange.getRGB());
		
		prefs.setDefault(IJiveUIConstants.PREF_ACTIVATION_WIDTH, 7);
		prefs.setDefault(IJiveUIConstants.PREF_EVENT_HEIGHT, 4);
		
		initializeDefaultEventFilterPreferences(prefs);
	}

	/**
	 * Initializes the default event filter preferences.
	 * 
	 * @param prefs the preferences to initialize
	 */
	private void initializeDefaultEventFilterPreferences(Preferences prefs) {
		prefs.setDefault(IJiveUIConstants.PREF_JAVA_APPLICATION_FILTERS, convertFilters(JAVA_APPLICATION_FILTERS));
		prefs.setDefault(IJiveUIConstants.PREF_JAVA_APPLET_FILTERS, convertFilters(JAVA_APPLET_FILTERS));
		prefs.setDefault(IJiveUIConstants.PREF_JUNIT_APPLICATION_FILTERS, convertFilters(JUNIT_APPLICATION_FILTERS));
	}
}
