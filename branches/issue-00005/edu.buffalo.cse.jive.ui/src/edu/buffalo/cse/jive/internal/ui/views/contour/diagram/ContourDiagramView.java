package edu.buffalo.cse.jive.internal.ui.views.contour.diagram;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.PauseAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunForwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepForwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts.ContourDiagramEditPart.State;
import edu.buffalo.cse.jive.ui.AbstractGraphicalJiveView;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A view part to present {@code InteractiveContourModel}s associated with
 * {@code IJiveDebugTarget}s.  The content provider used by this view is
 * specific to Java contour models.  The view uses a GEF
 * {@code ScrollingGraphicalViewer} to display the contours.  Controls are also
 * available to step and run through model transactions in both the forward and
 * reverse directions.  However, these controls are currently limited to
 * target's that are already terminated. 
 * 
 * @see IJiveDebugTarget
 * @see InteractiveContourModel
 * @see ScrollingGraphicalViewer
 * @see ContourModelEditPartFactory
 * @author Jeffrey K Czyz
 */
public class ContourDiagramView extends AbstractGraphicalJiveView {
	
	/**
	 * The group used to actions specific to the contour diagram.
	 */
	private static final String CONTOUR_DIAGRAM_GROUP = "contourDiagramGroup";
	
	private MinimizeContoursAction fMinimizeContoursAction;
	
	private ExpandContoursAction fExpandContoursAction;
	
	private StackInstanceContoursAction fStackInstanceContoursAction;
	
	private ShowMemberTablesAction fShowMemberTablesAction;
	
	private ScrollLockAction fScrollLockAction;

	/**
	 * Constructs the view.
	 */
	public ContourDiagramView() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#dispose()
	 */
	public void dispose() {
		
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractGraphicalJiveView#createGraphicalViewer()
	 */
	protected GraphicalViewer createGraphicalViewer() {
		return new ScrollingGraphicalViewer();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createActions()
	 */
	protected void createActions() {
		super.createActions();
		fScrollLockAction = new ScrollLockAction();
		
		// Create the pull down menu actions
		fMinimizeContoursAction = new MinimizeContoursAction();
		fExpandContoursAction = new ExpandContoursAction();
		fStackInstanceContoursAction = new StackInstanceContoursAction();
		fShowMemberTablesAction = new ShowMemberTablesAction();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager manager) {
		super.configureToolBar(manager);
		manager.insertBefore(ZOOM_CONTROLS_GROUP, new Separator(CONTOUR_DIAGRAM_GROUP));
		manager.appendToGroup(CONTOUR_DIAGRAM_GROUP, fScrollLockAction);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configurePullDownMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void configurePullDownMenu(IMenuManager manager) {
		manager.add(fMinimizeContoursAction);
		manager.add(fExpandContoursAction);
		manager.add(fStackInstanceContoursAction);
		manager.add(new Separator());
		manager.add(fShowMemberTablesAction);
	}
	
	protected class ContourStateAction extends Action {
		
		private String state;
		
		ContourStateAction(String text, String state) {
			super(text, AS_RADIO_BUTTON);
			this.state = state;

			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			String actualState = prefs.getString(IJiveUIConstants.PREF_CONTOUR_STATE);
			setChecked(actualState.equals(state));
		}
		
		public void run() {
			if (isChecked()) {
				Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
				prefs.setValue(IJiveUIConstants.PREF_CONTOUR_STATE, state);
			}
		}
	}
	
	protected class MinimizeContoursAction extends ContourStateAction {
		
		public MinimizeContoursAction() {
			super("Minimize Contours", IJiveUIConstants.PREF_CONTOUR_STATE_MINIMIZE);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_MINIMIZE_CONTOURS_ICON_KEY));
		}
	}
	
	protected class ExpandContoursAction extends ContourStateAction {
		
		public ExpandContoursAction() {
			super("Expand Contours", IJiveUIConstants.PREF_CONTOUR_STATE_EXPAND);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_EXPAND_CONTOURS_ICON_KEY));
		}
	}
	
	protected class StackInstanceContoursAction extends ContourStateAction {
		
		public StackInstanceContoursAction() {
			super("Stack Instance Contours", IJiveUIConstants.PREF_CONTOUR_STATE_STACK);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_STACK_INSTANCE_CONTOURS_ICON_KEY));
		}
	}
	
	protected class ShowMemberTablesAction extends Action {
		
		public ShowMemberTablesAction() {
			super("Show Member Tables", AS_CHECK_BOX);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_SHOW_MEMBER_TABLES_ICON_KEY));
			
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			setChecked(prefs.getBoolean(IJiveUIConstants.PREF_SHOW_MEMBER_TABLES));
		}
		
		public void run() {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			prefs.setValue(IJiveUIConstants.PREF_SHOW_MEMBER_TABLES, isChecked());
		}
	}
	
	protected class ScrollLockAction extends Action {
		
		public ScrollLockAction() {
			super("Scroll Lock", AS_CHECK_BOX);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_SCROLL_LOCK_ICON_KEY));
			
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			setChecked(prefs.getBoolean(IJiveUIConstants.PREF_SCROLL_LOCK));
		}
		
		public void run() {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			prefs.setValue(IJiveUIConstants.PREF_SCROLL_LOCK, isChecked());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractGraphicalJiveView#createEditPartFactory()
	 */
	protected EditPartFactory createEditPartFactory() {
		return new ContourModelEditPartFactory();
	}
	
	protected ContextMenuProvider createContextMenuProvider() {
		return new ContextMenuProvider(getViewer()) {

			@Override
			public void buildContextMenu(IMenuManager arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDefaultContentDescription()
	 */
	@Override
	protected String getDefaultContentDescription() {
		return "No contour diagrams to display at this time.";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayTargetDropDownText()
	 */
	@Override
	protected String getDisplayTargetDropDownText() {
		return "Display Contour Diagram";
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
