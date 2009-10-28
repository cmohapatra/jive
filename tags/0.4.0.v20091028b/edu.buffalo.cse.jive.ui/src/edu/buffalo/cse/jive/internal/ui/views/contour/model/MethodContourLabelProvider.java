package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A <code>Contour</code> exporter used to provide labels for a
 * <code>MethodContour</code> to be used by an <code>ILabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class MethodContourLabelProvider extends AbstractContourLabelProvider implements MethodContour.Exporter {

	/**
	 * The image representation of the contour.
	 */
	private static final Image CONTOUR_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		CONTOUR_IMAGE = registry.get(IJiveUIConstants.ENABLED_METHOD_CONTOUR_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.model.AbstractContourLabelProvider#getImage()
	 */
	public Image getImage() {
		return CONTOUR_IMAGE;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.java.MethodContour.Exporter#addThread(edu.bsu.cs.jive.util.ThreadID)
	 */
	public void addThread(ThreadID thread) {
		// TODO Determine if label should differ by thread
	}
}
