package edu.buffalo.cse.jive.ui;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

/**
 * Constants used by the JIVE user interface, possibly across many classes,
 * which may also be used by other plug-ins.
 * 
 * @author Jeffrey K Czyz
 */
public interface IJiveUIConstants {

	/*
	 * Constants for Eclipse identifiers
	 */
	
	/**
	 * The prefix of all non-internal constants.
	 */
	public static final String PLUGIN_ID_PREFIX = JiveUIPlugin.PLUGIN_ID;
	
	/**
	 * The perspective ID for the JIVE perspective.
	 */
	public static final String JIVE_PERSPECTIVE_ID = PLUGIN_ID_PREFIX + ".jivePerspective";
	
	/**
	 * The tab ID for the JIVE launch configuration tab.
	 */
	public static final String JIVE_TAB_ID = PLUGIN_ID_PREFIX + ".jiveTab";
	
	/**
	 * The category ID for the JIVE views category.
	 */
	public static final String JIVE_CATEGORY_ID = PLUGIN_ID_PREFIX + ".jiveCategory";
	
	/**
	 * The view ID for the Event Log view.
	 */
	public static final String EVENT_LOG_VIEW_ID = PLUGIN_ID_PREFIX + ".eventLogView";
	
	/**
	 * The view ID for the Contour Model view.
	 */
	public static final String CONTOUR_MODEL_VIEW_ID = PLUGIN_ID_PREFIX + ".contourModelView";
	
	/**
	 *  The view ID for the Contour Diagram view.
	 */
	public static final String CONTOUR_DIAGRAM_VIEW_ID = PLUGIN_ID_PREFIX + ".contourDiagramView";
	
	/**
	 * The view ID for the Sequence Model view.
	 */
	public static final String SEQUENCE_MODEL_VIEW_ID = PLUGIN_ID_PREFIX + ".sequenceModelView";
	
	/**
	 *  The view ID for the Sequence Diagram view.
	 */
	public static final String SEQUENCE_DIAGRAM_VIEW_ID = PLUGIN_ID_PREFIX + ".sequenceDiagramView";
	
	/**
	 * The fully-qualified ID for the JIVE Search Pages extension point.   
	 */
	public static final String SEARCH_PAGES_EXTENSION_POINT_ID = PLUGIN_ID_PREFIX + ".searchPages";
	
	/*
	 * Constants for icon keys and paths
	 */
	
	/**
	 * The root path of all icons used in the plug-in. 
	 */
	public static final String ICONS_PATH = "icons/";
	
	/**
	 * The path segment used for enabled icons.
	 */
	public static final String ENABLED_ICONS = "enabled/";
	
	/**
	 * The path segment used for disabled icons.
	 */
	public static final String DISABLED_ICONS = "disabled/";
	
	/**
	 * The path segment used for enabled action icons.
	 */
	public static final String ENABLED_ACTION_ICONS = ENABLED_ICONS + "actions/";
	
	/**
	 * The path segment used for disabled action icons.
	 */
	public static final String DISABLED_ACTION_ICONS = DISABLED_ICONS + "actions/";
	
	/**
	 * The root path for general purpose icons.
	 */
	public static final String GENERAL_ICONS_PATH = ICONS_PATH + "general/";
	
	/**
	 * The root path for search icons.
	 */
	public static final String SEARCH_ICONS_PATH = ICONS_PATH + "search/";
	
	/**
	 * The root path for event log icons.
	 */
	public static final String EVENT_LOG_ICONS_PATH = ICONS_PATH + "event_log/";
	
	/**
	 * The root path for event icons.
	 */
	public static final String EVENT_ICONS_PATH = EVENT_LOG_ICONS_PATH + ENABLED_ICONS + "events/";
	
	/**
	 * The root path for contour model icons.
	 */
	public static final String CONTOUR_MODEL_ICONS_PATH = ICONS_PATH + "contour_model/";
	
