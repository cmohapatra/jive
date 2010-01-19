package edu.buffalo.cse.jive.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A preference page for specifying the default event filters for various launch
 * configuration types.  
 * 
 * @author jkczyz
 */
public class DefaultEventFiltersPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * The editor for local Java application filters.
	 */
	private EventFiltersEditor javaApplicationEditor;
	
	/**
	 * The editor for Java applet filters.
	 */
	private EventFiltersEditor javaAppletEditor;
	
	/**
	 * The editor for JUnit application filters.
	 */
	private EventFiltersEditor junitApplicationEditor;
	
	/**
	 * Creates the default event filters preference page using a grid layout.
	 */
	public DefaultEventFiltersPreferencePage() {
		super(GRID);
		setPreferenceStore(JiveUIPlugin.getDefault().getPreferenceStore());
		String description = "JIVE collects events from a program as it " +
				"executes.  Class exclusion filters may be specified in order "  +
				"to reduce the resulting overhead and to produce smaller " +
				"visualizations.";
		setDescription(description);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#adjustGridLayout()
	 */
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		List field = javaApplicationEditor.getListControl(getFieldEditorParent());
		GridData layoutData = (GridData) field.getLayoutData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		field.setLayoutData(layoutData);
		
		field = javaAppletEditor.getListControl(getFieldEditorParent());
		layoutData = (GridData) field.getLayoutData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		field.setLayoutData(layoutData);
		
		field = junitApplicationEditor.getListControl(getFieldEditorParent());
		layoutData = (GridData) field.getLayoutData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		field.setLayoutData(layoutData);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		javaApplicationEditor = createEventFilterEditor(
				IJiveUIConstants.PREF_JAVA_APPLICATION_FILTERS,
				"Java application filters");
		javaAppletEditor = createEventFilterEditor(
				IJiveUIConstants.PREF_JAVA_APPLET_FILTERS,
				"Java applet filters");
		junitApplicationEditor = createEventFilterEditor(
				IJiveUIConstants.PREF_JUNIT_APPLICATION_FILTERS,
				"JUnit application filters");
		
		addField(javaApplicationEditor);
		addField(javaAppletEditor);
		addField(junitApplicationEditor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Creates an {@code EventFilterEditor} with the supplied name and label. 
	 * 
	 * @param name the attribute name of the preference
	 * @param labelText the label describing the editor's list
	 * @return an event filter editor contained on this page
	 */
	private EventFiltersEditor createEventFilterEditor(String name, String labelText) {
		return new EventFiltersEditor(name, labelText, getFieldEditorParent());
	}
}
