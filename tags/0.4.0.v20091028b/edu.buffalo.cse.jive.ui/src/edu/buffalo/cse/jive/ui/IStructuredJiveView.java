package edu.buffalo.cse.jive.ui;

import org.eclipse.jface.viewers.StructuredViewer;

/**
 * A view part used to present {@code IJiveDebugTarget}s using a JFace
 * {@code StructuredViewer}.  JFace viewers use content providers to obtain
 * model elements and label providers to obtain the visual representation of
 * those elements.
 * 
 * @see StructuredViewer
 * @author Jeffrey K Czyz
 */
public interface IStructuredJiveView extends IJiveView {

	/**
	 * Returns the {@code StructuredViewer} used by the view part to display its
	 * input.
	 * 
	 * @return the viewer used by the view part
	 */
	public StructuredViewer getViewer();
	
}
