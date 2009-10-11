package edu.buffalo.cse.jive.ui;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;

/**
 * A view part used to present {@code IJiveDebugTarget}s using a GEF
 * {@code GraphicalViewer}.  GEF viewers use {@code EditPart}s as controllers in
 * the Model-View-Controller (MVC) paradigm.  These edit parts take elements of
 * a model and construct visual representations of them using {@code IFigure}s.
 * 
 * @see GraphicalViewer
 * @see EditPart
 * @see IFigure
 * @author Jeffrey K Czyz
 */
public interface IGraphicalJiveView extends IJiveView {

	/**
	 * Returns the {@code GraphicalViewer} used by the view part to display its
	 * input.
	 * 
	 * @return the view's graphical viewer
	 */
	public GraphicalViewer getViewer();
}
