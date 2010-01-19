package edu.buffalo.cse.jive.internal.ui.views;

import org.eclipse.swt.graphics.Image;

/**
 * An interface to provide image and text labels for elements in a list or tree.
 * This interface is intended to be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public interface IJiveLabelProvider {
	
	/**
	 * Provides an image for the element.
	 * 
	 * @return the label image
	 */
	public Image getImage();
	
	/**
	 * Provides text for the element.
	 * 
	 * @return the label text
	 */
	public String getText();

}
