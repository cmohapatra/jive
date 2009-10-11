package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourModel;

/**
 * A Draw2d {@code Figure} used to visulaize a {@code Contour} from a
 * {@code ContourModel}.
 * 
 * @see Contour
 * @see ContourModel
 * @author Jeffrey K Czyz
 */
public class ContourFigure extends Figure {
	
	public enum State { FULL, NODE, OUTLINE, EMPTY }
	
	public static interface Importer {
		public String provideText();
		public Image provideIcon();
		public String provideToolTipText();
		public Image provideToolTipIcon();
	}
	
	/**
	 * The border used for the overall contour figure.
	 */
	protected static final Border CONTOUR_BORDER = new LineBorder(1);
	
	/**
	 * The border used around the contour's label.
	 */
//	protected static final Border LABEL_BORDER = new MarginBorder(2, 2, 0, 2);
	protected static final Border LABEL_BORDER = new CompoundBorder(new CustomLineBorder(0, 0, 1, 0), new MarginBorder(2, 2, 0, 2));
	
	/**
	 * The border used around the contour's child compartment.
	 */
//	protected static final Border CHILD_COMPARTMENT_BORDER = new CompoundBorder(new TopLineBorder(), new MarginBorder(4));
	protected static final Border CHILD_COMPARTMENT_BORDER = new MarginBorder(4);
	
	/**
	 * The background color for the contour.
	 */
	protected static final Color CONTOUR_BACKGROUND_COLOR = new Color(null, 255, 255, 225);
	
	/**
	 * The background color for the contour's label.
	 */
	protected static final Color LABEL_BACKGROUND_COLOR = new Color(null, 255, 255, 206);
	
	/**
	 * The contour's label, which typically represents the contour ID.
	 */
	private Label label;
	
	/**
	 * The figure which visualizes the contour's members.
	 */
	private ContourMemberTableFigure memberTable;
	
	/**
	 * The figure containing visualizations of the contour's children.
	 */
	private Figure childCompartment;
	
	// TODO Remove this and JavaContourFigure (and subclasses)
	public ContourFigure() {
		
	}
	
//	public ContourFigure(Importer importer) {
//		super();
//		initializeContour();
//		initializeLabel(importer);
//		initializeMemberTable();
//		initializeCompartment();
//		add(label);
//		add(memberTable);
//		add(childCompartment);
//	}
	
	// TODO Re-think stacked contours 
	/**
	 * Constructs the contour figure.
	 */
	public ContourFigure(State state, Importer importer) {
		super();
		switch (state) {
		case FULL:
			initializeFull(importer);
			return;
		case NODE:
			initializeNode(importer);
			return;
		case OUTLINE:	
			initializeOutline(importer);
			return;
		case EMPTY:
			initializeEmpty(importer);
			return;
		}

		throw new IllegalStateException("State " + state + " is not implemented.");
	}
	
	private void initializeFull(Importer importer) {
		initializeContour();
		initializeLabel(importer);
		initializeMemberTable();
		initializeCompartment();
		add(label);
		add(memberTable);
		add(childCompartment);
	}
	
	private void initializeNode(Importer importer) {
		FlowLayout layout = new FlowLayout(false);
		setLayoutManager(layout);
		
		initializeLabel(importer);
		label.setBorder(null);
		label.setBackgroundColor(null);
		
		initializeCompartment();
		childCompartment.setBorder(null);
		
		add(label);
		add(childCompartment);
	}
	
	private void initializeOutline(Importer importer) {
		FlowLayout layout = new FlowLayout();
		setLayoutManager(layout);
		setBorder(CONTOUR_BORDER);
		setBackgroundColor(CONTOUR_BACKGROUND_COLOR);
		setOpaque(true);
		initializeCompartment();
		add(childCompartment);
	}
	
	private void initializeEmpty(Importer importer) {
		FlowLayout layout = new FlowLayout();
		setLayoutManager(layout);
		initializeCompartment();
		childCompartment.setBorder(null);
		add(childCompartment);
	}
	
