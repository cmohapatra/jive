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
import edu.buffalo.cse.jive.ui.search.queries.ObjectCreatedSearchQuery;

/**
 * An {@code IJiveSearchPage} used to create a
 * {@code ObjectCreatedSearchQuery}.  The page contains input fields for a
 * class and an optional instance number.
 * <p>
 * When an instance number is given, creation of only that particular instance
 * of the class are included in the result.  When it is left out, creation of
 * all instances of the class are included in the result.
 * 
 * @see ObjectCreatedSearchQuery
 * @author Jeffrey K Czyz
 */
public class ObjectCreatedSearchPage extends AbstractJiveSearchPage {

	/**
	 * The top-level control returned by {@link #getControl()}.
	 */
	protected Composite control;
	
	/**
	 * A fully-qualified class of the object created.
	 */
	protected Text classText;
	
	/**
	 * An optional instance number specifying what instance of the class should
	 * be checked.
	 */
	protected Text instanceText;
	
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
				return "";
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.ui.search.JiveSearchPattern.Importer#provideVariableName()
			 */
			public String provideVariableName() {
				return "";
			}
		});
		
		return new ObjectCreatedSearchQuery(pattern);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#initializeInput(org.eclipse.jface.viewers.ISelection)
	 */
	public void initializeInput(ISelection selection) {
		SelectionTokenizer parser = new SelectionTokenizer(selection);
		classText.setText(parser.getClassName());
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#isInputValid()
	 */
	public boolean isInputValid() {
		if (classText.getText().equals("")) {
			return false;
		}
		
		String instanceNumber = instanceText.getText();
		if (!instanceNumber.equals("") && !instanceNumber.matches("[1-9]\\d*")) {
			return false;
		}
		else {
			return true;
		}
	}
}
