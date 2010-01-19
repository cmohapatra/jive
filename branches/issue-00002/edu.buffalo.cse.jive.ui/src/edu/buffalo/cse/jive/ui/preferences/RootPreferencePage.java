package edu.buffalo.cse.jive.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;


/**
 * The JIVE root level preference page.  All JIVE preferences should be located
 * on pages nested beneath the root page.
 *   
 * @author Jeffrey K Czyz
 */
public class RootPreferencePage	extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IntegerFieldEditor updateIntervalEditor;
	
	public RootPreferencePage() {
		super(GRID);
		setPreferenceStore(JiveUIPlugin.getDefault().getPreferenceStore());
		setDescription("JIVE user preferences:");
	}
	
	@Override
	protected void createFieldEditors() {
		updateIntervalEditor = new IntegerFieldEditor(
				IJiveUIConstants.PREF_UPDATE_INTERVAL,
				"Visualization update interval (ms):",
				getFieldEditorParent(),
				5);
		updateIntervalEditor.setValidRange(250, 60000);
		addField(updateIntervalEditor);
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
}