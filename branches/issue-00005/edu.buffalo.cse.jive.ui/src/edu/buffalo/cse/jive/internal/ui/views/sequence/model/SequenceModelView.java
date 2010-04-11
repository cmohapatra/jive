package edu.buffalo.cse.jive.internal.ui.views.sequence.model;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunToEventAction;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.MessageReceive;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.sequence.Message.MessageSort;
import edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation;
import edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation;
import edu.buffalo.cse.jive.sequence.java.MethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;
import edu.buffalo.cse.jive.ui.AbstractStructuredJiveView;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.IUpdatableAction;

/**
 * A view part to present {@code MultiThreadedSequenceModel}s associated with
 * {@code IJiveDebugTarget}s.  The content provider used by this view is
 * specific to Java sequence models.  The view uses a JFace {@code TreeViewer}
 * to display the execution and event occurrences.  The tree contains multiple
 * roots, where each is the call tree of a thread.  Controls are available to
 * expand or collapse portions of the call trees.
 * 
 * @author Jeffrey K Czyz
 */
public class SequenceModelView extends AbstractStructuredJiveView {
	
	protected static final String EXPAND_COLLAPSE_GROUP = "expandCollapseGroup";
	
	/**
	 * An action used to recursively expand all nodes in the tree rooted at the
	 * selected node.
	 */
	private ExpandAllSelectedAction expandAllSelectedAction;
	
	/**
	 * An action used to recursively collapse all nodes in the tree rooted at the
	 * selected node.
	 */
	private CollapseAllSelectedAction collapseAllSelectedAction;
	
	/**
	 * An action used to recursively expand all nodes in the tree.
	 */
	private ExpandAllAction expandAllAction;
	
	/**
	 * An action used to recursively collapse all nodes in the tree.
	 */
	private CollapseAllAction collapseAllAction;
	
	/**
	 * A viewer filter that excludes {@code AssignEvent}s.
	 */
	private FilterAssignEventsAction assignEventFilter;
	
	/**
	 * A viewer filter that excludes {@code CatchEvent}s.
	 */
	private FilterCatchEventsAction catchEventFilter;
	
	/**
	 * A viewer filter that excludes {@code EOSEvent}s.
	 */
	private FilterEOSEventsAction eosEventFilter;
	
	/**
	 * A viewer filter that excludes {@code LoadEvent}s.
	 */
	private FilterLoadEventsAction loadEventFilter;
	
	/**
	 * A viewer filter that excludes {@code NewEvent}s.
	 */
	private FilterNewEventsAction newEventFilter;
	
	/**
	 * A viewer filter that excludes {@code ReturnEvent}s.
	 */
	private FilterReturnEventsAction returnEventFilter;
	
	/**
	 * A viewer filter that excludes {@code ThrowEvent}s.
	 */
	private FilterThrowEventsAction throwEventFilter;
	
	/**
	 * A viewer filter that excludes {@code MessageReceive}s.
	 */
	private FilterMessageReceivesAction messageReceieveFilter;
	
