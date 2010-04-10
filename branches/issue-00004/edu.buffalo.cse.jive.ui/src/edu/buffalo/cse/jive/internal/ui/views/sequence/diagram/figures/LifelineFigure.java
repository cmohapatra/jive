package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;

public class LifelineFigure extends Figure {
	
	private static final Border HEAD_BORDER = new CompoundBorder(new LineBorder(1), new MarginBorder(4));

	private static final int[] LINE_DASH_PATTERN = new int[] {10, 5};
	
	protected static final Color HEAD_BACKGROUND_COLOR = new Color(null, 255, 255, 206);
	
	private Map<Integer, Long> columnToLastEventNumberMap;
	
	private Map<ExecutionOccurrenceFigure, Integer> figureToColumnMap;
	
	private Label head;
	
	private IFigure childCompartment;
	
	private boolean hasSelfCalls;
	
	public LifelineFigure(ContourFigure.Importer importer) {
		figureToColumnMap = new HashMap<ExecutionOccurrenceFigure, Integer>();
		hasSelfCalls = false;
		initializeHead(importer);
//		initializeChildCompartment();
		initializeLifeline();
	}
	
	private void initializeHead(ContourFigure.Importer importer) {
		head = new Label(importer.provideText(), importer.provideIcon());
		Label tooltip = new Label(importer.provideToolTipText(), importer.provideToolTipIcon());
		head.setToolTip(tooltip);
		head.setOpaque(true);
		head.setBorder(HEAD_BORDER);
		head.setIconAlignment(PositionConstants.BOTTOM);
		head.setLabelAlignment(PositionConstants.LEFT);
		head.setBackgroundColor(HEAD_BACKGROUND_COLOR);
	}
	
	private void initializeLifeline() {
		setOpaque(false);
		setLayoutManager(new XYLayout());
		
		add(head, new Rectangle(0, 0, -1, -1));
	}
	
//	public void add(IFigure figure, Object constraint, int index) {
//		if (figure instanceof ExecutionOccurrenceFigure) {
//			int center = head.getPreferredSize().width / 2;
//			int offset = center - 4;
//			Rectangle location = (Rectangle) constraint;
//			location.x += offset;
//		}
//		
//		super.add(figure, constraint, index);
//	}
	
	public Dimension getLifelineHeadSize() {
		return head.getPreferredSize();
	}
	
//	private void initializeChildCompartment() {
//		childCompartment = new Figure();
//		XYLayout layout = new XYLayout();
//		childCompartment.setLayoutManager(layout);
////		childCompartment.setBorder(CHILD_COMPARTMENT_BORDER);
//	}
//	
//	private void initializeLifeline() {
//		ToolbarLayout layout = new ToolbarLayout(false);
//		layout.setSpacing(10);
//		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
//		setLayoutManager(layout);
//		
//		add(head);
//		add(childCompartment);
//	}
	
//	public void add(IFigure figure, Object constraint, int index) {
//		if (figure instanceof ExecutionOccurrenceFigure) {
//			ExecutionOccurrenceFigure executionFigure = (ExecutionOccurrenceFigure) figure;
//			int x = getHorizontalPosition(executionFigure);
//			int y = ((int) executionFigure.getStart()) * 3;
//			
//			if (hasSelfCalls) {
//				int width = head.getPreferredSize().width;
//				int center = width / 2;
//				x = x + width - 4;
//				System.out.println("width = " + width + ", center = " + center + ", x = " + x);
//			}
//			else {
//				x = 0;
//			}
//			
//			Rectangle position = new Rectangle(x, y, -1, -1);
//			childCompartment.add(figure, position);	
//		}
//		else {
//			super.add(figure, constraint, index);
//		}
//	}
//	
//	public void remove(IFigure figure) {
//		if (figure instanceof ExecutionOccurrenceFigure) {
//			childCompartment.remove(figure);
//		}
//		else {
//			super.remove(figure);
//		}
//	}
	
	public int getHorizontalPosition(ExecutionOccurrenceFigure figure) {
		int column = 0;
		
		List<IFigure> childList = childCompartment.getChildren();
		for (IFigure child : childList) {
			ExecutionOccurrenceFigure f = (ExecutionOccurrenceFigure) child;
			int tempColumn = figureToColumnMap.get(f);
			int start = f.getStart();
			if (figure.getStart() > start) {
				int end = f.getStart() + f.getLength();
				if ((figure.getStart() + figure.getLength()) <= end) {
					if (tempColumn >= column) {
						column = tempColumn + 1;
					}
				}
			}
		}
		
		if (!hasSelfCalls && column > 0) {
			hasSelfCalls = true;
			ToolbarLayout layout = (ToolbarLayout) getLayoutManager();
			layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		}
		
		figureToColumnMap.put(figure, column);
		return column * 12;
	}
	
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
//		graphics.setLineWidth(2);
//		graphics.setLineStyle(SWT.LINE_DASH);
		graphics.setLineDash(LINE_DASH_PATTERN);
		graphics.setForegroundColor(ColorConstants.lightGray);
		Rectangle bounds = getBounds();
		Point top = head.getBounds().getBottom();
		Point bottom = bounds.getBottom();
		bottom.x = top.x;
		graphics.drawLine(top, bottom);
	}
}
