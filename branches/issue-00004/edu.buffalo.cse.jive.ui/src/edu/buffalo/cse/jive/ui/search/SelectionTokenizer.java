package edu.buffalo.cse.jive.ui.search;

import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import edu.bsu.cs.jive.contour.Contour;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.ExecutionOccurrenceEditPart;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;

/**
 * Utility class used to tokenize an {@code ISelection} into various components
 * representing Java elements.  This class can be used to initialize query input
 * fields.
 * 
 * @author Jeffrey K Czyz
 */
public class SelectionTokenizer {
	
	/**
	 * The string representation of a constructor call used by
	 * {@code ContourID}s. 
	 */
	private static final String CONSTRUCTOR_STRING = "<init>"; 
	
	/**
	 * An empty string.
	 */
	private static final String EMPTY_STRING = "";
	
	/**
	 * The fully-qualified class name associated with the selection.
	 */
	private String className = EMPTY_STRING;
	
	/**
	 * The variable name associated with the selection.
	 */
	private String variableName = EMPTY_STRING;
	
	/**
	 * The method name associated with the selection.
	 */
	private String methodName = EMPTY_STRING;
	
	
	/**
	 * Constructs a tokenizer for the supplied {@code ISelection} and tokenizes
	 * it.
	 * 
	 * @param selection the selection to tokenize
	 */
	public SelectionTokenizer(ISelection selection) {
		tokenize(selection);
	}
	
	/**
	 * Returns the fully-qualified class name associated with the
	 * {@code Selection}, or the empty string if there is none.
	 * 
	 * @return the fully-qualified class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the variable name associated with the {@code ISelection}, or the
	 * empty string if there is none.
	 * 
	 * @return the variable name
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * Returns the method name associated with the {@code ISelection}, or the
	 * empty string if there is none.
	 * 
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Tokenizes the supplied {@code ISelection} by delegating to the
	 * appropriate tokenizer.
	 * 
	 * @param selection the selection to tokenize
	 */
	private void tokenize(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			tokenize((IStructuredSelection) selection); 
		}
		else if (selection instanceof ITextSelection) {
			tokenize((ITextSelection) selection);
		}
	}
	
	/**
	 * Tokenizes the first element in the {@code IStructuredSelection} by
	 * delegating to the appropriate tokenizer. 
	 * 
	 * @param selection the selection to tokenize
	 */
	private void tokenize(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IJavaElement) {
			try {
				tokenize((IJavaElement) element);
			}
			catch (JavaModelException e) {
				// do nothing
			}
		}
		else if (element instanceof ContourEditPart) {
			tokenize((ContourEditPart) element);
		}
		else if (element instanceof ExecutionOccurrenceEditPart) {
			tokenize((ExecutionOccurrenceEditPart) element);
		}
	}
	
	/**
	 * Tokenizes the supplied {@code ITextSelection} if the selected text
	 * corresponds to an {@code IJavaElement}.
	 * 
	 * @param selection
	 */
	private void tokenize(ITextSelection selection) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			IEditorInput editorInput = editor.getEditorInput();
			IJavaElement element = JavaUI.getEditorInputJavaElement(editorInput);
			if (element instanceof ICodeAssist) {
				ICodeAssist root = (ICodeAssist) element;
				try {
					int offset = selection.getOffset();
					int length = selection.getLength();
					IJavaElement[] elements = root.codeSelect(offset, length);
					if (elements.length > 0) {
						tokenize(elements[0]);
					}
				}
				catch (JavaModelException e) {
					// do nothing
				}
			}
		}
	}
	
	/**
	 * Tokenizes the supplied {@code IJavaElement} depending on its type.
	 * 
	 * @param element the element to tokenize
	 * @throws JavaModelException if there is a failure in the Java model
	 */
	private void tokenize(IJavaElement element) throws JavaModelException {
		switch (element.getElementType()) {
		case IJavaElement.PACKAGE_FRAGMENT:
			initialize((IPackageFragment) element);
			break;
		case IJavaElement.TYPE:
			initialize((IType) element);
			break;
		case IJavaElement.FIELD:
			initialize((IField) element);
			break;
		case IJavaElement.METHOD:
			initialize((IMethod) element);
			break;
		case IJavaElement.LOCAL_VARIABLE:
			initialize((ILocalVariable) element);
			break;
		}
	}
	
	/**
	 * Initializes the fields using an {@code IPackageFragment}.
	 * 
	 * @param packageFragment the selected package fragment
	 */
	private void initialize(IPackageFragment packageFragment) {
		className = packageFragment.getElementName();
	}
	
	/**
	 * Initializes the fields using an {@code IType}.
	 * 
	 * @param type the selected type
	 */
	private void initialize(IType type) {
		className = type.getFullyQualifiedName('$');
	}

	/**
	 * Initializes the fields using an {@code IField}.
	 * 
	 * @param field the selected field
	 */
	private void initialize(IField field) {
		initialize(field.getDeclaringType());
		variableName = field.getElementName();
	}
	
	/**
	 * Initializes the fields using an {@code IMethod}.
	 * 
	 * @param method the selected method
	 * @throws JavaModelException if there is a failure in the Java model
	 */
	private void initialize(IMethod method) throws JavaModelException {
		initialize(method.getDeclaringType());
		if (method.isConstructor()) {
			methodName = CONSTRUCTOR_STRING;
		}
		else {
			methodName = method.getElementName();
		}
	}
	
	/**
	 * Initializes the fields using an {@code ILocalVariable}.
	 * 
	 * @param variable the selected local variable
	 * @throws JavaModelException if there is a failure in the Java model
	 */
	private void initialize(ILocalVariable variable) throws JavaModelException {
		IJavaElement parent = variable.getParent();
		if (parent.getElementType() == IJavaElement.METHOD) {
			initialize((IMethod) parent);
		}
		
		variableName = variable.getElementName();
	}
	
	/**
	 * Tokenizes the supplied {@code ContourEditPart} by tokenizing the
	 * {@code ContourID} referring to its model.
	 * 
	 * @param editPart
	 */
	private void tokenize(ContourEditPart editPart) {
		Contour contour = (Contour) editPart.getModel();
		ContourIDTokenizer parser = new ContourIDTokenizer(contour.id());
		className = parser.getClassName();
		methodName = parser.getMethodName();
		if (methodName == null) {
			methodName = EMPTY_STRING;
		}
	}
	
	/**
	 * Tokenizes the supplied {@code ExecutionOccurrenceEditPart} by tokenizing
	 * the {@code ContourID} referring to its model.
	 * 
	 * @param editPart
	 */
	private void tokenize(ExecutionOccurrenceEditPart editPart) {
		ExecutionOccurrence execution = editPart.getModel();
		if (!(execution instanceof ThreadActivation)) {
			ContourIDTokenizer parser = new ContourIDTokenizer(execution.id());
			className = parser.getClassName();
			methodName = parser.getMethodName();
		}
	}
}
