package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.StaticContour;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A <code>Contour</code> exporter used to provide labels for a
 * <code>StaticContour</code> to be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class StaticContourLabelProvider extends AbstractContourLabelProvider implements StaticContour.Exporter {

	/**
	 * The image representation of the contour.
	 */
	private static final Image CONTOUR_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		CONTOUR_IMAGE = registry.get(IJiveUIConstants.ENABLED_STATIC_CONTOUR_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.model.AbstractContourLabelProvider#getImage()
	 */
	public Image getImage() {
		return CONTOUR_IMAGE;
	}
}
