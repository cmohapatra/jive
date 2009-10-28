package edu.buffalo.cse.jive.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class that controls the plug-in life cycle and provides
 * utility methods.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveCorePlugin extends Plugin {

	/**
	 * The unique identifier of the plug-in.
	 */
	public static final String PLUGIN_ID = "edu.buffalo.cse.jive.core"; //$NON-NLS-1$

	/**
	 * The shared instance of the plug-in. 
	 */
	private static JiveCorePlugin plugin;
	
	/**
	 * Constructs the JIVE core plug-in.  This constructor is called by the
	 * Eclipse platform and should not be called by clients.
	 * 
	 * @throws IllegalStateException if the plug-in has already been instantiated
	 */
	public JiveCorePlugin() {
		if (plugin != null) {
			// TODO Add log message and internationalize the string literal
			throw new IllegalStateException("The JIVE core plug-in class already exists.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance of the JIVE core plug-in.
	 *
	 * @return the shared instance
	 */
	public static JiveCorePlugin getDefault() {
		return plugin;
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
	 * Logs a string to the Eclipse error log as an <code>IStatus.ERROR</code>
	 * object.
	 * 
	 * @param message the message to be recorded
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}
	
	/**
	 * Logs the message assoicated with a throwable object to the Eclipse error
	 * log as an <code>IStatus.ERROR</code> object. 
	 * 
	 * @param e the throwable object whose message is recorded
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
	}

}
