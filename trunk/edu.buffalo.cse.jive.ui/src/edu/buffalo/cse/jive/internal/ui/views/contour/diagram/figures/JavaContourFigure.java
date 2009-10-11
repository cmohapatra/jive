package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.JavaContour;

/**
 * A Draw2d figure used to visualize {@code JavaContour}s.
 * 
 * @see JavaContour
 * @see ContourFigure
 * @author Jeffrey K Czyz
 */
public abstract class JavaContourFigure extends ContourFigure {
	
	/**
	 * The string used for the contour figure's label.
	 */
	private String labelText;
	
	/**
	 * The string used for the tool tip of the contour figure's label. 
	 */
	private String toolTipText;
	
	/**
	 * Constructs the contour figure with the supplied identifier to be used by
	 * the contour figure's label.  The last portion of the identifier is used
	 * for the label, and the complete identifier is used for the label's tool
	 * tip.  The last portion is defined by all text after the last occurrence
	 * of the delimeter returned by {@link #getContourTextDelimiter()}.
	 * 
	 * @param id the string for the contour figure's label
	 */
	protected JavaContourFigure(String id) {
		super();
		int index = id.lastIndexOf(getContourTextDelimiter());
		labelText = id.substring(index + 1);
		toolTipText = id;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure#getLabelIcon()
	 */
	protected Image getLabelIcon() {
		return getContourImage();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure#getLabelText()
	 */
	protected String getLabelText() {
		return labelText;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure#getToolTipText()
	 */
	protected String getToolTipText() {
		return toolTipText;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure#getToolTipIcon()
	 */
	protected Image getToolTipIcon() {
		return getContourImage();
	}
	
	/**
	 * Returns the image used for the contour figure's label.
	 * 
	 * @return the image for the figure's label
	 */
	protected abstract Image getContourImage();
	
	/**
	 * Returns the delimiter to be used when determining the contour figure's
	 * label.
	 * 
	 * @return the delimiter for the contour figure's label
	 * @see #JavaContourFigure(String)
	 */
	protected abstract char getContourTextDelimiter();
	
}
