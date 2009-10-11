package edu.buffalo.cse.jive.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.part.ViewPart;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.PauseAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunForwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepForwardAction;

/**
 * An abstract view part used to present {@code IJiveDebugTarget}s.  The view
 * part supports viewing multiple targets using a drop down action to switch
 * between them.  In order for a target to be displayed, it must be present in
 * the launch manager.  As a convenience, the view also provides actions to
 * remove terminated launches in a fashion similar to that of the Console view.
 * <p>
 * Clients offering views of a JIVE model should derive from this class either
 * directly or indirectly.
 * 
 * @see AbstractStructuredJiveView
 * @see AbstractGraphicalJiveView
 * @author Jeffrey K Czyz
 */
public abstract class AbstractJiveView extends ViewPart
		implements IJiveView, ILaunchListener, IDebugEventSetListener, IPropertyChangeListener, ISelectionListener {
	
	/**
	 * The group used to hold actions having to do with removing terminated
	 * launches. 
	 */
	protected static final String REMOVE_TERMINATED_GROUP = "removeTerminatedGroup";
	
	/**
	 * The group used to hold actions having to do with stepping through a
	 * program in both directions.
	 */
	protected static final String STEP_CONTROLS_GROUP = "stepControlsGroup";
	
	/**
	 * The group used to hold the action used to switch between targets.
	 */
	protected static final String DISPLAY_TARGET_GROUP = "displayTargetGroup";
	
	/**
	 * The list of {@code IJiveDebugTarget}s known about by the launch manager.
	 */
	private List<IJiveDebugTarget> fTargetList;
	
	/**
	 * The {@code IJiveDebugTarget} that is currently being presented by the
	 * view.
	 */
	private IJiveDebugTarget fActiveTarget;
	
	/**
	 * The list of actions that should be updated when the active target changes.
	 */
	private List<IUpdatableAction> fUpdatableActionList;
	
	/**
	 * The action used to switch between targets.
	 */
	private DisplayTargetDropDownAction fDisplayTargetAction;
	
	/**
	 * The action used to remove the launch of the active target if its process
	 * has terminated.
	 */
	private RemoveTerminatedLaunchAction fRemoveTerminatedAction;
	
	/**
	 * The action used to remove all terminated launches.
	 */
	private RemoveAllTerminatedLaunchesAction fRemoveAllTerminatedAction;
	
	/**
	 * An action that takes one step backward in the debug target's transaction
	 * history.
	 */
	private StepBackwardAction fStepBackwardAction;
	
	/**
	 * An action that takes one step forward in the debug target's transaction
	 * history.
	 */
	private StepForwardAction fStepForwardAction;
	
	/**
	 * An action that steps forward in the debug target's transaction history
	 * until the last transaction has been committed.
	 */
	private RunBackwardAction fRunBackwardAction;
	
	/**
	 * An action that steps backward in the debug target's transaction history
	 * until the first transaction has been rolled back.
	 */
	private RunForwardAction fRunForwardAction;
	
	/**
	 * An action that halts any commits or rollbacks in progress.
	 */
	private PauseAction fPauseAction;
	
	/**
	 * Constructs the view.
	 */
	public AbstractJiveView() {
		super();
		fTargetList = new LinkedList<IJiveDebugTarget>();
		fActiveTarget = null;
		fUpdatableActionList = new LinkedList<IUpdatableAction>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// Initialize the viewer
		initializeViewer(parent);
		
		// Initialize the instance variables
		initializeTargetList();
		createActions();
		
		// Create the context menu
		createContextMenu();
		
		// Initialize the action bars
		IActionBars actionBars = getViewSite().getActionBars();
		configureToolBar(actionBars.getToolBarManager());
		configurePullDownMenu(actionBars.getMenuManager());
		actionBars.updateActionBars();
		
		// Register as a listener
		DebugPlugin.getDefault().addDebugEventListener(this);
		getSite().getPage().addPostSelectionListener(this);
		
		displayDebugTarget(fActiveTarget);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		// Unregister as a listener
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		DebugPlugin.getDefault().removeDebugEventListener(this);
		getSite().getPage().removePostSelectionListener(this);
		
		for (IJiveDebugTarget target : fTargetList) {
			IConsole console = DebugUITools.getConsole(target);
			console.removePropertyChangeListener(this);
		}
		
		fTargetList.clear();
		fActiveTarget = null;
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.removeStepListener(fStepBackwardAction);
		stepManager.removeStepListener(fStepForwardAction);
		stepManager.removeStepListener(fRunBackwardAction);
		stepManager.removeStepListener(fRunForwardAction);
		stepManager.removeStepListener(fPauseAction);
		
		super.dispose();
	}
	
	/**
	 * Called immediately in {@link #createPartControl(Composite)} to initialize
	 * the internal viewer for the view.  Subclasses must implement this method
	 * for the specific framework being used.
	 * 
	 * @param parent the parent widget of the viewer
	 */
	protected abstract void initializeViewer(Composite parent);
	
	/**
	 * Initializes the target list by consulting with the launch manager for
	 * {@code IJiveDebugTarget}s, then registers with the manager as a listener.
	 * The first target, if any, is assigned as the active target.
	 */
	private void initializeTargetList() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		for (IDebugTarget target : manager.getDebugTargets()) {
			if (target instanceof IJiveDebugTarget) {
				fTargetList.add((IJiveDebugTarget) target);
				
				// Used to update the view part's content description
				IConsole console = DebugUITools.getConsole(target);
				console.addPropertyChangeListener(this);
			}
		}
		
		if (!fTargetList.isEmpty()) {
			fActiveTarget = fTargetList.get(0);
		}
		
		manager.addLaunchListener(this);
	}
	
	/**
	 * Creates the actions that will be used by the view.
	 */
	protected void createActions() {
		fRemoveTerminatedAction = new RemoveTerminatedLaunchAction();
		fRemoveAllTerminatedAction = new RemoveAllTerminatedLaunchesAction();
		fDisplayTargetAction = new DisplayTargetDropDownAction();
		
		addUpdatableAction(fRemoveTerminatedAction);
		addUpdatableAction(fRemoveAllTerminatedAction);
		addUpdatableAction(fDisplayTargetAction);
		
		fStepBackwardAction = new StepBackwardAction(this);
		fStepForwardAction = new StepForwardAction(this);
		fRunBackwardAction = new RunBackwardAction(this);
		fRunForwardAction = new RunForwardAction(this);
		fPauseAction = new PauseAction(this);
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.addStepListener(fStepBackwardAction);
		stepManager.addStepListener(fStepForwardAction);
		stepManager.addStepListener(fRunBackwardAction);
		stepManager.addStepListener(fRunForwardAction);
		stepManager.addStepListener(fPauseAction);
		
		addUpdatableAction(fStepBackwardAction);
		addUpdatableAction(fStepForwardAction);
		addUpdatableAction(fRunBackwardAction);
		addUpdatableAction(fRunForwardAction);
		addUpdatableAction(fPauseAction);
	}
	
	/**
	 * Creates the context menu for the view.  Subclasses must implement this
	 * method.
	 */
	protected abstract void createContextMenu();
	
	/**
	 * Adds the actions to the tool bar using the supplied tool bar manager.
	 * 
	 * @param manager the manager used to add the actions to the tool bar
	 */
	protected void configureToolBar(IToolBarManager manager) {
		manager.add(new Separator(REMOVE_TERMINATED_GROUP));
		manager.appendToGroup(REMOVE_TERMINATED_GROUP, fRemoveTerminatedAction);
		manager.appendToGroup(REMOVE_TERMINATED_GROUP, fRemoveAllTerminatedAction);
		
		manager.add(new Separator(STEP_CONTROLS_GROUP));
		manager.appendToGroup(STEP_CONTROLS_GROUP, fRunBackwardAction);
		manager.appendToGroup(STEP_CONTROLS_GROUP, fStepBackwardAction);
		manager.appendToGroup(STEP_CONTROLS_GROUP, fPauseAction);
		manager.appendToGroup(STEP_CONTROLS_GROUP, fStepForwardAction);
		manager.appendToGroup(STEP_CONTROLS_GROUP, fRunForwardAction);
		
		manager.add(new Separator(DISPLAY_TARGET_GROUP));
		manager.appendToGroup(DISPLAY_TARGET_GROUP, fDisplayTargetAction);
	}
	
	/**
	 * Configures the view's pull-down menu with actions.  Subclasses should
	 * override this method if a pull-down menu is desired.
	 * 
	 * @param manager the menu manager in which to add actions
	 */
	protected void configurePullDownMenu(IMenuManager manager) {
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveView#getDisplayed()
	 */
	public IJiveDebugTarget getDisplayed() {
		return fActiveTarget;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.IJiveView#display(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public void display(IJiveDebugTarget target) {
		if (fTargetList.contains(target)) {
			if (target != fActiveTarget) {
				displayDebugTarget(target);
			}
		}
	}
	
	/**
	 * Displays the supplied target in the view part.  This target is put in
	 * front of the list of most recently viewed targets.
	 * <p>
	 * This method should only be called on the UI thread.
	 * 
	 * @param target the target to display
	 */
	private void displayDebugTarget(IJiveDebugTarget target) {
		if (target != null) {
			fTargetList.remove(target);
			fTargetList.add(0, target);
		}
		
		fActiveTarget = target;
		setViewerInput(target);
		
		updateUserInterface();
	}
	
	/**
	 * Sets the internal viewer's input to that of the supplied target.
	 * Subclasses must implement this method for the specific framework being
	 * used.
	 * 
	 * @param target the target to set as input.
	 */
	protected abstract void setViewerInput(IJiveDebugTarget target);
	
	/**
	 * Adds an {@code IUpdatableAction} to the list of actions that must be
	 * updated when the active target changes.
	 * 
	 * @param action the action to add
	 */
	protected void addUpdatableAction(IUpdatableAction action) {
		fUpdatableActionList.add(action);
	}
	
	/**
	 * Updates the state of the view part's content description and actions.
	 * <p>
	 * This method should only be called on the UI thread.
	 */
	private void updateUserInterface() {
		updateContentDescription();
		updateActions();
	}
	
	/**
	 * Updates the content description based on the active target.  If there
	 * isn't an active target, then a default description is used.
	 * <p>
	 * This method should only be called on the UI thread.
	 */
	private void updateContentDescription() {
		if (fActiveTarget == null) {
			setContentDescription(getDefaultContentDescription());
		}
		else {
			IConsole console = DebugUITools.getConsole(fActiveTarget);
			setContentDescription(console.getName());
		}
	}
	
	/**
	 * Updates the actions' states based on the active and inactive targets
	 * (depending on the action).
	 * <p>
	 *  This method should only be called on the UI thread.
	 */
	private void updateActions() {
		for (IUpdatableAction action : fUpdatableActionList) {
			action.update();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
	 */
	public void launchAdded(ILaunch launch) {
		launchChanged(launch);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
	 */
	public void launchChanged(final ILaunch launch) {
		Display display = JiveUITools.getStandardDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				IDebugTarget target = launch.getDebugTarget();
				if (target != null && target instanceof IJiveDebugTarget) {
					addDebugTarget((IJiveDebugTarget) target);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	public void launchRemoved(final ILaunch launch) {
		Display display = JiveUITools.getStandardDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				IDebugTarget target = launch.getDebugTarget();
				if (target != null && target instanceof IJiveDebugTarget) {
					removeDebugTarget((IJiveDebugTarget) target);
				}
			}
		});
	}
	
	/**
	 * Adds the supplied target to the target list and displays it if there
	 * are no other targets.  If the target is already in the target list, then
	 * the call has no effect.
	 * <p>
	 * This method should only be called on the UI thread.
	 * 
	 * @param target the target to add
	 */
	private void addDebugTarget(IJiveDebugTarget target) {
		if (!fTargetList.contains(target)) {
			fTargetList.add(target);
			
			// Used to update the view part's content description
			IConsole console = DebugUITools.getConsole(target);
			console.addPropertyChangeListener(this);
			
			if (fActiveTarget == null) {
				displayDebugTarget(target);
			}
		}
	}
	
	/**
	 * Removes the supplied target from the target list.  If it was the active
	 * target, then the most recently viewed target, if one exists, is
	 * displayed.  If the target is not in the target list, then the call has
	 * no effect.
	 * <p>
	 * This method should only be called on the UI thread.
	 * 
	 * @param target the target to remove
	 */
	private void removeDebugTarget(IJiveDebugTarget target) {
		if (fTargetList.contains(target)) {
			fTargetList.remove(target);
			
			// Check if not null to avoid a NullPointerException in some cases
			IConsole console = DebugUITools.getConsole(target);
			if (console != null) {
				console.removePropertyChangeListener(this);
			}
		
			if (target == fActiveTarget) {
				if (fTargetList.isEmpty()) {
					displayDebugTarget(null);
				}
				else {
					displayDebugTarget(fTargetList.get(0));
				}
			}
			else {
				// This is implicitly called by displayDebugTarget but must be
				// explicitly called here in order to disable any actions 
				updateActions();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			switch (event.getKind()) {
			case DebugEvent.TERMINATE:
			case DebugEvent.SUSPEND:
			case DebugEvent.RESUME:
				Display display = JiveUITools.getStandardDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						updateActions();
					}
				});
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IBasicPropertyConstants.P_TEXT)) {
			if (fActiveTarget != null) {
				IConsole console = DebugUITools.getConsole(fActiveTarget);
				if (event.getSource().equals(console)) {
					updateContentDescription();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof IDebugView && selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (!selection.isEmpty()) {
				
				// Determine if an IJiveDebugTarget is associated with the selection 
				if (object instanceof IStackFrame) {
					object = ((IStackFrame) object).getThread();
				}
				
				if (object instanceof IThread) {
					object = ((IThread) object).getDebugTarget();
				}
				
				if (object instanceof IProcess) {
					object = ((IProcess) object).getLaunch();
				}
				
				if (object instanceof ILaunch) {
					object = ((ILaunch) object).getDebugTarget();
				}
				
				// Display the debug target
				if (object instanceof IJiveDebugTarget) {
					display((IJiveDebugTarget) object);
				}
			}
		}
	}
	
	/**
	 * An action used to remove the launch associated with the active target.
	 * The launch is only removed if it is terminated.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class RemoveTerminatedLaunchAction extends Action implements IUpdatableAction {
		
		/**
		 * Constructs the action.
		 */
		public RemoveTerminatedLaunchAction() {
			super("Remove Launch");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_REMOVE_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_REMOVE_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			if (fActiveTarget != null) {
				ILaunch launch = fActiveTarget.getLaunch();
				if (launch.isTerminated()) {
					DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
				}
			}
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
		 */
		public void update() {
			IJiveDebugTarget target = getDisplayed();
			setEnabled(target != null && target.getLaunch().isTerminated());
		}
	}
	
	/**
	 * An action used to remove all terminated launches assoicated with
	 * {@code IJiveDebugTarget}s.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class RemoveAllTerminatedLaunchesAction extends Action implements IUpdatableAction {
		
		public RemoveAllTerminatedLaunchesAction() {
			super("Remove All Terminated Launches");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_REMOVE_ALL_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_REMOVE_ALL_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			List<ILaunch> terminatedList = new LinkedList<ILaunch>();
			for (IJiveDebugTarget target : fTargetList) {
				if (target.getLaunch().isTerminated()) {
					terminatedList.add(target.getLaunch());
				}
			}
			
			if (!terminatedList.isEmpty()) {
				ILaunch[] launches = terminatedList.toArray(new ILaunch[0]);
				DebugPlugin.getDefault().getLaunchManager().removeLaunches(launches);
			}
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
		 */
		public void update() {
			for (IJiveDebugTarget target : fTargetList) {
				if (target.getLaunch().isTerminated()) {
					setEnabled(true);
					return;
				}
			}
			
			setEnabled(false);
		}
	}
	
	/**
	 * An action used to switch between targets.  A drop down menu can be used
	 * to switch between targets.  Otherwise, the most recent target viewed
	 * before the active target is displayed.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class DisplayTargetDropDownAction extends Action implements IMenuCreator, IUpdatableAction, ILaunchListener {
		
		/**
		 * The drop down menu.
		 */
		private Menu fMenu;
		
		/**
		 * Constructs the action.
		 */
		public DisplayTargetDropDownAction() {
			super(getDisplayTargetDropDownText());
			setImageDescriptor(getDisplayDropDownEnabledImageDescriptor());
			setDisabledImageDescriptor(getDisplayDropDownDisabledImageDescriptor());
			
			setMenuCreator(this);
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
			update();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			if (fTargetList.size() > 1) {
				displayDebugTarget(fTargetList.get(1));
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.IMenuCreator#dispose()
		 */
		public void dispose() {
			if (fMenu != null) {
				fMenu.dispose();
			}
			
			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
		 */
		public Menu getMenu(Control parent) {
			// Dispose the menu, so it can be refreshed
			if (fMenu != null) {
				fMenu.dispose();
			}
			
			fMenu = new Menu(parent);
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			
			for (IDebugTarget target : manager.getDebugTargets()) {
				if (target instanceof IJiveDebugTarget) {
					Action action = new DisplayTargetAction((IJiveDebugTarget) target);
					action.setChecked(target.equals(fActiveTarget));
					ActionContributionItem item = new ActionContributionItem(action);
					item.fill(fMenu, -1);
				}
			}
			
			return fMenu;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
		 */
		public Menu getMenu(Menu parent) {
			// Not used since the menu is not a sub-menu
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
		 */
		public void update() {
			setEnabled(fTargetList.size() > 1);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
		 */
		public void launchAdded(ILaunch launch) {
			launchChanged(launch);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
		 */
		public void launchChanged(ILaunch launch) {
			Display display = JiveUITools.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					update();
				}
			});
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
		 */
		public void launchRemoved(ILaunch launch) {
			Display display = JiveUITools.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					// Dispose of the menu so it doesn't hang on to resources
					if (fMenu != null) {
						fMenu.dispose();
					}
					
					update();
				}
			});
		}
		
		/**
		 * An action used to display a target through the drop down menu.
		 * 
		 * @author Jeffrey K Czyz
		 */
		private class DisplayTargetAction extends Action {
			
			/**
			 * The target to display.
			 */
			private IJiveDebugTarget fTarget;
			
			/**
			 * Constructs the action for the supplied parameter.
			 * 
			 * @param target the target to display
			 */
			public DisplayTargetAction(IJiveDebugTarget target) {
				super("", AS_RADIO_BUTTON);
				IConsole console = DebugUITools.getConsole(target);
				setText(console.getName());
				setImageDescriptor(console.getImageDescriptor());
				
				fTarget = target;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				if (!fTarget.equals(fActiveTarget)) {
					displayDebugTarget(fTarget);
				}
			}
		}
	}
	
	/**
	 * Returns the content description to be used when there is no active
	 * target.
	 * 
	 * @return the default content description
	 * @see #setContentDescription(String)
	 */
	protected abstract String getDefaultContentDescription();
	
	/**
	 * Returns the text to be used for the {@code DisplayTargetDropDownAction}.
	 * 
	 * @return the text for the display target drop down
	 */
	protected abstract String getDisplayTargetDropDownText();
	
	/**
	 * Returns the {@code ImageDescriptor} of the image to be used for the
	 * {@code DisplayTargetDropDownAction} when it is enabled.
	 * 
	 * @return the enabled image descriptor for the display target drop down
	 */
	protected abstract ImageDescriptor getDisplayDropDownEnabledImageDescriptor();
	
	/**
	 * Returns the {@code ImageDescriptor} of the image to be used for the
	 * {@code DisplayTargetDropDownAction} when it is disabled.
	 * 
	 * @return the disabled image descriptor for the display target drop down
	 */
	protected abstract ImageDescriptor getDisplayDropDownDisabledImageDescriptor();
}