	/**
	 * Initializes the overall contour figure.  This method is called from
	 * {@link #initialize()}.
	 */
	protected void initializeContour() {
		ToolbarLayout layout = new ToolbarLayout(false);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setOpaque(true);
		setBorder(CONTOUR_BORDER);
		setBackgroundColor(CONTOUR_BACKGROUND_COLOR);
	}
	
//	/**
//	 * Initializes the contour figure's label.  This method is called from
//	 * {@link #initialize()}.
//	 */
	protected void initializeLabel(Importer importer) {
		label = new Label(importer.provideText(), importer.provideIcon());
		label.setOpaque(true);
		label.setToolTip(new Label(importer.provideToolTipText(), importer.provideToolTipIcon()));
		label.setBorder(LABEL_BORDER);
		label.setBackgroundColor(LABEL_BACKGROUND_COLOR);
		label.setIconAlignment(PositionConstants.BOTTOM);
		label.setLabelAlignment(PositionConstants.LEFT);
	}
	
	/**
	 * Initializes the contour figure's member table.  This method is called
	 * from {@link #initialize()}.
	 */
	protected void initializeMemberTable() {
		memberTable = new ContourMemberTableFigure();
	}
	
	/**
	 * Initializes the contour figure's child compartment.  This method is
	 * called from {@link #initialize()}.
	 */
	protected void initializeCompartment() {
		ToolbarLayout layout = new ToolbarLayout(true); // TODO determine if we want horizontal or vertical arrangement
		layout.setSpacing(4);
		childCompartment = new Figure();
		childCompartment.setLayoutManager(layout);
		childCompartment.setOpaque(false);
		childCompartment.setBorder(CHILD_COMPARTMENT_BORDER);
	}
	
	public ContourMemberTableFigure getMemberTable() {
		return memberTable;
	}
	
	// TODO Determine if this is still need (because we are not overriding the edit
	// part's getContentPane() method for the time being.
	/**
	 * Returns the figure used to contain figures representing the children of
	 * the contour.
	 * 
	 * @return the figure holding contour children
	 */
	public IFigure getChildCompartment() {
		return childCompartment;
	}

	// TODO See above
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#add(org.eclipse.draw2d.IFigure, java.lang.Object, int)
	 */
	public void add(IFigure figure, Object constraint, int index) {
		if (figure == label || figure == memberTable || figure == childCompartment) {
			super.add(figure, constraint, index);
		}
		else {
			childCompartment.add(figure, constraint, index);
		}
	}
	
	// TODO See above
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#remove(org.eclipse.draw2d.IFigure)
	 */
	public void remove(IFigure figure) {
		if (figure == label || figure == memberTable || figure == childCompartment) {
			super.remove(figure);
		}
		else {
			childCompartment.remove(figure);
		}
	}
	
	/**
	 * A border that consists of one line across the top edge.
	 * 
	 * @author Jeffrey K Czyz
	 */
	static class TopLineBorder extends AbstractBorder {
		
		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.Border#getInsets(org.eclipse.draw2d.IFigure)
		 */
		public Insets getInsets(IFigure figure) {
			return new Insets(1, 0, 0, 0);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
		 */
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.setLineWidth(1);
			Rectangle paintArea = getPaintRectangle(figure, insets);
			graphics.drawLine(paintArea.getTopLeft(), paintArea.getTopRight());
		}
	}
	
//	/**
//	 * Returns the image used for the contour figure's label.  This method is
//	 * is called from {@link #initializeLabel()}.
//	 * 
//	 * @return the image for the contour figure's label
//	 */
//	protected abstract Image getLabelIcon();
//	
//	/**
//	 * Returns the string used for the contour figure's label.  This method is
//	 * called from {@link #initializeLabel()}.
//	 * 
//	 * @return the string for the contour figure's label
//	 */
//	protected abstract String getLabelText();
//	
//	/**
//	 * Returns the image used for the tool tip of the contour figure's label.
//	 * This method is is called from {@link #initializeLabel()}.
//	 * 
//	 * @return the image for the tool tip of the contour figure's label
//	 */
//	protected abstract Image getToolTipIcon();
//	
//	/**
//	 * Returns the string used for the tool tip of the contour figure's label.
//	 * This method is called from {@link #initializeLabel()}.
//	 * 
//	 * @return the string for the tool tip of the contour figure's label
//	 */
//	protected abstract String getToolTipText();
}
