package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * 
 * @author jkczyz
 */
public class InstanceContourFigureBuilder extends JavaContourFigureBuilder {

	/**
	 * 
	 */
	private static final char CONTOUR_TEXT_DELIMETER = '.';
	
	/**
	 * 
	 */
	private static final Image CONTOUR_IMAGE;

	/**
	 * 
	 */
	private String text;
	
	/**
	 * 
	 */
	private String toolTipText;
	
	static {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		CONTOUR_IMAGE = registry.get(IJiveUIConstants.ENABLED_INSTANCE_CONTOUR_ICON_KEY);
	}
	
	/**
	 * @param id
	 */
	public InstanceContourFigureBuilder(ContourID id) {
		toolTipText = id.toString();
		text = computeText(toolTipText, CONTOUR_TEXT_DELIMETER);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure.Importer#provideText()
	 */
	public String provideText() {
		return text;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure.Importer#provideIcon()
	 */
	public Image provideIcon() {
		return CONTOUR_IMAGE;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure.Importer#provideToolTipText()
	 */
	public String provideToolTipText() {
		return toolTipText;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure.Importer#provideToolTipIcon()
	 */
	public Image provideToolTipIcon() {
		return CONTOUR_IMAGE;
	}
}
