package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.ui.AbstractGraphicalJiveView;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A view part to present {@code MultiThreadedSequenceModel}s associated with
 * {@code IJiveDebugTarget}s.  The content provider used by this view is
 * specific to Java sequence models.  The view uses a GEF
 * {@code ScrollingGraphicalViewer} to display the diagram.
 * 
 * @see IJiveDebugTarget
 * @see MultiThreadedSequenceModel
 * @see ScrollingGraphicalViewer
 * @see SequenceModelEditPartFactory
 * @author Jeffrey K Czyz
 */
public class SequenceDiagramView extends AbstractGraphicalJiveView {

	private ShowThreadActivationsAction showThreadActivationsAction;
	
	private ExpandLifelinesAction expandLifelinesAction;
	
	/**
	 * Constructs the view.
	 */
	public SequenceDiagramView() {
		super();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractGraphicalJiveView#createGraphicalViewer()
	 */
	protected GraphicalViewer createGraphicalViewer() {
		return new ScrollingGraphicalViewer();
	}
	
	protected void createActions() {
		super.createActions();
		
		showThreadActivationsAction = new ShowThreadActivationsAction();
		expandLifelinesAction = new ExpandLifelinesAction();
	}
	
	protected void configurePullDownMenu(IMenuManager manager) {
		manager.add(showThreadActivationsAction);
		manager.add(expandLifelinesAction);
	}
	
	protected class ShowThreadActivationsAction extends Action {
		
		public ShowThreadActivationsAction() {
			super("Show Thread Activations", AS_CHECK_BOX);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_SHOW_THREAD_ACTIVATIONS_ICON_KEY));
			
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			setChecked(prefs.getBoolean(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS));
		}
		
		public void run() {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			prefs.setValue(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS, isChecked());
		}
	}
	
	protected class ExpandLifelinesAction extends Action {
		
		public ExpandLifelinesAction() {
			super("Expand Lifelines", AS_CHECK_BOX);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_EXPAND_LIFELINES_ICON_KEY));
			
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			setChecked(prefs.getBoolean(IJiveUIConstants.PREF_EXPAND_LIFELINES));
		}
		
		public void run() {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			prefs.setValue(IJiveUIConstants.PREF_EXPAND_LIFELINES, isChecked());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractGraphicalJiveView#createEditPartFactory()
	 */
	protected EditPartFactory createEditPartFactory() {
		return new SequenceModelEditPartFactory();
	}
	
	protected ContextMenuProvider createContextMenuProvider() {
		return new SequenceDiagramContextMenuProvider(getViewer());
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDefaultContentDescription()
	 */
	@Override
	protected String getDefaultContentDescription() {
		return "No sequence diagrams to display at this time.";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayTargetDropDownText()
	 */
	@Override
	protected String getDisplayTargetDropDownText() {
		return "Display Sequence Diagram";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayDropDownEnabledImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getDisplayDropDownEnabledImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_TREE_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayDropDownDisabledImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getDisplayDropDownDisabledImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.DISABLED_TREE_ICON_KEY);
	}
}
