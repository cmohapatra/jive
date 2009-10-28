package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider;

/**
 * An abstract <code>Contour</code> exporter used to provide labels to an
 * <code>IStructuredContentProvider</code>.
 * 
 * @author Jeffrey K Czyz
 */
public abstract class AbstractContourLabelProvider extends LabelProvider implements IJiveLabelProvider, Contour.Exporter {
	
	/**
	 * A flag indicating whether or not the export was completed.
	 */
	private boolean fIsFinished;
	
	/**
	 * The contour ID representation.
	 */
	private String fContourID;
	
	// TODO Determine if this is even being used
	/**
	 * A list of members obtained from the contour member table.
	 */
	private List<ContourMember> fMemberList;
	
	/**
	 * Initializes the provider so it may be used to export another contour.
	 */
	protected void initialize() {
		fIsFinished = false;
		if (fMemberList != null) {
			fMemberList.clear();
		}
		fMemberList = new LinkedList<ContourMember>();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider#getText()
	 */
	public String getText() {
		if (!fIsFinished) {
			throw new IllegalStateException();
		}
		
		return fContourID;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider#getImage()
	 */
	public abstract Image getImage();

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.Contour.Exporter#addID(edu.bsu.cs.jive.util.ContourID)
	 */
	public void addID(ContourID id) {
		fContourID = id.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.Contour.Exporter#addMember(edu.bsu.cs.jive.contour.ContourMember)
	 */
	public void addMember(ContourMember member) {
		fMemberList.add(member);

	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.Contour.Exporter#exportFinished()
	 */
	public void exportFinished() {
		fIsFinished = true;
	}
}
