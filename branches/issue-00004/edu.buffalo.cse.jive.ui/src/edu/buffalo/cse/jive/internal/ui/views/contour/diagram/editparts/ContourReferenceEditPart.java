package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.graphics.Color;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager;

public class ContourReferenceEditPart extends AbstractConnectionEditPart {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		final PolylineConnection connection = new PolylineConnection();
		connection.setTargetDecoration(new PolygonDecoration());
		
		Value.ContourReference reference = (Value.ContourReference) getModel();
		if (reference.toString().indexOf("#") != -1) {
			connection.setLineWidth(2);
			
			ContourDiagramEditPart contents = (ContourDiagramEditPart) getRoot().getContents();
			final IJiveDebugTarget target = contents.getModel();
			ContourModel model = target.getContourModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				MethodContour contour = (MethodContour) model.getContour(reference.getContourID());
				contour.export(new MethodContour.Exporter() {

					public void addThread(ThreadID thread) {
						IThreadColorManager manager = JiveUIPlugin.getDefault().getThreadColorManager();
						Color c = manager.threadColor(target, thread);
						connection.setForegroundColor(c);
					}

					public void addID(ContourID id) {
						// do nothing
					}

					public void addMember(ContourMember member) {
						// do nothing
					}

					public void exportFinished() {
						// do nothing
					}
				});
			}
			finally {
				modelLock.unlock();
			}
		}
		else {
			connection.setLineWidth(1);
			connection.setForegroundColor(ColorConstants.gray);
		}
		
		return connection;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO Determine if anything should be done here
	}
}
