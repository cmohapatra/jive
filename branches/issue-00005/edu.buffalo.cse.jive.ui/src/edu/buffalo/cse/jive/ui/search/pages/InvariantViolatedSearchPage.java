package edu.buffalo.cse.jive.ui.search.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.buffalo.cse.jive.ui.search.AbstractJiveSearchPage;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchPattern;
import edu.buffalo.cse.jive.ui.search.RelationalOperator;
import edu.buffalo.cse.jive.ui.search.SelectionTokenizer;
import edu.buffalo.cse.jive.ui.search.queries.InvariantViolatedSearchQuery;

/**
 * An {@code IJiveSearchPage} used to create an
 * {@code InvariantViolatedSearchQuery}.  The page contains input fields for a
 * class, an optional instance number, and an invariant expression in the form
 * of:  <code>variableName relationalOperator variableName</code>.
 * <p>
 * When an instance number is given, the invariant is checked only for that
 * particular instance of the class.  When it is left out, the invariant is
 * checked for every instance of the class.
 * 
 * @see InvariantViolatedSearchQuery
 * @author Jeffrey K Czyz
 */
public class InvariantViolatedSearchPage extends AbstractJiveSearchPage {

	/**
	 * The top-level control returned by {@link #getControl()}.
	 */
	protected Composite control;
	
	/**
	 * A fully-qualified class name in which the invariant is being checked. 
	 */
	protected Text leftClassText;
	
	/**
	 * An optional instance number specifying what instance of the class should
	 * be checked.
	 */
	protected Text leftInstanceText;
	
	/**
	 * A variable of the class used on the left side of the operator.
	 */
	protected Text leftVariableText;
	
	/**
	 * A relational operator selector.
	 */
	protected Combo operatorCombo;
	
	/**
	 * A variable of the class used on the right side of the operator.
	 */
	protected Text rightVariableText;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		
		Label label = new Label(control, SWT.NONE);
		label.setText("Class name:");
		leftClassText = new Text(control, SWT.BORDER);
		leftClassText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Instance number:");
		leftInstanceText = new Text(control, SWT.BORDER);
		leftInstanceText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Variable name:");
		leftVariableText = new Text(control, SWT.BORDER);
		leftVariableText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("");
		operatorCombo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		operatorCombo.setItems(new String[] {
				RelationalOperator.EQUAL.toString(),
				RelationalOperator.NOT_EQUAL.toString(),
				RelationalOperator.LESS_THAN.toString(),
				RelationalOperator.LESS_THAN_OR_EQUAL.toString(),
				RelationalOperator.GREATER_THAN.toString(),
				RelationalOperator.GREATER_THAN_OR_EQUAL.toString()
		});
		operatorCombo.select(0);
		operatorCombo.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Variable name:");
		rightVariableText = new Text(control, SWT.BORDER);
		rightVariableText.addModifyListener(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#getControl()
	 */
	public Control getControl() {
		return control;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#createSearchQuery()
	 */
	public IJiveSearchQuery createSearchQuery() {
		JiveSearchPattern leftPattern = createPattern(
				leftClassText.getText(),
				leftInstanceText.getText(),
				leftVariableText.getText());
		
		RelationalOperator operator = RelationalOperator.fromString(operatorCombo.getText());
		
		JiveSearchPattern rightPattern = createPattern(
				leftClassText.getText(),
				leftInstanceText.getText(),
				rightVariableText.getText());
		
		return new InvariantViolatedSearchQuery(leftPattern, operator, rightPattern);
	}
	
	/**
	 * Creates a {@code JiveSearchPattern} with the supplied values.
	 * 
	 * @param className a fully-qualified class name
	 * @param instanceNumber an instance number
	 * @param variableName a variable name
	 * @return the pattern created
	 */
	protected JiveSearchPattern createPattern(final String className, final String instanceNumber, final String variableName) {
		return JiveSearchPattern.createPattern(new JiveSearchPattern.Importer() {

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideClassName()
			 */
			public String provideClassName() {
				return className;
			}
			
			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideInstanceNumber()
			 */
			public String provideInstanceNumber() {
				return instanceNumber;
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideMethodName()
			 */
			public String provideMethodName() {
				return "";
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideVariableName()
			 */
			public String provideVariableName() {
				return variableName;
			}
		});
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#initializeInput(org.eclipse.jface.viewers.ISelection)
	 */
	public void initializeInput(ISelection selection) {
		SelectionTokenizer parser = new SelectionTokenizer(selection);
		leftClassText.setText(parser.getClassName());
		
		// Only set the variable name if it is not a local variable
		if (!parser.getMethodName().equals("")) {
			leftVariableText.setText(parser.getVariableName());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#isInputValid()
	 */
	public boolean isInputValid() {
		if (leftClassText.getText().equals("")) {
			return false;
		}
		
		String leftInstanceNumber = leftInstanceText.getText();
		if (!leftInstanceNumber.equals("") && !leftInstanceNumber.matches("[1-9]\\d*")) {
			return false;
		}
		
		if (leftVariableText.getText().equals("")) {
			return false;
		}
		
		if (rightVariableText.getText().equals("")) {
			return false;
		}
		
		return true;
	}
}