	/**
	 * The root path for contour icons.
	 */
	public static final String CONTOUR_ICONS_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ICONS + "contours/";
	
	/**
	 * The root path for sequence model icons.
	 */
	public static final String SEQUENCE_MODEL_ICONS_PATH = ICONS_PATH + "sequence_model/";
	
	/**
	 * The root path for execution icons.
	 */
	public static final String EXECUTION_ICONS_PATH = SEQUENCE_MODEL_ICONS_PATH + ENABLED_ICONS + "executions/";
	
	/**
	 * The key of the enabled JIVE icon.
	 */
	public static final String ENABLED_JIVE_ICON_KEY = "ENABLED_JIVE_ICON";
	
	/**
	 * The path of the enabled JIVE icon.
	 */
	public static final String ENABLED_JIVE_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "jive.gif";
	
	/**
	 * The key of the enabled remove icon.
	 */
	public static final String ENABLED_REMOVE_ICON_KEY = "ENABLED_REMOVE_ICON";
	
	/**
	 * The path of the enabled remove icon.
	 */
	public static final String ENABLED_REMOVE_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "remove.gif";
	
	/**
	 * The key of the disabled remove icon.
	 */
	public static final String DISABLED_REMOVE_ICON_KEY = "DISABLED_REMOVE_ICON";
	
	/**
	 * The path of the disabled remove icon.
	 */
	public static final String DISABLED_REMOVE_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "remove.gif";
	
	/**
	 * The key of the enabled remove all icon.
	 */
	public static final String ENABLED_REMOVE_ALL_ICON_KEY = "ENABLED_REMOVE_ALL_ICON";
	
	/**
	 * The path of the enabled remove all icon.
	 */
	public static final String ENABLED_REMOVE_ALL_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "remove_all.gif";
	
	/**
	 * The key of the disabled remove all icon.
	 */
	public static final String DISABLED_REMOVE_ALL_ICON_KEY = "DISABLED_REMOVE_ALL_ICON";
	
	/**
	 * The path of the disabled remove all icon.
	 */
	public static final String DISABLED_REMOVE_ALL_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "remove_all.gif";
	
	/**
	 * The key of the enabled expand all icon.
	 */
	public static final String ENABLED_EXPAND_ALL_ICON_KEY = "ENABLED_EXPAND_ALL_ICON";
	
	/**
	 * The path of the enabled expand all icon.
	 */
	public static final String ENABLED_EXPAND_ALL_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "expand_all.gif";
	
	/**
	 * The key of the disabled expand all icon.
	 */
	public static final String DISABLED_EXPAND_ALL_ICON_KEY = "DISABLED_EXPAND_ALL_ICON";
	
	/**
	 * The path of the disabled expand all icon.
	 */
	public static final String DISABLED_EXPAND_ALL_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "expand_all.gif";
	
	/**
	 * The key of the enabled collapse all icon.
	 */
	public static final String ENABLED_COLLAPSE_ALL_ICON_KEY = "ENABLED_COLLAPSE_ALL_ICON";
	
	/**
	 * The path of the enabled collapse all icon.
	 */
	public static final String ENABLED_COLLAPSE_ALL_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "collapse_all.gif";
	
	/**
	 * The key of the disabled collapse all icon.
	 */
	public static final String DISABLED_COLLAPSE_ALL_ICON_KEY = "DISABLED_COLLAPSE_ALL_ICON";
	
	/**
	 * The path of the disabled collapse all icon.
	 */
	public static final String DISABLED_COLLAPSE_ALL_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "collapse_all.gif";
	
	/**
	 * The key of the enabled list icon.
	 */
	public static final String ENABLED_LIST_ICON_KEY = "ENABLED_LIST";
	
	/**
	 * The path of the enabled list icon.
	 */
	public static final String ENABLED_LIST_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "list.gif";
	
	/**
	 * The key of the disabled list icon.
	 */
	public static final String DISABLED_LIST_ICON_KEY = "DISABLED_LIST";
	
