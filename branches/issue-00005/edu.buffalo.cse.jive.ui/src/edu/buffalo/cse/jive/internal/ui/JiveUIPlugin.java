package edu.buffalo.cse.jive.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.buffalo.cse.jive.internal.ui.search.JiveSearchPageDescriptor;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepManager;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager;
import edu.buffalo.cse.jive.internal.ui.views.sequence.ThreadColorManager;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

import static edu.buffalo.cse.jive.ui.IJiveUIConstants.*;

/**
 * The activator class that controls the plug-in life cycle and provides
 * utility methods.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveUIPlugin extends AbstractUIPlugin implements IPropertyChangeListener {

	/**
	 * The unique identifier of the plug-in.
	 */
	public static final String PLUGIN_ID = "edu.buffalo.cse.jive.ui";

	/**
	 * The shared instance of the plug-in. 
	 */
	private static JiveUIPlugin plugin;
	
	/**
	 * The {@code IStepManager} used by the plug-in.
	 */
	private StepManager stepManager;
	
	/**
	 * The {@code IThreadColorManager} used by the plug-in.
	 */
	private ThreadColorManager threadColorManager;
	
	/**
	 * The {@code SourceLookupFacility} used by the plug-in.
	 */
	private SourceLookupFacility sourceLookupFacility;
	
	/**
	 * The width in pixels of activations on the sequence diagram.
	 */
	private int activationWidth;
	
	/**
	 * The height in pixels of events on the sequence diagram.
	 */
	private int eventHeight;
	
	/**
	 * Constructs the JIVE UI plug-in.  This constructor is called by the
	 * Eclipse platform and should not be called by clients.
	 * 
	 * @throws IllegalStateException if the plug-in has already been instantiated
	 */
	public JiveUIPlugin() {
		if (plugin != null) {
			// TODO Add log message and internationalize the string literal
			throw new IllegalStateException("The JIVE UI plug-in class already exists.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		sourceLookupFacility = new SourceLookupFacility();
		
		// Initialize the sequence diagram attributes
		Preferences prefs = getPluginPreferences();
		activationWidth = prefs.getInt(IJiveUIConstants.PREF_ACTIVATION_WIDTH);
		eventHeight = prefs.getInt(IJiveUIConstants.PREF_EVENT_HEIGHT);
		prefs.addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// Clean up the source lookup facility
		sourceLookupFacility.dispose();
		
		// Clean up the step manager
		if (stepManager != null) {
			stepManager.dispose();
			stepManager = null;
		}
		
		// Clean up the thread color manager
		if (threadColorManager != null) {
			threadColorManager.dispose();
			threadColorManager = null;
		}
		
		getPluginPreferences().removePropertyChangeListener(this);
		plugin = null;
		
		super.stop(context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	protected void initializeImageRegistry(ImageRegistry reg) {
		// Add general purpose images to the image registry
		declareRegistryImage(ENABLED_JIVE_ICON_KEY, ENABLED_JIVE_ICON_PATH);
		declareRegistryImage(ENABLED_REMOVE_ICON_KEY, ENABLED_REMOVE_ICON_PATH);
		declareRegistryImage(DISABLED_REMOVE_ICON_KEY, DISABLED_REMOVE_ICON_PATH);
		declareRegistryImage(ENABLED_REMOVE_ALL_ICON_KEY, ENABLED_REMOVE_ALL_ICON_PATH);
		declareRegistryImage(DISABLED_REMOVE_ALL_ICON_KEY, DISABLED_REMOVE_ALL_ICON_PATH);
		declareRegistryImage(ENABLED_LIST_ICON_KEY, ENABLED_LIST_ICON_PATH);
		declareRegistryImage(DISABLED_LIST_ICON_KEY, DISABLED_LIST_ICON_PATH);
		declareRegistryImage(ENABLED_TREE_ICON_KEY, ENABLED_TREE_ICON_PATH);
		declareRegistryImage(DISABLED_TREE_ICON_KEY, DISABLED_TREE_ICON_PATH);
		declareRegistryImage(ENABLED_EXPAND_ALL_ICON_KEY, ENABLED_EXPAND_ALL_ICON_PATH);
		declareRegistryImage(DISABLED_EXPAND_ALL_ICON_KEY, DISABLED_EXPAND_ALL_ICON_PATH);
		declareRegistryImage(ENABLED_COLLAPSE_ALL_ICON_KEY, ENABLED_COLLAPSE_ALL_ICON_PATH);
		declareRegistryImage(DISABLED_COLLAPSE_ALL_ICON_KEY, DISABLED_COLLAPSE_ALL_ICON_PATH);
		
		// Add search images to the image registry
		declareRegistryImage(ENABLED_SEARCH_ICON_KEY, ENABLED_SEARCH_ICON_PATH);
		declareRegistryImage(ENABLED_INVARIANT_VIOLATED_ICON_KEY, ENABLED_INVARIANT_VIOLATED_ICON_PATH);
		
		// Add event images to the image registry
		declareRegistryImage(ENABLED_START_EVENT_ICON_KEY, ENABLED_START_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_EXIT_EVENT_ICON_KEY, ENABLED_EXIT_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_LOAD_EVENT_ICON_KEY, ENABLED_LOAD_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_NEW_EVENT_ICON_KEY, ENABLED_NEW_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_CALL_EVENT_ICON_KEY, ENABLED_CALL_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_RETURN_EVENT_ICON_KEY, ENABLED_RETURN_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_EXCEPTION_EVENT_ICON_KEY, ENABLED_EXCEPTION_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_THROW_EVENT_ICON_KEY, ENABLED_THROW_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_CATCH_EVENT_ICON_KEY, ENABLED_CATCH_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_EOS_EVENT_ICON_KEY, ENABLED_EOS_EVENT_ICON_PATH);
		declareRegistryImage(ENABLED_ASSIGN_EVENT_ICON_KEY, ENABLED_ASSIGN_EVENT_ICON_PATH);
		
		// Add contour model images to the image registry
		declareRegistryImage(ENABLED_STATIC_CONTOUR_ICON_KEY, ENABLED_STATIC_CONTOUR_ICON_PATH);
		declareRegistryImage(ENABLED_INSTANCE_CONTOUR_ICON_KEY, ENABLED_INSTANCE_CONTOUR_ICON_PATH);
		declareRegistryImage(ENABLED_METHOD_CONTOUR_ICON_KEY, ENABLED_METHOD_CONTOUR_ICON_PATH);
		
		// Add sequence model images to the image registry
		declareRegistryImage(ENABLED_THREAD_ACTIVATION_ICON_KEY, ENABLED_THREAD_ACTIVATION_ICON_PATH);
		declareRegistryImage(ENABLED_METHOD_ACTIVATION_ICON_KEY, ENABLED_METHOD_ACTIVATION_ICON_PATH);
		declareRegistryImage(ENABLED_FILTERED_METHOD_ACTIVATION_ICON_KEY, ENABLED_FILTERED_METHOD_ACTIVATION_ICON_PATH);
		
		// Add contour model actions to the image registry
		declareRegistryImage(ENABLED_STEP_BACKWARD_ICON_KEY, ENABLED_STEP_BACKWARD_ICON_PATH);
		declareRegistryImage(DISABLED_STEP_BACKWARD_ICON_KEY, DISABLED_STEP_BACKWARD_ICON_PATH);
		declareRegistryImage(ENABLED_STEP_FORWARD_ICON_KEY, ENABLED_STEP_FORWARD_ICON_PATH);
		declareRegistryImage(DISABLED_STEP_FORWARD_ICON_KEY, DISABLED_STEP_FORWARD_ICON_PATH);
		declareRegistryImage(ENABLED_RUN_BACKWARD_ICON_KEY, ENABLED_RUN_BACKWARD_ICON_PATH);
		declareRegistryImage(DISABLED_RUN_BACKWARD_ICON_KEY, DISABLED_RUN_BACKWARD_ICON_PATH);
		declareRegistryImage(ENABLED_RUN_FORWARD_ICON_KEY, ENABLED_RUN_FORWARD_ICON_PATH);
		declareRegistryImage(DISABLED_RUN_FORWARD_ICON_KEY, DISABLED_RUN_FORWARD_ICON_PATH);
		declareRegistryImage(ENABLED_PAUSE_ICON_KEY, ENABLED_PAUSE_ICON_PATH);
		declareRegistryImage(DISABLED_PAUSE_ICON_KEY, DISABLED_PAUSE_ICON_PATH);
		declareRegistryImage(ENABLED_MINIMIZE_CONTOURS_ICON_KEY, ENABLED_MINIMIZE_CONTOURS_ICON_PATH);
		declareRegistryImage(ENABLED_EXPAND_CONTOURS_ICON_KEY, ENABLED_EXPAND_CONTOURS_ICON_PATH);
		declareRegistryImage(ENABLED_STACK_INSTANCE_CONTOURS_ICON_KEY, ENABLED_STACK_INSTANCE_CONTOURS_ICON_PATH);
		declareRegistryImage(ENABLED_SHOW_MEMBER_TABLES_ICON_KEY, ENABLED_SHOW_MEMBER_TABLES_ICON_PATH);
		declareRegistryImage(ENABLED_SCROLL_LOCK_ICON_KEY, ENABLED_SCROLL_LOCK_ICON_PATH);
		
		// Add sequence model actions to the image registry
		declareRegistryImage(ENABLED_HIDE_MESSAGE_RECEIVES_ICON_KEY, ENABLED_HIDE_MESSAGE_RECEIVES_ICON_PATH);
		declareRegistryImage(ENABLED_SHOW_THREAD_ACTIVATIONS_ICON_KEY, ENABLED_SHOW_THREAD_ACTIVATIONS_ICON_PATH);
		declareRegistryImage(ENABLED_EXPAND_LIFELINES_ICON_KEY, ENABLED_EXPAND_LIFELINES_ICON_PATH);
	}
	
	/**
	 * Adds an image to the plug-in's image registry.  The image's
	 * {@code ImageDescriptor} is stored with the supplied key.  The image
	 * itself is located at the supplied path.
	 * 
	 * @param key the key to be associated with the image
	 * @param path the location of the image
	 */
	private void declareRegistryImage(String key, String path) {
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(getBundle().getEntry(path));
		getImageRegistry().put(key, descriptor);
	}
	
	/**
	 * Returns the {@code IStepManager} used by the plug-in.
	 * 
	 * @return the step manager
	 */
	public IStepManager getStepManager() {
		if (stepManager == null) {
			stepManager = new StepManager();
		}
		
		return stepManager;
	}
	
	/**
	 * Returns the {@code IThreadColorManager} used by the plug-in.
	 * 
	 * @return the thread color manager
	 */
	public IThreadColorManager getThreadColorManager() {
		if (threadColorManager == null) {
			threadColorManager = new ThreadColorManager();
		}
		
		return threadColorManager;
	}
	
	/**
	 * Returns the width in pixels of activations on the sequence diagram.
	 * 
	 * @return the activation width
	 */
	public int getActivationWidth() {
		return activationWidth;
	}
	
	/**
	 * Returns the height in pixels of events on the sequence diagram.
	 * 
	 * @return the event height
	 */
	public int getEventHeight() {
		return eventHeight;
	}
	
	/**
	 * Returns a list of search page descriptors available to the plug-in.
	 * 
	 * @see JiveSearchPageDescriptor
	 * @return a list of search page descriptors
	 */
	public List<JiveSearchPageDescriptor> getSearchPageDescriptors() {
		List<JiveSearchPageDescriptor> result = new ArrayList<JiveSearchPageDescriptor>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(IJiveUIConstants.SEARCH_PAGES_EXTENSION_POINT_ID);
		
		for (IConfigurationElement element : elements) {
			if (JiveSearchPageDescriptor.PAGE_TAG.equals(element.getName())) {
				result.add(new JiveSearchPageDescriptor(element));
			}
		}
		
		return result;
	}

	/**
	 * Returns the shared instance of the JIVE UI plug-in.
	 *
	 * @return the shared instance
	 */
	public static JiveUIPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 * 
	 */
	// TODO Taken from org.eclipse.ui.console.ConsolePlugin
	// TODO Determine why this needs to be done
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;		
	}

	/**
	 * Logs a status object to the Eclipse error log.
	 * 
	 * @param status the status object to record
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	/**
	 * Logs a string to the Eclipse error log as an {@code IStatus.ERROR}
	 * object.
	 * 
	 * @param message the message to be recorded
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}
		
	/**
	 * Logs the message associated with a {@code Throwable} object to the
	 * Eclipse error log as an {@code IStatus.ERROR} object. 
	 * 
	 * @param e the {@code Throwable} object whose message is recorded
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(IJiveUIConstants.PREF_ACTIVATION_WIDTH)) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			activationWidth = prefs.getInt(IJiveUIConstants.PREF_ACTIVATION_WIDTH);
		}
		else if (property.equals(IJiveUIConstants.PREF_EVENT_HEIGHT)) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			eventHeight = prefs.getInt(IJiveUIConstants.PREF_EVENT_HEIGHT);
		}
	}
}
