package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A <code>Contour</code> exporter used to provide labels for an
 * <code>InstanceContour</code> to be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class InstanceContourLabelProvider extends AbstractContourLabelProvider implements InstanceContour.Exporter {

	/**
	 * The image representation of the contour.
	 */
	private static final Image CONTOUR_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		CONTOUR_IMAGE = registry.get(IJiveUIConstants.ENABLED_INSTANCE_CONTOUR_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.model.AbstractContourLabelProvider#getImage()
	 */
	public Image getImage() {
		return CONTOUR_IMAGE;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.java.InstanceContour.Exporter#addVirtualProperty(boolean)
	 */
	public void addVirtualProperty(boolean virtual) {
		// TODO Determine if label should be different for virtual contours
	}
}
