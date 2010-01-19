package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

public class EventOccurrenceFigure extends Figure {
	
	public interface Importer {
		public String provideToolTipText();
		public Image provideToolTipIcon();
	}

	public EventOccurrenceFigure(Importer importer) {
		super();
		
		int width = JiveUIPlugin.getDefault().getActivationWidth();
		int height = JiveUIPlugin.getDefault().getEventHeight();
		setBounds(new Rectangle(-1, -1, width, height));
		setOpaque(true);
		setBackgroundColor(ColorConstants.red);
		
		Label tooltip = new Label(importer.provideToolTipText(), importer.provideToolTipIcon());
		setToolTip(tooltip);
	}
}