	/**
	 * The path of the disabled list icon.
	 */
	public static final String DISABLED_LIST_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "list.gif";
	
	/**
	 * The key of the enabled tree icon.
	 */
	public static final String ENABLED_TREE_ICON_KEY = "ENABLED_TREE";
	
	/**
	 * The path of the enabled tree icon.
	 */
	public static final String ENABLED_TREE_ICON_PATH = GENERAL_ICONS_PATH + ENABLED_ICONS + "tree.gif";
	
	/**
	 * The key of the disabled tree icon.
	 */
	public static final String DISABLED_TREE_ICON_KEY = "DISABLED_TREE";
	
	/**
	 * The path of the disabled tree icon.
	 */
	public static final String DISABLED_TREE_ICON_PATH = GENERAL_ICONS_PATH + DISABLED_ICONS + "tree.gif";
	
	/**
	 * The key of the enabled search icon.
	 */
	public static final String ENABLED_SEARCH_ICON_KEY = "ENABLED_SEARCH";
	
	/**
	 * The path of the enabled search icon.
	 */
	public static final String ENABLED_SEARCH_ICON_PATH = SEARCH_ICONS_PATH + ENABLED_ICONS + "search.gif";
	
	/**
	 * The key of the enabled invariant violated query icon.
	 */
	public static final String ENABLED_INVARIANT_VIOLATED_ICON_KEY = "ENABLED_INVARIANT_VIOLATED";
	
	/**
	 * The path of the enabled invariant violated query icon.
	 */
	public static final String ENABLED_INVARIANT_VIOLATED_ICON_PATH = SEARCH_ICONS_PATH + ENABLED_ICONS + "invariant_violated.gif";
	
	/**
	 * The key of the enabled start event icon.
	 */
	public static final String ENABLED_START_EVENT_ICON_KEY = "ENABLED_START_EVENT";
	
	/**
	 * The path of the enabled start event icon.
	 */
	public static final String ENABLED_START_EVENT_ICON_PATH = EVENT_ICONS_PATH + "start.gif";
	
	/**
	 * The key of the enabled exit event icon.
	 */
	public static final String ENABLED_EXIT_EVENT_ICON_KEY = "ENABLED_EXIT_EVENT";
	
	/**
	 * The path of the enabled exit event icon.
	 */
	public static final String ENABLED_EXIT_EVENT_ICON_PATH = EVENT_ICONS_PATH + "exit.gif";
	
	/**
	 * The key of the enabled load event icon.
	 */
	public static final String ENABLED_LOAD_EVENT_ICON_KEY = "ENABLED_LOAD_EVENT";
	
	/**
	 * The path of the enabled load event icon.
	 */
	public static final String ENABLED_LOAD_EVENT_ICON_PATH = EVENT_ICONS_PATH + "load.gif";
	
	/**
	 * The key of the enabled new event icon.
	 */
	public static final String ENABLED_NEW_EVENT_ICON_KEY = "ENABLED_NEW_EVENT";
	
	/**
	 * The key of the enabled new event icon.
	 */
	public static final String ENABLED_NEW_EVENT_ICON_PATH = EVENT_ICONS_PATH + "new.gif";
	
	/**
	 * The key of the enabled call event icon.
	 */
	public static final String ENABLED_CALL_EVENT_ICON_KEY = "ENABLED_CALL_EVENT";
	
	/**
	 * The path of the enabled call event icon.
	 */
	public static final String ENABLED_CALL_EVENT_ICON_PATH = EVENT_ICONS_PATH + "call.gif";
	
	/**
	 * The key of the enabled return event icon.
	 */
	public static final String ENABLED_RETURN_EVENT_ICON_KEY = "ENABLED_RETURN_EVENT";
	
	/**
	 * The path of the enabled return event icon.
	 */
	public static final String ENABLED_RETURN_EVENT_ICON_PATH = EVENT_ICONS_PATH + "return.gif";
	
