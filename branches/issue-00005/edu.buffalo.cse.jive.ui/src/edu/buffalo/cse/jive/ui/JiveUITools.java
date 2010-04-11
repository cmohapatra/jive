package edu.buffalo.cse.jive.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

/**
 * Miscellaneous utility methods used to access portions of the JIVE UI plug-in.
 * This class will be expanded in the future.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveUITools {

	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 * 
	 * @return the standard display
	 */
	public static Display getStandardDisplay() {
		return JiveUIPlugin.getStandardDisplay();
	}
	
	/**
	 * Returns the {@code ImageDescriptor} associated with the supplied key.
	 * 
	 * @see IJiveUIConstants
	 * @param key the key for the image descriptor
	 * @return the image descriptor associated with the key
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return JiveUIPlugin.getDefault().getImageRegistry().getDescriptor(key);
	}
	
	/**
	 * Returns the {@code Image} associated with the supplied key.
	 * 
	 * @see IJiveUIConstants
	 * @param key the key for the image
	 * @return the image associated with the key
	 */
	public static Image getImage(String key) {
		return JiveUIPlugin.getDefault().getImageRegistry().get(key);
	}
}
