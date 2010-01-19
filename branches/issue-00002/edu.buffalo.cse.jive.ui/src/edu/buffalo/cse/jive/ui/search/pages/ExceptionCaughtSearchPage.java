package edu.buffalo.cse.jive.ui.search.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.buffalo.cse.jive.ui.search.AbstractJiveSearchPage;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchPattern;
import edu.buffalo.cse.jive.ui.search.SelectionTokenizer;
import edu.buffalo.cse.jive.ui.search.queries.ExceptionCaughtSearchQuery;

/**
 * An {@code IJiveSearchPage} used to create an
 * {@code ExceptionCaughtSearchQuery}.  The page contains input fields for an
 * optional class, instance number, and method representing where the exception
 * was caught and an optional name for the exception caught.
 * <p>
 * When a class name is given, only exceptions caught by the class are matched.
 * Providing an instance number or method name limits the matches to exceptions
 * caught by the particular instance or in the given method, respectively.  When
 * an exception name is supplied, matches are limited to exceptions starting
 * with that name.
 * 
 * @see ExceptionCaughtSearchQuery
 * @author Jeffrey K Czyz
 */
public class ExceptionCaughtSearchPage extends AbstractJiveSearchPage {

	/**
	 * The top-level control returned by {@link #getControl()}.
	 */
	protected Composite control;
	
	/**
	 * An optional fully-qualified class name in which an exception is caught. 
	 */
	protected Text classText;
	
	/**
	 * An optional instance number specifying what instance of the class should
	 * be checked.
	 */
	protected Text instanceText;
	
	/**
	 * An optional method name specifying in which method an exception was caught.
	 */
	protected Text methodText;
	
	/**
	 * An optional fully-qualified class name of the exception.
	 */
	protected Text exceptionText;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		
		Label label = new Label(control, SWT.NONE);
		label.setText("Class name:");
		classText = new Text(control, SWT.BORDER);
		classText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Instance number:");
		instanceText = new Text(control, SWT.BORDER);
		instanceText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Method name:");
		methodText = new Text(control, SWT.BORDER);
		methodText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Exception name:");
		exceptionText = new Text(control, SWT.BORDER);
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
		
		return new ExceptionCaughtSearchQuery(pattern, exceptionText.getText());
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
		String className = classText.getText();
		String instanceNumber = instanceText.getText();
		if (!instanceNumber.equals("")) {
			if (!instanceNumber.matches("[1-9]\\d*")) {
				return false;
			}
			else {
				if (className.equals("")) {
					return false;
				}
			}
		}
		
		if (!methodText.getText().equals("") && className.equals("")) {
			return false;
		}
		
		return true;
	}

}
