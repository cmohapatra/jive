package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class CustomLineBorder extends AbstractBorder {
	
	private int top;
	
	private int left;
	
	private int bottom;
	
	private int right;

	public CustomLineBorder(int top, int left, int bottom, int right) {
//		super(Math.max(Math.max(top, bottom), Math.max(left, right)));
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#getInsets(org.eclipse.draw2d.IFigure)
	 */
	public Insets getInsets(IFigure figure) {
		return new Insets(top, left, bottom, right);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
	 */
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		Rectangle paintArea = getPaintRectangle(figure, insets);
		graphics.setForegroundColor(ColorConstants.gray);
		
		if (top > 0) {
			graphics.setLineWidth(top);
			graphics.drawLine(paintArea.getTopLeft(), paintArea.getTopRight());
		}
		
		if (left > 0) {
			graphics.setLineWidth(left);
			graphics.drawLine(paintArea.getTopLeft(), paintArea.getBottomLeft());
		}
		
		if (bottom > 0) {
			graphics.setLineWidth(top);
			Point bottomLeft = paintArea.getBottomLeft();
			bottomLeft.y--;
			Point bottomRight = paintArea.getBottomRight();
			bottomRight.y--;
			graphics.drawLine(bottomLeft, bottomRight);
		}
		
		if (right > 0) {
			graphics.setLineWidth(right);
			Point topRight = paintArea.getTopRight();
			topRight.x--;
			Point bottomRight = paintArea.getBottomRight();
			bottomRight.x--;
			graphics.drawLine(topRight, bottomRight);
		}
	}
}
