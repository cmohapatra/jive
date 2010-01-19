package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A Draw2d figure used for visualizing an {@code MethodContour}.
 * 
 * @see MethodContour
 * @author Jeffrey K Czyz
 */
public class MethodContourFigure extends JavaContourFigure {

	/**
	 * The delimiter for the figure's label.
	 * 
	 * @see #getContourTextDelimiter()
	 */
	private static final char CONTOUR_TEXT_DELIMETER = '#';
	
	/**
	 * The image for the figure's label.
	 */
	private static final Image CONTOUR_IMAGE;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		CONTOUR_IMAGE = registry.get(IJiveUIConstants.ENABLED_METHOD_CONTOUR_ICON_KEY);
	}
	
	/**
	 * Constructs the contour figure with the supplied identifier.
	 * 
	 * @param id the string for the contour figure's label
	 * @see JavaContourFigure#JavaContourFigure(String)
	 */
	public MethodContourFigure(String id) {
		super(id);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.JavaContourFigure#getContourImage()
	 */
	protected Image getContourImage() {
		return CONTOUR_IMAGE;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.JavaContourFigure#getContourTextDelimiter()
	 */
	protected char getContourTextDelimiter() {
		return CONTOUR_TEXT_DELIMETER;
	}
}