	/**
	 * The key of the enabled exception event icon.
	 * 
	 * @deprecated As of JIVE Platform 0.4, replace with {@link #ENABLED_THROW_EVENT_ICON_KEY}
	 * or {@link #ENABLED_CATCH_EVENT_ICON_KEY}.
	 */
	public static final String ENABLED_EXCEPTION_EVENT_ICON_KEY = "ENABLED_EXCEPTION_EVENT";
	
	/**
	 * The path of the enabled exception event icon.
	 * 
	 * @deprecated As of JIVE Platform 0.4, replace with {@link #ENABLED_THROW_EVENT_ICON_PATH}
	 * or {@link #ENABLED_CATCH_EVENT_ICON_PATH}.
	 */
	public static final String ENABLED_EXCEPTION_EVENT_ICON_PATH = EVENT_ICONS_PATH + "exception.gif";
	
	/**
	 * The key of the enabled throw event icon.
	 */
	public static final String ENABLED_THROW_EVENT_ICON_KEY = "ENABLED_THROW_EVENT";
	
	/**
	 * The path of the enabled throw event icon.
	 */
	public static final String ENABLED_THROW_EVENT_ICON_PATH = EVENT_ICONS_PATH + "throw.gif";
	
	/**
	 * The key of the enabled throw event icon.
	 */
	public static final String ENABLED_CATCH_EVENT_ICON_KEY = "ENABLED_CATCH_EVENT";
	
	/**
	 * The path of the enabled throw event icon.
	 */
	public static final String ENABLED_CATCH_EVENT_ICON_PATH = EVENT_ICONS_PATH + "catch.gif";
	
	/**
	 * The key of the enabled EOS event icon.
	 */
	public static final String ENABLED_EOS_EVENT_ICON_KEY = "ENABLED_EOS_EVENT";
	
	/**
	 * The path of the enabled EOS event icon.
	 */
	public static final String ENABLED_EOS_EVENT_ICON_PATH = EVENT_ICONS_PATH + "eos.gif";
	
	/**
	 * The key of the enabled assign event icon.
	 */
	public static final String ENABLED_ASSIGN_EVENT_ICON_KEY = "ENABLED_ASSIGN_EVENT";
	
	/**
	 * The path of the enabled assign event icon.
	 */
	public static final String ENABLED_ASSIGN_EVENT_ICON_PATH = EVENT_ICONS_PATH + "assign.gif";
	
	/**
	 * The key of the enabled static contour icon.
	 */
	public static final String ENABLED_STATIC_CONTOUR_ICON_KEY = "ENABLED_STATIC_CONTOUR";
	
	/**
	 * The path of the enabled static contour icon.
	 */
	public static final String ENABLED_STATIC_CONTOUR_ICON_PATH = CONTOUR_ICONS_PATH + "static.gif";
	
	/**
	 * The key of the enabled instance contour icon.
	 */
	public static final String ENABLED_INSTANCE_CONTOUR_ICON_KEY = "ENABLED_INSTANCE_CONTOUR";
	
	/**
	 * The path of the enabled instance contour icon.
	 */
	public static final String ENABLED_INSTANCE_CONTOUR_ICON_PATH = CONTOUR_ICONS_PATH + "instance.gif";
	
	/**
	 * The key of the enabled method contour icon.
	 */
	public static final String ENABLED_METHOD_CONTOUR_ICON_KEY = "ENABLED_METHOD_CONTOUR";
	
	/**
	 * The path of the enabled method contour icon.
	 */
	public static final String ENABLED_METHOD_CONTOUR_ICON_PATH = CONTOUR_ICONS_PATH + "method.gif";
	
	/**
	 * The key of the enabled thread activation icon.
	 */
	public static final String ENABLED_THREAD_ACTIVATION_ICON_KEY = "ENABLED_THREAD_ACTIVATION";
	
