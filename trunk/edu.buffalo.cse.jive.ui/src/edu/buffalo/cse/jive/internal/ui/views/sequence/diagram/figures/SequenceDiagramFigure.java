package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

// TODO Provide an interface for allowing other layout mechanisms (more than setLayout)
/**
 * A Draw2d {@code Figure} used to visualize a {@code SequenceModel}.  The
 * children of a sequence diagram are {@code LifelineFigure}s.  Currently,
 * a {@code ToolbarLayout} is used to arrange the lifelines.
 * 
 * @author Jeffrey K Czyz
 * @author Nirupama Chakravarti
 *
 */
public class SequenceDiagramFigure extends Figure {

	private static final Border DIAGRAM_BORDER = new MarginBorder(5);
	
	private static final int[] LINE_DASH_PATTERN = new int[] {10, 5};
	
	private int currentEventNumber;
	
	/**
	 * Constructs the sequence diagram.
	 */
	public SequenceDiagramFigure() {
		super();
		currentEventNumber = 1;
		ToolbarLayout layout = new ToolbarLayout(true);
		layout.setStretchMinorAxis(true);
		layout.setSpacing(10);
		setLayoutManager(layout);
		setBorder(DIAGRAM_BORDER);
	}
	
	public void setCurrentEventNumber(int eventNumber) {
		currentEventNumber = eventNumber;
	}
	
	protected void paintBorder(Graphics graphics) {
		super.paintBorder(graphics);
		
		List<IFigure> children = getChildren();
		if (!children.isEmpty()) {
			LifelineFigure child = (LifelineFigure) children.get(0);
			Dimension headDimension = child.getLifelineHeadSize();
			
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			int eventHeight = prefs.getInt(IJiveUIConstants.PREF_EVENT_HEIGHT);
			int y = eventHeight * currentEventNumber + headDimension.height + 10;
			
			Point p1 = getClientArea().getTopLeft();
			p1.y += y;
			Point p2 = getClientArea().getTopRight();
			p2.y += y;
			
			graphics.setLineDash(LINE_DASH_PATTERN);
//			graphics.setLineStyle(Graphics.LINE_DASH);
			graphics.drawLine(p1, p2);
		}		
	}
}
