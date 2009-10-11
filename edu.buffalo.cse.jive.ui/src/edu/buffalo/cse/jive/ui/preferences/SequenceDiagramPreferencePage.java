package edu.buffalo.cse.jive.ui.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

public class SequenceDiagramPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private IntegerFieldEditor activationWidthEditor;
	
	private IntegerFieldEditor eventHeightEditor;
	
	public SequenceDiagramPreferencePage() {
		super(GRID);
		setPreferenceStore(JiveUIPlugin.getDefault().getPreferenceStore());
		setDescription("Sequence diagram user preferences:");
	}
	
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		Text field = activationWidthEditor.getTextControl(getFieldEditorParent());
		GridData layoutData = (GridData) field.getLayoutData();
		layoutData.grabExcessHorizontalSpace = false;
		
		field = eventHeightEditor.getTextControl(getFieldEditorParent());
		layoutData = (GridData) field.getLayoutData();
		layoutData.grabExcessHorizontalSpace = false;
	}

	@Override
	protected void createFieldEditors() {
		createThreadColorEditor(IJiveUIConstants.PREF_THREAD_COLOR_1, 1);
		createThreadColorEditor(IJiveUIConstants.PREF_THREAD_COLOR_2, 2);
		createThreadColorEditor(IJiveUIConstants.PREF_THREAD_COLOR_3, 3);
		createThreadColorEditor(IJiveUIConstants.PREF_THREAD_COLOR_4, 4);
		createThreadColorEditor(IJiveUIConstants.PREF_THREAD_COLOR_5, 5);
		createActivationWidthEditor();
		createEventHeightEditor();
	}
	
	private void createThreadColorEditor(String name, int threadNumber) {
		ColorFieldEditor threadColorEditor = new ColorFieldEditor(
				name,
				"Thread #" + threadNumber + " color:",
				getFieldEditorParent());
		addField(threadColorEditor);
	}
	
	private void createActivationWidthEditor() {
		activationWidthEditor = new IntegerFieldEditor(
				IJiveUIConstants.PREF_ACTIVATION_WIDTH,
				"Activation width (pixels):",
				getFieldEditorParent(),
				2);
		activationWidthEditor.setValidRange(1, 21);
		addField(activationWidthEditor);
	}
	
	private void createEventHeightEditor() {
		eventHeightEditor = new IntegerFieldEditor(
				IJiveUIConstants.PREF_EVENT_HEIGHT,
				"Event height (pixels):",
				getFieldEditorParent(),
				1);
		eventHeightEditor.setValidRange(1, 9);
		addField(eventHeightEditor);
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

}