	/**
	 * The path of the enabled thread activation icon.
	 */
	public static final String ENABLED_THREAD_ACTIVATION_ICON_PATH = EXECUTION_ICONS_PATH + "thread.gif";
	
	/**
	 * The key of the enabled method activation icon.
	 */
	public static final String ENABLED_METHOD_ACTIVATION_ICON_KEY = "ENABLED_METHOD_ACTIVATION";
	
	/**
	 * The path of the enabled method activation icon.
	 */
	public static final String ENABLED_METHOD_ACTIVATION_ICON_PATH = EXECUTION_ICONS_PATH + "method.gif";
	
	/**
	 * The key of the enabled filtered method activation icon.
	 */
	public static final String ENABLED_FILTERED_METHOD_ACTIVATION_ICON_KEY = "ENABLED_FILTERED_METHOD_ACTIVATION";
	
	/**
	 * The path of the enabled filtered method activation icon.
	 */
	public static final String ENABLED_FILTERED_METHOD_ACTIVATION_ICON_PATH = EXECUTION_ICONS_PATH + "filtered_method.gif";
	
	/**
	 * The key of the enabled step backward icon.
	 */
	public static final String ENABLED_STEP_BACKWARD_ICON_KEY = "ENABLED_STEP_BACKWARD";
	
	/**
	 * The path of the enabled step backward icon.
	 */
	public static final String ENABLED_STEP_BACKWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "step_backward.gif";
	
	/**
	 * The key of the disabled step backward icon.
	 */
	public static final String DISABLED_STEP_BACKWARD_ICON_KEY = "DISABLED_STEP_BACKWARD";
	
	/**
	 * The path of the disabled step backward icon.
	 */
	public static final String DISABLED_STEP_BACKWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + DISABLED_ACTION_ICONS + "step_backward.gif";
	
	/**
	 * The key of the enabled step forward icon.
	 */
	public static final String ENABLED_STEP_FORWARD_ICON_KEY = "ENABLED_STEP_FORWARD";
	
	/**
	 * The path of the enabled step forward icon.
	 */
	public static final String ENABLED_STEP_FORWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "step_forward.gif";
	
	/**
	 * The key of the disabled step forward icon.
	 */
	public static final String DISABLED_STEP_FORWARD_ICON_KEY = "DISABLED_STEP_FORWARD";
	
	/**
	 * The path of the disabled step forward icon.
	 */
	public static final String DISABLED_STEP_FORWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + DISABLED_ACTION_ICONS + "step_forward.gif";
	
	/**
	 * The key of the enabled run backward icon.
	 */
	public static final String ENABLED_RUN_BACKWARD_ICON_KEY = "ENABLED_RUN_BACKWARD";
	
	/**
	 * The path of the enabled run backward icon.
	 */
	public static final String ENABLED_RUN_BACKWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "run_backward.gif";
	
	/**
	 * The key of the disabled run backward icon.
	 */
	public static final String DISABLED_RUN_BACKWARD_ICON_KEY = "DISABLED_RUN_BACKWARD";
	
	/**
	 * The path of the disabled run backward icon.
	 */
	public static final String DISABLED_RUN_BACKWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + DISABLED_ACTION_ICONS + "run_backward.gif";
	
	/**
	 * The key of the enabled run forward icon.
	 */
	public static final String ENABLED_RUN_FORWARD_ICON_KEY = "ENABLED_RUN_FORWARD";
	
	/**
	 * The path of the enabled run forward icon.
	 */
	public static final String ENABLED_RUN_FORWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "run_forward.gif";
	
	/**
	 * The key of the disabled run forward icon.
	 */
	public static final String DISABLED_RUN_FORWARD_ICON_KEY = "DISABLED_RUN_FORWARD";
	
	/**
	 * The path of the disabled run forward icon.
	 */
	public static final String DISABLED_RUN_FORWARD_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + DISABLED_ACTION_ICONS + "run_forward.gif";
	
