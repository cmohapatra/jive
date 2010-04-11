package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import org.eclipse.swt.graphics.Image;

import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.Type;
import edu.bsu.cs.jive.contour.Value;
import edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider;

/**
 * An abstract <code>ContourMember</code> exporter used to provide labels to an
 * <code>ITableLabelProvider</code>.  Column labels are provided for a member's
 * name, type, and value.
 * 
 * @author Jeffrey K Czyz
 */
public abstract class AbstractContourMemberLabelProvider implements IJiveTableRowLabelProvider, ContourMember.Exporter {

	/**
	 * The index of the member name column.
	 */
	private static final int NAME_COLUMN = 0;
	
	/**
	 * The index of the member type column.
	 */
	private static final int TYPE_COLUMN = 1;
	
	/**
	 * The index of the member value column.
	 */
	private static final int VALUE_COLUMN = 2;
	
	/**
	 * The member name representation.
	 */
	private String fName;
	
	/**
	 * The member type representation.
	 */
	private String fType;
	
	/**
	 * The member value representation.
	 */
	private String fValue;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider#getColumnImage(int)
	 */
	public Image getColumnImage(int columnIndex) {
		// do nothing
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider#getColumnText(int)
	 */
	public String getColumnText(int columnIndex) {
		switch (columnIndex) {
		case NAME_COLUMN:
			return fName;
		case TYPE_COLUMN:
			return fType;
		case VALUE_COLUMN:
			return fValue;
		default:
			throw new IllegalArgumentException("Invalid column index:  " + columnIndex);
		}
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourMember.Exporter#addName(java.lang.String)
	 */
	public void addName(String name) {
		fName = name;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourMember.Exporter#addType(edu.bsu.cs.jive.contour.Type)
	 */
	public void addType(Type type) {
		fType = type.toString();
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.contour.ContourMember.Exporter#addValue(edu.bsu.cs.jive.contour.Value)
	 */
	public void addValue(Value value) {
		fValue = value.toString();
	}
}
