package edu.buffalo.cse.jive.internal.ui.views.contour.diagram;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.Value;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourEditPart;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourReferenceEditPart;

/**
 * An {@code EditPartFactory} used to create {@code EditPart}s for elements in
 * the {@code ContourModel}.
 * 
 * @see ContourDiagramEditPart
 * @see ContourEditPart
 * @see ContourReferenceEditPart
 * @author Jeffrey K Czyz
 */
public class ContourModelEditPartFactory implements EditPartFactory {
	
	/**
	 * An empty edit part to be used when there are no targets being debugged.
	 */
	private static final EditPart EMPTY_EDIT_PART = new AbstractGraphicalEditPart() {
		
		/* (non-Javadoc)
		 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
		 */
		protected IFigure createFigure() {
			return new Figure();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
		 */
		protected void createEditPolicies() {
			// TODO Determine if this should be implemented
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		if (model == null) {
			return EMPTY_EDIT_PART;
		}
		
		if (model instanceof IJiveDebugTarget) {
			EditPart editPart = new ContourDiagramEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		if (model instanceof Contour) {
			EditPart editPart = new ContourEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
//		if (model instanceof ContourMember) {
//			EditPart editPart = new ContourMemberEditPart();
//			editPart.setModel(model);
//			return editPart;
//		}
		
		if (model instanceof Value.ContourReference) {
			EditPart editPart = new ContourReferenceEditPart();
			editPart.setModel(model);
			return editPart;
		}
		
		throw new IllegalArgumentException("Unknown element type:  " + model.getClass());
	}
}