	/**
	 * The key of the enabled pause icon.
	 */
	public static final String ENABLED_PAUSE_ICON_KEY = "ENABLED_PAUSE_FORWARD";
	
	/**
	 * The path of the enabled pause icon.
	 */
	public static final String ENABLED_PAUSE_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "pause.gif";
	
	/**
	 * The key of the disabled pause icon.
	 */
	public static final String DISABLED_PAUSE_ICON_KEY = "DISABLED_PAUSE";
	
	/**
	 * The path of the disabled pause icon.
	 */
	public static final String DISABLED_PAUSE_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + DISABLED_ACTION_ICONS + "pause.gif";
	
	/**
	 * The key of the enabled minimize contours icon.
	 */
	public static final String ENABLED_MINIMIZE_CONTOURS_ICON_KEY = "ENABLED_MINIMIZE_CONTOURS";
	
	/**
	 * The path of the enabled minimize contours icon.
	 */
	public static final String ENABLED_MINIMIZE_CONTOURS_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "minimize_contours.gif";
	
	/**
	 * The key of the enabled expand contours icon.
	 */
	public static final String ENABLED_EXPAND_CONTOURS_ICON_KEY = "ENABLED_EXPAND_CONTOURS";
	
	/**
	 * The path of the enabled expand contours icon.
	 */
	public static final String ENABLED_EXPAND_CONTOURS_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "expand_contours.gif";
	
	/**
	 * The key of the enabled stack instance contours icon.
	 */
	public static final String ENABLED_STACK_INSTANCE_CONTOURS_ICON_KEY = "ENABLED_STACK_INSTANCE_CONTOURS";
	
	/**
	 * The path of the enabled stack instance contours icon.
	 */
	public static final String ENABLED_STACK_INSTANCE_CONTOURS_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "stack_instance_contours.gif";
	
	/**
	 * The key of the show member tables icon.
	 */
	public static final String ENABLED_SHOW_MEMBER_TABLES_ICON_KEY = "ENABLED_SHOW_MEMBER_TABLES";
	
	/**
	 * The path of the show member tables icon.
	 */
	public static final String ENABLED_SHOW_MEMBER_TABLES_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "show_member_tables.gif";
	
	/**
	 * The key of the show member tables icon.
	 */
	public static final String ENABLED_SCROLL_LOCK_ICON_KEY = "ENABLED_SCROLL_LOCK_TABLES";
	
	/**
	 * The path of the show member tables icon.
	 */
	public static final String ENABLED_SCROLL_LOCK_ICON_PATH = CONTOUR_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "scroll_lock.gif";
	
	/**
	 * The key of the hide message receives icon.
	 */
	public static final String ENABLED_HIDE_MESSAGE_RECEIVES_ICON_KEY = "ENABLED_HIDE_MESSAGE_RECEIVES_ICON";

	/**
	 * The path of the hide message receives icon.
	 */
	public static final String ENABLED_HIDE_MESSAGE_RECEIVES_ICON_PATH = SEQUENCE_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "hide_message_receives.gif";
	
	/**
	 * The key of the show thread activations icon.
	 */
	public static final String ENABLED_SHOW_THREAD_ACTIVATIONS_ICON_KEY = "ENABLED_SHOW_THREAD_ACTIVATIONS_ICON";
	
	/**
	 * The path of the show thread activations icon.
	 */
	public static final String ENABLED_SHOW_THREAD_ACTIVATIONS_ICON_PATH = SEQUENCE_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "show_thread_activations.gif";
	
	/**
	 * The key of the expand lifelines icon.
	 */
	public static final String ENABLED_EXPAND_LIFELINES_ICON_KEY = "ENABLED_EXPAND_LIFELINES_ICON";

	/**
	 * The path of the expand lifelines icon.
	 */
	public static final String ENABLED_EXPAND_LIFELINES_ICON_PATH = SEQUENCE_MODEL_ICONS_PATH + ENABLED_ACTION_ICONS + "expand_lifelines.gif";
	
