package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

public class ContourMemberTableFigure extends Figure {
	
	public static interface MemberImporter {
		public String provideIdentifierText();
		public Image provideIdentifierIcon();
		
		public String provideTypeText();
		public Image provideTypeIcon();
		
		public String provideTypeToolTipText();
		public Image provideTypeToolTipIcon();
		
		public String provideValueText();
		public Image provideValueIcon();
		
		public String provideValueToolTipText();
		public Image provideValueToolTipIcon();
	}
	
	private static final Border IDENTIFIER_COLUMN_BORDER = new CompoundBorder(new CustomLineBorder(0, 0, 0, 0), new MarginBorder(0));
	
	private static final Border TYPE_COLUMN_BORDER = new CompoundBorder(new CustomLineBorder(0, 1, 0, 0), new MarginBorder(0));

	private static final Border VALUE_COLUMN_BORDER = new CompoundBorder(new CustomLineBorder(0, 1, 0, 0), new MarginBorder(0));
	
	private static final Border CELL_BORDER = new CompoundBorder(new CustomLineBorder(0, 0, 1, 0), new MarginBorder(1, 2, 1, 2));
	
	private IFigure identifierColumn;
	
	private IFigure typeColumn;
	
	private IFigure valueColumn;
	
	public ContourMemberTableFigure() {
		identifierColumn = new Figure();
		typeColumn = new Figure();
		valueColumn = new Figure();
		
		identifierColumn.setBorder(IDENTIFIER_COLUMN_BORDER);
		typeColumn.setBorder(TYPE_COLUMN_BORDER);
		valueColumn.setBorder(VALUE_COLUMN_BORDER);
		
		ToolbarLayout identifierLayout = new ToolbarLayout(false);
		identifierLayout.setStretchMinorAxis(true);
		identifierColumn.setLayoutManager(identifierLayout);
		
		ToolbarLayout typeLayout = new ToolbarLayout(false);
		typeLayout.setStretchMinorAxis(true);
		typeColumn.setLayoutManager(typeLayout);
		
		ToolbarLayout valueLayout = new ToolbarLayout(false);
		valueLayout.setStretchMinorAxis(true);
		valueColumn.setLayoutManager(valueLayout);
		
		BorderLayout layout = new BorderLayout();
		setLayoutManager(layout);
		add(identifierColumn, BorderLayout.LEFT);
		add(typeColumn, BorderLayout.CENTER);
		add(valueColumn, BorderLayout.RIGHT);
	}
	
	public void addMember(MemberImporter importer) {
		identifierColumn.add(createIdentifierLabel(importer));
		typeColumn.add(createTypeLabel(importer));
		valueColumn.add(createValueLabel(importer));
	}
	
	private Label createIdentifierLabel(MemberImporter importer) {
		Label result = new Label(importer.provideIdentifierText(), importer.provideIdentifierIcon());
		result.setBorder(CELL_BORDER);
		result.setLabelAlignment(PositionConstants.LEFT);
		return result;
	}
	
	private Label createTypeLabel(MemberImporter importer) {
		Label result = new Label(importer.provideTypeText(), importer.provideTypeIcon());
		result.setBorder(CELL_BORDER);
		result.setLabelAlignment(PositionConstants.LEFT);
		result.setToolTip(new Label(importer.provideTypeToolTipText(), importer.provideTypeToolTipIcon()));
		return result;
	}
	
	private Label createValueLabel(MemberImporter importer) {
		Label result = new Label(importer.provideValueText(), importer.provideValueIcon());
		result.setBorder(CELL_BORDER);
		result.setLabelAlignment(PositionConstants.LEFT);
		result.setToolTip(new Label(importer.provideValueToolTipText(), importer.provideValueToolTipIcon()));
		result.setTextPlacement(PositionConstants.WEST);
		return result;
	}
	
	public void updateMember(int index, MemberImporter importer) {
		assert index < valueColumn.getChildren().size();
		Label label = (Label) valueColumn.getChildren().get(index);
		label.setText(importer.provideValueText());
		label.setIcon(importer.provideValueIcon());
		Label toolTip = (Label) label.getToolTip();
		toolTip.setText(importer.provideValueToolTipText());
		toolTip.setIcon(importer.provideValueToolTipIcon());
	}
	
	public Label getValueLabel(int index) {
		assert index < valueColumn.getChildren().size();
		return (Label) valueColumn.getChildren().get(index);
	}
}
