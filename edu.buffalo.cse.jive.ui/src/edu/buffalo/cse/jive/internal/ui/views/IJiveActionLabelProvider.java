package edu.buffalo.cse.jive.internal.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;

public interface IJiveActionLabelProvider {

	/**
	 * Provides an image descriptor for the action.
	 * 
	 * @return an image descriptor for the action
	 */
	public ImageDescriptor getImageDescriptor();
	
	/**
	 * Provides text for the element.
	 * 
	 * @return the action text
	 */
	public String getText();
}
