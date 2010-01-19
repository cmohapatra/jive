package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph;

import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph.ContourGraph.Section;

public class ContourGraphPosition {

	private Section section;
	
	private int column;
	
	private int layer;
	
	private int cell;
	
	public ContourGraphPosition(Section section, int column, int layer, int cell) {
		this.section = section;
		this.column = column;
		this.layer = layer;
		this.cell = cell;
	}
	
	public Section getSection() {
		return section;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public int getCell() {
		return cell;
	}
	
	public boolean equals(Object o) {
		if (o instanceof ContourGraphPosition) {
			ContourGraphPosition position = (ContourGraphPosition) o;
			if (position.getSection() == section &&
					position.getColumn() == column &&
					position.getLayer() == layer &&
					position.getCell() == cell) {
				return true;
			}
		}
		
		return false;
	}
	
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + section.hashCode();
		hash = hash * 31 + column;
		hash = hash * 31 + layer;
		hash = hash * 31 + cell;
		return hash;
	}
	
	public String toString() {
		return "[section = " + section.toString() +
			", column = " + column +
			", layer = " + layer +
			", cell = " + cell + "]";
	}
}
