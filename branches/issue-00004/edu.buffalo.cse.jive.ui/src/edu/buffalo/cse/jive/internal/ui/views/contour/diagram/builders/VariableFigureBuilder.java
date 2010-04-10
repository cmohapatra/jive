package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders;

import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Type;
import edu.bsu.cs.jive.contour.Value;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourMemberTableFigure;

public class VariableFigureBuilder implements ContourMemberTableFigure.MemberImporter {
	
	private static char TYPE_TEXT_DELIMITER = '.';
	
	private static char VALUE_TEXT_DELIMITER = '.';

	private String identifierText;
	
	private String typeText;
	
	private String typeToolTipText;
	
	private String valueText;
	
	private String valueToolTipText;
	
	public VariableFigureBuilder(ContourMember.Variable variable) {
		identifierText = variable.id().toString();
		
		typeToolTipText = variable.type().toString();
		typeText = computeText(typeToolTipText, TYPE_TEXT_DELIMITER);
		
		valueToolTipText = variable.value().toString();
		if (canShortenValueText(variable)) {
			valueText = computeText(valueToolTipText, VALUE_TEXT_DELIMITER);
		}
		else {
			valueText = valueToolTipText;
		}
	}
	
	private String computeText(String text, char delimiter) {
		int index = text.lastIndexOf(delimiter);
		return text.substring(index + 1);
	}
	
	private boolean canShortenValueText(ContourMember.Variable member) {
		Value value = member.value();
		if (value instanceof Value.ContourReference) {
			return true;
		}
		else if (value instanceof Value.Encoded) {
			String type = member.type().toString();
			if (type.equals("float")) {
				return false;
			}
			else if (type.equals("double")) {
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
	
	public String provideIdentifierText() {
		return identifierText;
	}
	
	public Image provideIdentifierIcon() {
		return null;
	}
	
	public String provideTypeText() {
		return typeText;
	}
	
	public Image provideTypeIcon() {
		return null;
	}
	
	public String provideTypeToolTipText() {
		return typeToolTipText;
	}
	
	public Image provideTypeToolTipIcon() {
		return null;
	}
	
	public String provideValueText() {
		return valueText;
	}
	
	public Image provideValueIcon() {
		return null;
	}
	
	public String provideValueToolTipText() {
		return valueToolTipText;
	}
	
	public Image provideValueToolTipIcon() {
		return null;
	}
}
