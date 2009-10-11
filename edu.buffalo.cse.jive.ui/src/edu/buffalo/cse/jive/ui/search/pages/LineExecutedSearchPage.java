package edu.buffalo.cse.jive.ui.search.pages;

import java.io.File;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import edu.buffalo.cse.jive.ui.search.AbstractJiveSearchPage;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;
import edu.buffalo.cse.jive.ui.search.queries.LineExecutedSearchQuery;

/**
 * An {@code IJiveSearchPage} used to create a
 * {@code LineExecutedSearchQuery}.  The page contains input fields for a
 * relative filename path and a line number.
 * 
 * @see LineExecutedSearchQuery
 * @author Jeffrey K Czyz
 */
public class LineExecutedSearchPage extends AbstractJiveSearchPage {

	/**
	 * The top-level control returned by {@link #getControl()}.
	 */
	protected Composite control;
	
	/**
	 * A relative path of the Java source file.
	 */
	protected Text sourcePathText;
	
	/**
	 * The line number of interest.
	 */
	protected Text lineNumberText;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		
		Label label = new Label(control, SWT.NONE);
		label.setText("Source path:");
		sourcePathText = new Text(control, SWT.BORDER);
		sourcePathText.addModifyListener(this);
		
		label = new Label(control, SWT.NONE);
		label.setText("Line number:");
		lineNumberText = new Text(control, SWT.BORDER);
		lineNumberText.addModifyListener(this);
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
		String sourcePath = sourcePathText.getText();
		int lineNumber = Integer.parseInt(lineNumberText.getText());
		return new LineExecutedSearchQuery(sourcePath, lineNumber);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#initializeInput(org.eclipse.jface.viewers.ISelection)
	 */
	public void initializeInput(ISelection selection) {
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				IEditorPart editor = activePage.getActiveEditor();
				IEditorInput editorInput = editor.getEditorInput();
				IJavaElement element = JavaUI.getEditorInputJavaElement(editorInput);
				if (element instanceof ICompilationUnit) {
					ICompilationUnit unit = (ICompilationUnit) element;
					String filename = unit.getElementName();
					String packageName = unit.getParent().getElementName();
					if (!packageName.equals("")) {
						packageName = packageName.replace('.', File.separatorChar);
						sourcePathText.setText(packageName + File.separatorChar + filename);
					}
					else { // default package
						sourcePathText.setText(packageName + filename);
					}
					
					int lineNumber = textSelection.getEndLine();
					if (lineNumber != -1) {
						lineNumberText.setText(Integer.toString(lineNumber + 1));
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchPage#isInputValid()
	 */
	public boolean isInputValid() {
		if (sourcePathText.getText().equals("")) {
			return false;
		}
		
		return lineNumberText.getText().matches("[1-9]\\d*");
	}

}
