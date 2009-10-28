package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders;

import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;

public abstract class JavaContourFigureBuilder implements ContourFigure.Importer {

	protected String computeText(String text, char delimiter) {
		int index = text.lastIndexOf(delimiter);
		return text.substring(index + 1);
	}
}
