package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;

import edu.bsu.cs.jive.contour.ContourModel;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph.ContourGraph;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph.ContourGraphPosition;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph.ContourGraph.Section;

//TODO Expand the code to use a custom layout and re-write docs comments
/**
 * A Draw2d {@code Figure} used to visualize a {@code ContourModel}.  Currently,
 * this figure uses a simple {@code ToolbarLayout} to arrange the root level
 * contours.
 * 
 * @see ContourModel
 * @author Jeffrey K Czyz
 */
public class ContourDiagramFigure extends Figure {
	
	private IFigure staticSection;
	
	private IFigure reachableSection;
	
	private IFigure unreachableSection;
	
	private Map<ContourGraphPosition, IFigure> positionToFigureMapping;
	
	/**
	 * Constructs the contour diagram.
	 */
	public ContourDiagramFigure() {
		super();
//		Border temp = new LineBorder(1);
		
		staticSection = new Figure();
		staticSection.setLayoutManager(new ToolbarLayout(true));
//		staticSection.setBorder(temp);
		
		reachableSection = new Figure();
		ToolbarLayout reachableSectionLayout = new ToolbarLayout(true);
		reachableSectionLayout.setSpacing(30);
		reachableSection.setLayoutManager(reachableSectionLayout);
//		reachableSection.setBorder(temp);
		
		unreachableSection = new Figure();
		ToolbarLayout unreachableSectionLayout = new ToolbarLayout(true);
		unreachableSectionLayout.setSpacing(30);
		unreachableSection.setLayoutManager(unreachableSectionLayout);
//		unreachableSection.setBorder(temp);
		
		positionToFigureMapping = new HashMap<ContourGraphPosition, IFigure>();
		
		ToolbarLayout layout = new ToolbarLayout(false);
		layout.setSpacing(30);
		setLayoutManager(layout);
		
		add(staticSection);
		add(reachableSection);
		add(unreachableSection);
	}
	
	public void add(IFigure f, Object constraint, int index) {
		if (f instanceof ContourFigure) {
			assert constraint instanceof ContourGraphPosition;
			ContourFigure figure = (ContourFigure) f;
			ContourGraphPosition position = (ContourGraphPosition) constraint;
			addContourFigure(figure, position);
		}
		else {
			super.add(f, constraint, index);
		}
	}
	
	private void addContourFigure(ContourFigure figure, ContourGraphPosition position) {
		switch (position.getSection()) {
		case STATIC:
			staticSection.add(figure);
			break;
		case REACHABLE:
			addNonStaticFigure(figure, position);
			break;
		case UNREACHABLE:
			addNonStaticFigure(figure, position);
			break;
		}
	}
	
	private void addNonStaticFigure(ContourFigure figure, ContourGraphPosition position) {
		ensurePositionFigureExists(position);
		if (positionToFigureMapping.containsKey(position)) {
			IFigure cellFigure = positionToFigureMapping.get(position);
			cellFigure.add(figure);
//			layerFigure.setVisible(true);
			
//			IFigure column = layerFigure.getParent();
//			FlowLayout layout = (FlowLayout) column.getLayoutManager();
//			layout.setMinorSpacing(20);
		}
		else {
			throw new IllegalStateException("The layer for the following position does not exist:  " + position);
		}
	}
	
	private void ensurePositionFigureExists(ContourGraphPosition position) {
		Section section = position.getSection();
		IFigure sectionFigure = section == Section.REACHABLE ? reachableSection : unreachableSection;
		int column = position.getColumn();
		while (column >= sectionFigure.getChildren().size()) {
			IFigure columnFigure = new Figure();
//			columnFigure.setBorder(new LineBorder(1));
			FlowLayout layout = new FlowLayout(false);
			// TODO Set layout properties
			layout.setMinorSpacing(20);
			layout.setStretchMinorAxis(true);
			columnFigure.setLayoutManager(layout);
			sectionFigure.add(columnFigure);
		}
		
		IFigure columnFigure = (IFigure) sectionFigure.getChildren().get(column);
		int layer = position.getLayer();
		while (layer >= columnFigure.getChildren().size()) {
			IFigure layerFigure = new Figure();
//			layerFigure.setBorder(new LineBorder(1));
			FlowLayout layout = new FlowLayout(true);
			// TODO Set layout properties
			layout.setMinorSpacing(20);
			layout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
			layerFigure.setLayoutManager(layout);
			columnFigure.add(layerFigure);
		}
		
		IFigure layerFigure = (IFigure) columnFigure.getChildren().get(layer);
		int cell = position.getCell();
		while (cell >= layerFigure.getChildren().size()) {
			IFigure cellFigure = new Figure();
			cellFigure.setLayoutManager(new FlowLayout());
			layerFigure.add(cellFigure);
		}
		
		IFigure cellFigure = (IFigure) layerFigure.getChildren().get(cell);
		positionToFigureMapping.put(position, cellFigure);
	}
	
	public void remove(IFigure figure) {
		if (figure instanceof ContourFigure) {
			IFigure parentFigure = figure.getParent();
			parentFigure.remove(figure);
			
			if (parentFigure != staticSection) {
				IFigure cellFigure = parentFigure;				
				IFigure layerFigure = cellFigure.getParent();
				layerFigure.remove(cellFigure);
				if (layerFigure.getChildren().size() == 0) {
					IFigure columnFigure = layerFigure.getParent();
					columnFigure.remove(layerFigure);
					if (columnFigure.getChildren().size() == 0) {
						IFigure sectionFigure = columnFigure.getParent();
						sectionFigure.remove(columnFigure);
					}
					
					ContourGraphPosition position = null;
					for (Entry<ContourGraphPosition, IFigure> e : positionToFigureMapping.entrySet()) {
						if (e.getValue() == cellFigure) {
							position = e.getKey();
							break;
						}
					}
					
					if (position != null) {
//						System.out.println("Removing layer at position " + position);
						positionToFigureMapping.remove(position);
					}
					else {
						throw new IllegalStateException("No layer exists for the position:  " + position);
					}
				}
			}
			
//			IFigure column = parent.getParent();
//			for (Object o : column.getChildren()) {
//				IFigure layer = (IFigure) o;
//				if (layer.getChildren().size() != 0) {
//					return;
//				}
//			}
//			
//			FlowLayout layout = (FlowLayout) column.getLayoutManager();
//			layout.setMinorSpacing(0);
		}
		else {
			super.remove(figure);
		}
	}
}