	/**
	 * Constructs the view.
	 */
	public SequenceModelView() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createContentProvider()
	 */
	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new SequenceModelContentProvider();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new SequenceModelLabelProvider();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createSorter()
	 */
	@Override
	protected ViewerSorter createSorter() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected StructuredViewer createViewer(Composite parent) {
		return new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createActions()
	 */
	protected void createActions() {
		// Make sure the super class's method is called
		super.createActions();
		
		// Create the tool bar actions
		TreeViewer viewer = (TreeViewer) getViewer();
		expandAllAction = new ExpandAllAction(viewer);
		collapseAllAction = new CollapseAllAction(viewer);
		
		// Register the tool bar actions to be included in updates
		addUpdatableAction(expandAllAction);
		addUpdatableAction(collapseAllAction);
		
		// Create the pull down menu actions
		assignEventFilter = new FilterAssignEventsAction();
		catchEventFilter = new FilterCatchEventsAction();
		eosEventFilter = new FilterEOSEventsAction();
		loadEventFilter = new FilterLoadEventsAction();
		newEventFilter = new FilterNewEventsAction();
		returnEventFilter = new FilterReturnEventsAction();
		throwEventFilter = new FilterThrowEventsAction();
		messageReceieveFilter = new FilterMessageReceivesAction();
		
		// Create the context menu actions
		expandAllSelectedAction = new ExpandAllSelectedAction();
		collapseAllSelectedAction = new CollapseAllSelectedAction();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager manager) {
		// Fill the context menu with disabled actions
		expandAllSelectedAction.setEnabled(false);
		collapseAllSelectedAction.setEnabled(false);
		manager.add(expandAllSelectedAction);
		manager.add(collapseAllSelectedAction);
		
		TreeViewer viewer = (TreeViewer) getViewer();
		ITreeSelection selection = (ITreeSelection) viewer.getSelection();
		if (!selection.isEmpty()) {
			// Determine if the actions should be enabled
			for (TreePath path : selection.getPaths()) {
				if (viewer.isExpandable(path)) {
					expandAllSelectedAction.setEnabled(true);
					collapseAllSelectedAction.setEnabled(true);
					break;
				}
			}
			
			// Add run to event action
			Object element = selection.getFirstElement();
			if (element instanceof EventOccurrence) {
				EventOccurrence event = (EventOccurrence) element;
				IJiveDebugTarget target = getDisplayed();
				IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
				if (target.canReplayRecordedStates() && !stepManager.isStepping(target)) {
					RunToEventAction action = new RunToEventAction(target, event.underlyingEvent());
					action.setText("Run to Event");
					manager.add(action);
				}
			}
		}
	}
	
	/**
	 * An action that recursively expanding all nodes rooted at the selected
	 * node.  This action supports expanding multiple selections.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class ExpandAllSelectedAction extends Action {
		
		/**
		 * Constructs the action.
		 */
		public ExpandAllSelectedAction() {
			super("Expand All");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_EXPAND_ALL_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_EXPAND_ALL_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			TreeViewer viewer = (TreeViewer) getViewer(); 
			ITreeSelection selection = (ITreeSelection) viewer.getSelection();
			for (TreePath path : selection.getPaths()) {
				viewer.expandToLevel(path, AbstractTreeViewer.ALL_LEVELS);
			}
		}
	}
	
