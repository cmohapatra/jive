package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;

/**
 * A base implementation of an {@code EditPart} for non-connection elements in
 * the {@code ContourModel}.  This class provides utility methods for looking
 * up edit parts associated with elements in the model.
 * 
 * @see ContourDiagramEditPart
 * @see ContourEditPart
 * @see Contour
 * @see Value.ContourReference
 * @author Jeffrey K Czyz
 */
public abstract class AbstractContourModelEditPart extends AbstractGraphicalEditPart {
	
	// TODO Determine if this will be used at all (e.g. connection caching, etc.)
	/**
	 * Returns the {@code ContourDiagramEditPart} for the edit part.  This
	 * corresponds to the contents of the {@code RootEditPart}.
	 * 
	 * @return the contour diagram edit part of the edit part
	 */
	protected ContourDiagramEditPart getDiagramEditPart() {
		return (ContourDiagramEditPart) getRoot().getContents();
	}
	
	/**
	 * Returns the {@code ContourEditPart} associated with the supplied contour.
	 * 
	 * @param contour the contour whose edit part will be returned
	 * @return the contour edit part for the contour, or <code>null</code> if
	 *         none exist
	 */
	protected ContourEditPart getContourEditPart(Contour contour) {
		return (ContourEditPart) getViewer().getEditPartRegistry().get(contour);
	}
	
//	/**
//	 * Returns the {@code ContourEditPart} associated with the contour referred
//	 * to by the supplied contour reference. 
//	 * 
//	 * @param model the model containing the contour reference
//	 * @param ref the contour reference whose edit part will be returned
//	 * @return the contour edit part for the contour reference, or
//	 *         <code>null</code> if none exist
//	 */
//	protected ContourEditPart getContourEditPart(ContourModel model, Value.ContourReference ref) {
//		ReentrantLock modelLock = model.getModelLock();
//		modelLock.lock();
//		try {
//			Contour contour = model.getContour(ref.getContourID());
//			return getContourEditPart(contour);
//		}
//		finally {
//			modelLock.unlock();
//		}
//	}
}
