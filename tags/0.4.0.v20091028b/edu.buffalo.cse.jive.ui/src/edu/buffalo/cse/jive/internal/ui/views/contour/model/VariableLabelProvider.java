package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import edu.bsu.cs.jive.contour.ContourMember.Variable;
import edu.bsu.cs.jive.util.VariableID;

/**
 * A <code>ContourMember</code> exporter used to provide column labels for a
 * <code>Variable</code> to be used by an <code>ITableLabelProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public class VariableLabelProvider extends AbstractContourMemberLabelProvider implements Variable.Exporter {

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourMember.Variable.Exporter#addID(edu.bsu.cs.jive.util.VariableID)
	 */
	public void addID(VariableID id) {
		// TODO Determine if the id should be used
	}
}
