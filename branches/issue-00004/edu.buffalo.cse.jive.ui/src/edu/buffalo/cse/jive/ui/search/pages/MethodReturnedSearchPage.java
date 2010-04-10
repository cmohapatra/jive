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
import edu.buffalo.cse.jive.ui.search.queries.MethodReturnedSearchQuery;

/**
 * An {@code IJiveSearchPage} used to create a
 * {@code MethodReturnedSearchQuery}.  The page contains input fields for a
 * class, an optional instance number, a method name, and an optional return
 * value condition.
 * <p>
 * When an instance number is given, returns only on that particular instance of
 * the class are included in the result.  When it is left out, returns on any
 * instance of the class are included in the result.  Additionally, if a
 * condition on the return value is given, then only returns meeting this
 * condition will be included in the result.
 * 
 * @see MethodReturnedSearchQuery
 * @author Jeffrey K Czyz
 */
public class MethodReturnedSearchPage extends AbstractJiveSearchPage {

	/**
	 * The top-level control returned by {@link #getControl()}.
	 */
	protected Composite control;
	
	/**
	 * A fully-qualified class name from which the method is returned.
	 */
	protected Text classText;
	
	/**
	 * An optional instance number specifying what instance of the class should
	 * be checked.
	 */
	protected Text instanceText;
	
	/**
	 * A method name specifying what method on the class should be checked.
	 */
	protected Text methodText;
	
	/**
	 * A relational operator selector.
	 */
	protected Combo operatorCombo;
	
	/**
	 * A value to be used on the right side of the operator.
	 */
	protected Text returnValueText;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		
		Label label = new Label(control, SWT.NONE);
		label.setText("Class name:");
		classText = new Text(control, SWT.BORDER);
		
		label = new Label(control, SWT.NONE);
		label.setText("Instance number:");
		instanceText = new Text(control, SWT.BORDER);
		instanceText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Method name:");
		methodText = new Text(control, SWT.BORDER);
		
		label = new Label(control, SWT.NONE);
		label.setText("Return value is:");
		operatorCombo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		operatorCombo.setItems(new String[] {
			RelationalOperator.NONE.toString(),
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
		label.setText("Value:");
		returnValueText = new Text(control, SWT.BORDER);
		returnValueText.addModifyListener(this);
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
		final String className = classText.getText();
		final String instanceNumber = instanceText.getText();
		final String methodName = methodText.getText();
		
		JiveSearchPattern pattern = JiveSearchPattern.createPattern(new JiveSearchPattern.Importer() {

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
				return methodName;
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideVariableName()
			 */
			public String provideVariableName() {
				return "";
			}
		});
		
		RelationalOperator operator = RelationalOperator.fromString(operatorCombo.getText());
		
		return new MethodReturnedSearchQuery(pattern, operator, returnValueText.getText());
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#initializeInput(org.eclipse.jface.viewers.ISelection)
	 */
	public void initializeInput(ISelection selection) {
		SelectionTokenizer parser = new SelectionTokenizer(selection);
		classText.setText(parser.getClassName());
		methodText.setText(parser.getMethodName());
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#isInputValid()
	 */
	public boolean isInputValid() {
		RelationalOperator operator = RelationalOperator.fromString(operatorCombo.getText());
		returnValueText.setEnabled(operator != RelationalOperator.NONE);
		
		if (classText.getText().equals("")) {
			return false;
		}
		
		String instanceNumber = instanceText.getText();
		if (!instanceNumber.equals("") && !instanceNumber.matches("[1-9]\\d*")) {
			return false;
		}
		
		if (methodText.getText().equals("")) {
			return false;
		}
		
		if (operator == RelationalOperator.NONE) {
			return true;
		}
		
		return !returnValueText.getText().equals("");
	}

}
