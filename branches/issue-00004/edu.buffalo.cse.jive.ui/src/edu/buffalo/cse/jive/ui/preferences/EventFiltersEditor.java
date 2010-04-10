package edu.buffalo.cse.jive.ui.preferences;

import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * An editor for adding and removing filters from the list of default event
 * filters.
 * 
 * @author jkczyz
 */
public class EventFiltersEditor extends ListEditor {

	/**
	 * Creates an event filter editor with the supplied name and label as a
	 * child of the given parent.
	 * 
	 * @param name the attribute name of the editor's preference
	 * @param labelText the label describing the editor's list
	 * @param parent the containing component
	 */
	public EventFiltersEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.ListEditor#createList(java.lang.String[])
	 */
	@Override
	protected String createList(String[] items) {
		return PreferenceInitializer.convertFilters(items);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.ListEditor#parseString(java.lang.String)
	 */
	@Override
	protected String[] parseString(String stringList) {
		StringTokenizer tokenizer = new StringTokenizer(stringList, PreferenceInitializer.EVENT_FILTER_DELIMITER);
		int tokenCount = tokenizer.countTokens();
		String[] result = new String[tokenCount];
		
		for (int i = 0; i < tokenCount; i++) {
			result[i] = tokenizer.nextToken();
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.ListEditor#getNewInputObject()
	 */
	@Override
	protected String getNewInputObject() {
		InputDialog inputDialog = new InputDialog(
				getShell(),
				"Exclude classes",
				"Enter class exclusion filter",
				null,
				new IInputValidator() {
					public String isValid(String newText) {
						// TODO: Perform better validation
						String delimiter = PreferenceInitializer.EVENT_FILTER_DELIMITER;
						if (newText.contains(delimiter)) {
							return "The filter cannot contain the delimiter: " + delimiter;
						}
						else if (newText.isEmpty()) {
							return "The filter cannot be empty";
						}
						else {
							return null;
						}
					}
				}
		);
		
		inputDialog.open();
		if (inputDialog.getReturnCode() == InputDialog.OK) {
			return inputDialog.getValue();
		}
		else {
			return null;
		}
	}
}