	/*
	 * Constants for preference names
	 */
	
	/**
	 * Diagram update interval (in milliseconds) preference name.
	 */
	public static final String PREF_UPDATE_INTERVAL = PLUGIN_ID_PREFIX + ".update_interval";
	
	/**
	 * Contour diagram state preference name.
	 */
	public static final String PREF_CONTOUR_STATE = PLUGIN_ID_PREFIX + ".contour_state";
	
	/**
	 * Contour diagram minimized state preference value.
	 */
	public static final String PREF_CONTOUR_STATE_MINIMIZE = "minimize";
	
	/**
	 * Contour diagram expanded state preference value.
	 */
	public static final String PREF_CONTOUR_STATE_EXPAND = "expand";
	
	/**
	 * Contour diagram stacked state preference value.
	 */
	public static final String PREF_CONTOUR_STATE_STACK = "stack";
	
	/**
	 * Contour diagram member table preference.
	 */
	public static final String PREF_SHOW_MEMBER_TABLES = PLUGIN_ID_PREFIX + ".show_member_tables";
	
	/**
	 * Contour diagram scroll lock preference.
	 */
	public static final String PREF_SCROLL_LOCK = PLUGIN_ID_PREFIX + ".scroll_lock";
	
	/**
	 * Sequence diagram show thread activation preference.
	 */
	public static final String PREF_SHOW_THREAD_ACTIVATIONS = PLUGIN_ID_PREFIX + ".show_thread_activations";
	
	/**
	 * Sequence diagram expand lifelines preference.
	 */
	public static final String PREF_EXPAND_LIFELINES = PLUGIN_ID_PREFIX + ".expand_lifelines";
	
	/**
	 * Sequence diagram activation width preference.
	 */
	public static final String PREF_ACTIVATION_WIDTH = PLUGIN_ID_PREFIX + ".activation_width";
	
	/**
	 * Sequence diagram event height preference.
	 */
	public static final String PREF_EVENT_HEIGHT = PLUGIN_ID_PREFIX + ".event_height";
	
	/**
	 * Sequence diagram thread color preference prefix.
	 */
	public static final String PREF_THREAD_COLOR_PREFIX = PLUGIN_ID_PREFIX + ".thread_color_";
	
	/**
	 * Sequence diagram thread color 1 preference.
	 */
	public static final String PREF_THREAD_COLOR_1 = PREF_THREAD_COLOR_PREFIX + "1";
	
	/**
	 * Sequence diagram thread color 2 preference.
	 */
	public static final String PREF_THREAD_COLOR_2 = PREF_THREAD_COLOR_PREFIX + "2";
	
	/**
	 * Sequence diagram thread color 3 preference.
	 */
	public static final String PREF_THREAD_COLOR_3 = PREF_THREAD_COLOR_PREFIX + "3";
	
	/**
	 * Sequence diagram thread color 4 preference.
	 */
	public static final String PREF_THREAD_COLOR_4 = PREF_THREAD_COLOR_PREFIX + "4";
	
	/**
	 * Sequence diagram thread color 5 preference.
	 */
	public static final String PREF_THREAD_COLOR_5 = PREF_THREAD_COLOR_PREFIX + "5";
	
	/**
	 * Java application event filters.
	 */
	public static final String PREF_JAVA_APPLICATION_FILTERS =  PLUGIN_ID_PREFIX + ".java_application_filters";
	
	/**
	 * Java applet event filters.
	 */
	public static final String PREF_JAVA_APPLET_FILTERS =  PLUGIN_ID_PREFIX + ".java_applet_filters";
	
	/**
	 * JUnit application filters.
	 */
	public static final String PREF_JUNIT_APPLICATION_FILTERS =  PLUGIN_ID_PREFIX + ".junit_application_filters";
}
