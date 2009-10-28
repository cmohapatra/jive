package edu.buffalo.cse.jive.internal.ui.views;

import org.eclipse.swt.graphics.Image;

/**
 * An interface to provide image and text labels for a row in a table.  This
 * interface is intended to be used by an <code>ITableLabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public interface IJiveTableRowLabelProvider {
	
	/**
	 * Provides an image for the column with the supplied index.
	 * 
	 * @param columnIndex the column in which the image will be presented
	 * @return the column image
	 */
	public Image getColumnImage(int columnIndex);
	
	/**
	 * Provides text for the column with the supplied index.
	 * 
	 * @param columnIndex the column in which the text will be presented
	 * @return the column text
	 */
	public String getColumnText(int columnIndex);

}