	/**
	 * An action that recursively collapses all nodes rooted at the selected
	 * node.  This action supports collapsing multiple selections.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class CollapseAllSelectedAction extends Action {
		
		/**
		 * Constructs the action.
		 */
		public CollapseAllSelectedAction() {
			super("Collapse All");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_COLLAPSE_ALL_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_COLLAPSE_ALL_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			TreeViewer viewer = (TreeViewer) getViewer(); 
			ITreeSelection selection = (ITreeSelection) viewer.getSelection();
			for (TreePath path : selection.getPaths()) {
				viewer.collapseToLevel(path, AbstractTreeViewer.ALL_LEVELS);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager manager) {
		super.configureToolBar(manager);
		manager.insertBefore(DISPLAY_TARGET_GROUP, new Separator(EXPAND_COLLAPSE_GROUP));
		manager.appendToGroup(EXPAND_COLLAPSE_GROUP, expandAllAction);
		manager.appendToGroup(EXPAND_COLLAPSE_GROUP, collapseAllAction);
	}
	
	// TODO Move this class to its own file
	/**
	 * An action that recursively expands all nodes in the corresponding
	 * {@code TreeViewer}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class ExpandAllAction extends Action implements IUpdatableAction {
		
		/**
		 * The tree viewer to expand.
		 */
		private TreeViewer viewer;
		
		/**
		 * Constructs the action.
		 * 
		 * @param viewer the tree viewer to expand
		 */
		public ExpandAllAction(TreeViewer viewer) {
			super("Expand All");
			this.viewer = viewer;
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_EXPAND_ALL_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_EXPAND_ALL_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			viewer.expandAll();
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
		 */
		public void update() {
			IJiveDebugTarget target = getDisplayed();
			setEnabled(target != null);
		}
	}
	
	// TODO Move this class to its own file
	/**
	 * An action that recursively collapses all nodes in the corresponding
	 * {@code TreeViewer}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class CollapseAllAction extends Action implements IUpdatableAction {
		
		/**
		 * The tree viewer to collapse.
		 */
		private TreeViewer viewer;
		
		/**
		 * Constructs the action.
		 * 
		 * @param viewer the tree viewer to collapse.
		 */
		public CollapseAllAction(TreeViewer viewer) {
			super("Collapse All");
			this.viewer = viewer;
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_COLLAPSE_ALL_ICON_KEY));
			setDisabledImageDescriptor(registry.getDescriptor(IJiveUIConstants.DISABLED_COLLAPSE_ALL_ICON_KEY));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			viewer.collapseAll();
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.IUpdatableAction#update()
		 */
		public void update() {
			IJiveDebugTarget target = getDisplayed();
			setEnabled(target != null);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configurePullDownMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void configurePullDownMenu(IMenuManager manager) {
		manager.add(assignEventFilter);
		manager.add(eosEventFilter);
		manager.add(catchEventFilter);
		manager.add(loadEventFilter);
		manager.add(newEventFilter);
		manager.add(returnEventFilter);
		manager.add(throwEventFilter);
		manager.add(new Separator());
		manager.add(messageReceieveFilter);
	}
	
	/**
	 * An action that adds or removes an event exclusion filter based on the
	 * checked status.  When the action is checked, the exclusion filter is
	 * added to the viewer.  When it is unchecked, the filter is removed. 
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected abstract class AbstractFilterEventsAction extends Action {
		
		/**
		 * The {@code Event} type which will be excluded from the viewer when
		 * the filter is added.
		 */
		private Class<? extends Event> eventClass;
		
		/**
		 * Constructs the action with the supplied event class as the class
		 * that will be filtered.
		 * 
		 * @param eventClass the class for the viewer to filter
		 */
		public AbstractFilterEventsAction(Class<? extends Event> eventClass) {
			super("", AS_CHECK_BOX);
			this.eventClass = eventClass;
			setChecked(false);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			if (isChecked()) {
				getViewer().addFilter(filter);
			}
			else {
				getViewer().removeFilter(filter);
			}
		}
		
		/**
		 * The viewer filter that is used by this action.
		 */
		private ViewerFilter filter = new ViewerFilter() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof EventOccurrence) {
					Event event = ((EventOccurrence) element).underlyingEvent();
					if (eventClass.isInstance(event)) {
						return false;
					}
				}
				return true;
			}
		};
	}
	
	/**
	 * An action that filters {@code AssignEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterAssignEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterAssignEventsAction() {
			super(AssignEvent.class);
			setText("Hide Assign Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_ASSIGN_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code CatchEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterCatchEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterCatchEventsAction() {
			super(CatchEvent.class);
			setText("Hide Catch Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_CATCH_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code EOSEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterEOSEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterEOSEventsAction() {
			super(EOSEvent.class);
			setText("Hide EOS Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_EOS_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code LoadEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterLoadEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterLoadEventsAction() {
			super(LoadEvent.class);
			setText("Hide Load Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_LOAD_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code NewEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterNewEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterNewEventsAction() {
			super(NewEvent.class);
			setText("Hide New Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_NEW_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code ReturnEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterReturnEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterReturnEventsAction() {
			super(ReturnEvent.class);
			setText("Hide Return Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_RETURN_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code ThrowEvent}s from the viewer.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterThrowEventsAction extends AbstractFilterEventsAction {
		
		/**
		 * Constructs the action.
		 */
		public FilterThrowEventsAction() {
			super(ThrowEvent.class);
			setText("Hide Throw Events");
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_THROW_EVENT_ICON_KEY));
		}
	}
	
	/**
	 * An action that filters {@code MessageReceive}s from the viewer.  When the
	 * action is checked, the exclusion filter is added to the viewer.  When it
	 * is unchecked, the filter is removed. 
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class FilterMessageReceivesAction extends Action {
		
		/**
		 * Constructs the action.
		 */
		public FilterMessageReceivesAction() {
			super("Hide Message Receives", AS_CHECK_BOX);
			ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
			setImageDescriptor(registry.getDescriptor(IJiveUIConstants.ENABLED_HIDE_MESSAGE_RECEIVES_ICON_KEY));
			setChecked(false);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			if (isChecked()) {
				getViewer().addFilter(filter);
			}
			else {
				getViewer().removeFilter(filter);
			}
		}
		
		/**
		 * The viewer filter that is used by this action.
		 */
		private ViewerFilter filter = new ViewerFilter() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !(element instanceof MessageReceive);
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDefaultContentDescription()
	 */
	@Override
	protected String getDefaultContentDescription() {
		return "No sequence models to display at this time.";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayTargetDropDownText()
	 */
	@Override
	protected String getDisplayTargetDropDownText() {
		return "Display Sequence Model";
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
	
	/**
	 * An implementation of an {@code AbstractJiveContentProvider} that provides
	 * {@code ExecutionOccurrence}s and {@code EventOccurrence}s as model
	 * elements.
	 * 
	 * @see ExecutionOccurrence
	 * @see EventOccurrence
	 * @author Jeffrey K Czyz
	 */
	protected class SequenceModelContentProvider extends AbstractJiveContentProvider implements ITreeContentProvider, SequenceModel.Listener {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// do nothing
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#getModelElements(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected Object[] getModelElements(IJiveDebugTarget target) {
			if (target != null) {
				MultiThreadedSequenceModel model = target.getSequenceModel();
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					return model.getRoots().toArray();
				}
				finally {
					modelLock.unlock();
				}
			}
			
			return new Object[0];
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			// Children of an execution occurrence are its events
			if (parentElement instanceof ExecutionOccurrence) {
				ExecutionOccurrence parent = (ExecutionOccurrence) parentElement;
				SequenceModel model = parent.containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					return parent.events().toArray();
				}
				finally {
					modelLock.unlock();
				}
			}
			// A message send's only child is its receiving  execution
			else if (parentElement instanceof MessageSend) {
				MessageSend parent = (MessageSend) parentElement;
				SequenceModel model = parent.containingExecution().containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					// Reply messages should be leaf nodes
					Message message = parent.message();
					if (message.messageSort() == MessageSort.REPLY) {
						return new Object[0];
					}
					else {
						return new Object[] { message.receiveEvent().containingExecution() };
					}
				}
				finally {
					modelLock.unlock();
				}
			}
			// Other event occurrences do not have children
			else if (parentElement instanceof EventOccurrence) {
				return new Object[0];
			}
			// This case should not be reached unless there is a programming error
			else {
				throw new IllegalStateException("Element " + parentElement + " has an invalid type.");
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			// The parent of an execution occurrence is its initiator
			if (element instanceof ExecutionOccurrence) {
				ExecutionOccurrence child = (ExecutionOccurrence) element;
				SequenceModel model = child.containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					return child.initiator();
				}
				finally {
					modelLock.unlock();
				}
			}
			// The parent of an event occurrence is its containing execution
			else if (element instanceof EventOccurrence) {
				EventOccurrence child = (EventOccurrence) element;
				SequenceModel model = child.containingExecution().containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					return child.containingExecution();
				}
				finally {
					modelLock.unlock();
				}
			}
			// This case should not be reached unless there is a programming error
			else {
				throw new IllegalStateException("Element " + element + " has an invalid type.");
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			// An execution occurrence has children if it has any events
			if (element instanceof ExecutionOccurrence) {
				ExecutionOccurrence parent = (ExecutionOccurrence) element;
				SequenceModel model = parent.containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					return parent.events().size() > 0;
				}
				finally {
					modelLock.unlock();
				}
			}
			// A message send's only child is it's receiving execution
			else if (element instanceof MessageSend) {
				MessageSend parent = (MessageSend) element;
				SequenceModel model = parent.containingExecution().containingModel();
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					// Reply messages should be leaf nodes
					Message message = parent.message();
					if (message.messageSort() == MessageSort.REPLY) {
						return false;
					}
					else {
						// This case probably won't ever be hit
						return message.receiveEvent().containingExecution() != null;
					}
				}
				finally {
					modelLock.unlock();
				}
			}
			// Other event occurrences do not have children
			else if (element instanceof EventOccurrence) {
				return false;
			}
			// This case should not be reached unless there is a programming error
			else {
				throw new IllegalStateException("Element " + element + " has an invalid type.");
			}
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#registerWithModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void registerWithModel(IJiveDebugTarget newInput) {
			newInput.getSequenceModel().addListener(this);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#unregisterFromModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void unregisterFromModel(IJiveDebugTarget oldInput) {
			oldInput.getSequenceModel().removeListener(this);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel.Listener#eventAdded(edu.buffalo.cse.jive.sequence.SequenceModel, edu.buffalo.cse.jive.sequence.EventOccurrence, edu.buffalo.cse.jive.sequence.ExecutionOccurrence)
		 */
		public void eventAdded(final SequenceModel model, final EventOccurrence event, final ExecutionOccurrence execution) {
			Display display = JiveUIPlugin.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					ReentrantLock modelLock = model.getModelLock();
					modelLock.lock();
					try {
						TreeViewer viewer = (TreeViewer) getViewer();
						if (!viewer.getControl().isDisposed()) {
							TreePath path = createTreePath(model, execution);
							viewer.add(path, event);
						}
					}
					finally {
						modelLock.unlock();
					}
				}
			});
			
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel.Listener#executionAdded(edu.buffalo.cse.jive.sequence.SequenceModel, edu.buffalo.cse.jive.sequence.ExecutionOccurrence, edu.buffalo.cse.jive.sequence.EventOccurrence)
		 */
		public void executionAdded(final SequenceModel model, final ExecutionOccurrence execution, final MessageSend initiator) {
			Display display = JiveUIPlugin.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					ReentrantLock modelLock = model.getModelLock();
					modelLock.lock();
					try {
						TreeViewer viewer = (TreeViewer) getViewer();
						if (!viewer.getControl().isDisposed()) {
							if (initiator == null) {
								viewer.add(viewer.getInput(), execution);
							}
							else {
								TreePath path = createTreePath(model, execution);
								viewer.add(path.getParentPath(), execution);
							}
						}
					}
					finally {
						modelLock.unlock();
					}
				}
			});
		}
		
		/**
		 * Creates a {@code TreePath} for the supplied execution occurrence, so
		 * that it may be used to efficiently add (remove) an execution or event
		 * occurrence to (from) the tree viewer.
		 * 
		 * @param model the model containing the execution occurrence
		 * @param execution the execution occurrence for which to construct a path
		 * @return a tree path from the root to the execution occurrence, inclusive
		 */
		private TreePath createTreePath(SequenceModel model, ExecutionOccurrence execution) {
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				LinkedList<Object> segmentList = new LinkedList<Object>();
				segmentList.addFirst(execution);
				MessageSend initiator = execution.initiator();
				
				while (initiator != null) {
					segmentList.addFirst(initiator);
					execution = initiator.containingExecution();
					segmentList.addFirst(execution);
					initiator = execution.initiator();
				}
				
				return new TreePath(segmentList.toArray());
			}
			finally {
				modelLock.unlock();
			}
		}
	}
	
	// TODO Move this to a separate class
	/**
	 * An {@code ILabelProvider} for {@code ExecutionOccurrence}s and
	 * {@code EventOccurrence}s.  Since event occurrences are wrappers for
	 * {@code Event}s, their labels are simply evnt labels.
	 * 
	 * @author Jeffrey K Czyz
	 */
	public static class SequenceModelLabelProvider extends LabelProvider {
		
		/**
		 * The last event for which a label was provided.  This is used so the
		 * event is not exported more than once.
		 */
		private Event lastEvent = null;
		
		/**
		 * The the label provider that was used for the last event. 
		 */
		private IJiveLabelProvider labelProvider = null;
		
		/**
		 * A label provider for start events. 
		 */
		private StartEventLabelProvider startEventExporter = new StartEventLabelProvider();
		
		/**
		 * A label provider for exit events. 
		 */
		private ExitEventLabelProvider exitEventExporter = new ExitEventLabelProvider();
		
		/**
		 * A label provider for load events. 
		 */
		private LoadEventLabelProvider loadEventExporter = new LoadEventLabelProvider();
		
		/**
		 * A label provider for new events. 
		 */
		private NewEventLabelProvider newEventExporter = new NewEventLabelProvider();
		
		/**
		 * A label provider for call events. 
		 */
		private CallEventLabelProvider callEventExporter = new CallEventLabelProvider();
		
		/**
		 * A label provider for return events. 
		 */
		private ReturnEventLabelProvider returnEventExporter = new ReturnEventLabelProvider();
		
		/**
		 * A label provider for catch events. 
		 */
		private CatchEventLabelProvider catchEventExporter = new CatchEventLabelProvider();
		
		/**
		 * A label provider for throw events. 
		 */
		private ThrowEventLabelProvider throwEventExporter = new ThrowEventLabelProvider();
		
		/**
		 * A label provider for EOS events. 
		 */
		private EOSEventLabelProvider eosEventExporter = new EOSEventLabelProvider();
		
		/**
		 * A label provider for assign events. 
		 */
		private AssignEventLabelProvider assignEventExporter = new AssignEventLabelProvider();
		
		/**
		 * An event visitor used to export events.
		 */
		private Event.Visitor eventLabelProviderVisitor = new Event.Visitor() {

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.AssignEvent, java.lang.Object)
			 */
			public Object visit(AssignEvent event, Object arg) {
				event.export(assignEventExporter);
				labelProvider = assignEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.CallEvent, java.lang.Object)
			 */
			public Object visit(CallEvent event, Object arg) {
				event.export(callEventExporter);
				labelProvider = callEventExporter;
				return null;
			}
			
			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.CatchEvent, java.lang.Object)
			 */
			public Object visit(CatchEvent event, Object arg) {
				event.export(catchEventExporter);
				labelProvider = catchEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.EOSEvent, java.lang.Object)
			 */
			public Object visit(EOSEvent event, Object arg) {
				event.export(eosEventExporter);
				labelProvider = eosEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ExceptionEvent, java.lang.Object)
			 */
			public Object visit(ExceptionEvent event, Object arg) {
				throw new IllegalStateException("ExceptionEvent has been deprecated.");
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ExitEvent, java.lang.Object)
			 */
			public Object visit(ExitEvent event, Object arg) {
				event.export(exitEventExporter);
				labelProvider = exitEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.LoadEvent, java.lang.Object)
			 */
			public Object visit(LoadEvent event, Object arg) {
				event.export(loadEventExporter);
				labelProvider = loadEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.NewEvent, java.lang.Object)
			 */
			public Object visit(NewEvent event, Object arg) {
				event.export(newEventExporter);
				labelProvider = newEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ReturnEvent, java.lang.Object)
			 */
			public Object visit(ReturnEvent event, Object arg) {
				event.export(returnEventExporter);
				labelProvider = returnEventExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.StartEvent, java.lang.Object)
			 */
			public Object visit(StartEvent event, Object arg) {
				event.export(startEventExporter);
				labelProvider = startEventExporter;
				return null;
			}
			
			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.events.Event.Visitor#visit(edu.bsu.cs.jive.events.ThrowEvent, java.lang.Object)
			 */
			public Object visit(ThrowEvent event, Object arg) {
				event.export(throwEventExporter);
				labelProvider = throwEventExporter;
				return null;
			}
		};
		
		/**
		 * Exports the event using the event label provider visitor if the event
		 * was not exported last.  This prevents the contour from being exported
		 * twice; once for the text label and again for the image label.
		 * 
		 * @param event the event to export
		 */
		private void exportEvent(Event event) {
			if (event != lastEvent) {
				event.accept(eventLabelProviderVisitor, null);
				lastEvent = event;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof JavaExecutionActivation) {
				JavaExecutionActivation activation = (JavaExecutionActivation) element;
				return activation.accept(activationTextProvider, null).toString();
			}
			else if (element instanceof EventOccurrence) {
				EventOccurrence event = (EventOccurrence) element;
				exportEvent(event.underlyingEvent());
				return labelProvider.getText();
			}
			else {
				throw new IllegalStateException("Element " + element + " has an invalid type.");
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			if (element instanceof JavaExecutionActivation) {
				JavaExecutionActivation activation = (JavaExecutionActivation) element;
				return (Image) (activation.accept(activationImageProvider, null));
			}
			else if (element instanceof EventOccurrence) {
				EventOccurrence event = (EventOccurrence) element;
				exportEvent(event.underlyingEvent());
				return labelProvider.getImage();
			}
			else {
				throw new IllegalStateException("Element " + element + " has an invalid type.");
			}
		}
		
		/**
		 * A visitor that provides image labels for {@code JavaExecutionActivation}s.
		 */
		private JavaExecutionActivation.Visitor activationImageProvider = new JavaExecutionActivation.Visitor() {

			/**
			 * The image for a {@code MethodActivation}.
			 */
			private final Image METHOD_ACTIVATION_IMAGE;
			
			/**
			 * The image for a {@code FilteredMethodActivation}.
			 */
			private final Image FILTERED_METHOD_ACTIVATION_IMAGE;
			
			/**
			 * The image for a {@code ThreadActivation}.
			 */
			private final Image THREAD_ACTIVATION_IMAGE;
			
			{
				ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
				METHOD_ACTIVATION_IMAGE = registry.get(IJiveUIConstants.ENABLED_METHOD_ACTIVATION_ICON_KEY);
				FILTERED_METHOD_ACTIVATION_IMAGE = registry.get(IJiveUIConstants.ENABLED_FILTERED_METHOD_ACTIVATION_ICON_KEY);
				THREAD_ACTIVATION_IMAGE = registry.get(IJiveUIConstants.ENABLED_THREAD_ACTIVATION_ICON_KEY);
			}
			
			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.MethodActivation, java.lang.Object)
			 */
			public Object visit(MethodActivation activation, Object arg) {
				return METHOD_ACTIVATION_IMAGE;
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation, java.lang.Object)
			 */
			public Object visit(FilteredMethodActivation activation, Object arg) {
				return FILTERED_METHOD_ACTIVATION_IMAGE;
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.ThreadActivation, java.lang.Object)
			 */
			public Object visit(ThreadActivation activation, Object arg) {
				return THREAD_ACTIVATION_IMAGE;
			}
		};
		
		/**
		 * A visitor that provides text labels for {@code JavaExecutionActivation}s.
		 */
		private JavaExecutionActivation.Visitor activationTextProvider = new JavaExecutionActivation.Visitor() {

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.MethodActivation, java.lang.Object)
			 */
			public Object visit(MethodActivation activation, Object arg) {
				return activation.id();
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation, java.lang.Object)
			 */
			public Object visit(FilteredMethodActivation activation, Object arg) {
				return activation.description();
			}

			/* (non-Javadoc)
			 * @see edu.buffalo.cse.jive.sequence.java.JavaExecutionActivation.Visitor#visit(edu.buffalo.cse.jive.sequence.java.ThreadActivation, java.lang.Object)
			 */
			public Object visit(ThreadActivation activation, Object arg) {
				ThreadID thread = activation.thread();
				return thread.getName() + " (id = " + thread.getId() + ")"; 
			}
		};
	}
}
