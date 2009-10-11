package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;

/**
 * * A Draw2d {@code Figure} used to visualize a {@code ExecutionOccurrence} from a
 * {@code SequenceModel}.
 * @author Nirupama Chakravarti
 *
 */
public class ExecutionOccurrenceFigure extends RectangleFigure {
	
	private int start;
	
	private int length;
	
	/**
	 * Constructs the execution occurrence figure.
	 */
	public ExecutionOccurrenceFigure(ContourFigure.Importer importer, int start, int length, Color color) {
		super();
		this.start = start;
		this.length = length;
		
		Label tooltip = new Label(importer.provideToolTipText(), importer.provideToolTipIcon());
		setToolTip(tooltip);
		
		int width = JiveUIPlugin.getDefault().getActivationWidth();
		int height = (int) length * JiveUIPlugin.getDefault().getEventHeight();
		setBounds(new Rectangle(-1, -1, width, height));
		setOpaque(true);
		setOutline(false);
		setBackgroundColor(color);
		
		setLayoutManager(new XYLayout());
		add(new Figure(), new Rectangle(width, height, -1, -1)); // Ensures the figure has the correct dimensions
	}
	
	public int getStart() {
		return start;
	}
	
	public int getLength() {
		return length;
	}
}
