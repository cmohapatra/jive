package edu.buffalo.cse.jive.internal.ui.views.contour.model;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.ContourMember.InnerClass;
import edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration;
import edu.bsu.cs.jive.contour.ContourMember.Variable;
import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.bsu.cs.jive.contour.java.JavaContour;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.StaticContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.IJiveLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.PauseAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunForwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepBackwardAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.StepForwardAction;
import edu.buffalo.cse.jive.ui.AbstractStructuredJiveView;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A view part to present {@codeInteractiveContourModel}s associated with
 * {@code IJiveDebugTarget}s.  The content provider used by this view is
 * specific to Java contour models.  The view uses a JFace {@code TreeViewer} to
 * display the contours and a JFace {@code TableViewer} to display the member
 * table of the selected contour.  Controls are also available to step and run
 * through model transactions in both the forward and reverse directions.
 * However, these controls are currently limited to target's that are already
 * terminated.
 * 
 * @see IJiveDebugTarget
 * @see InteractiveContourModel
 * @see TreeViewer
 * @author Jeffrey K Czyz
 */
public class ContourModelView extends AbstractStructuredJiveView {
	
	/**
	 * A {@code TableViewer} used to display the member table of the currently
	 * selected contour.
	 */
	private TableViewer fMemberTableViewer;
	
//	/**
//	 * An action that takes one step backward in the debug target's transaction
//	 * history.
//	 */
//	private StepBackwardAction fStepBackwardAction;
//	
//	/**
//	 * An action that takes one step forward in the debug target's transaction
//	 * history.
//	 */
//	private StepForwardAction fStepForwardAction;
//	
//	/**
//	 * An action that steps forward in the debug target's transaction history
//	 * until the last transaction has been committed.
//	 */
//	private RunBackwardAction fRunBackwardAction;
//	
//	/**
//	 * An action that steps backward in the debug target's transaction history
//	 * until the first transaction has been rolled back.
//	 */
//	private RunForwardAction fRunForwardAction;
//	
//	/**
//	 * An action that halts any commits or rollbacks in progress.
//	 */
//	private PauseAction fPauseAction;

	/**
	 * Constructs the view.
	 */
	public ContourModelView() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#dispose()
	 */
	public void dispose() {
//		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
//		stepManager.removeStepListener(fStepBackwardAction);
//		stepManager.removeStepListener(fStepForwardAction);
//		stepManager.removeStepListener(fRunBackwardAction);
//		stepManager.removeStepListener(fRunForwardAction);
//		stepManager.removeStepListener(fPauseAction);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// Update the member table if the selection is a Contour
		if (part instanceof ContourModelView  && selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (!selection.isEmpty()) {
				if (object instanceof Contour) {
					fMemberTableViewer.setInput(object);
				}
			}
		}
		
		// Allow the super class to handle the selection change
		super.selectionChanged(part, selection);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractStructuredJiveView#getViewer()
	 */
	public TreeViewer getViewer() {
		return (TreeViewer) super.getViewer();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createContentProvider()
	 */
	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new ContourModelContentProvider();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new ContourModelLabelProvider();
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
		// Create a tree viewer for the contours
		SashForm splitter = new SashForm(parent, SWT.NONE);
		TreeViewer contourViewer = new TreeViewer(splitter, SWT.H_SCROLL | SWT.V_SCROLL);
		contourViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		
		// Create a table viewer for the contour member tables
		fMemberTableViewer = new TableViewer(splitter, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		fMemberTableViewer.setContentProvider(new ContourMemberTableContentProvider());
		fMemberTableViewer.setLabelProvider(new ContourMemberTableLabelProvider());
		
		// Customize the member table
		Table table = fMemberTableViewer.getTable();
		table.setHeaderVisible(true);
		String[] columnNames = {"Name", "Type", "Value"};
		int[] columnAlignments = {SWT.LEFT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {120, 180, 180};
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, columnAlignments[i]);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}
		
		// Customize the splitter
		splitter.setWeights(new int[] {70, 30});
		splitter.setOrientation(SWT.VERTICAL);
		
		return contourViewer;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createActions()
	 */
	protected void createActions() {
		super.createActions();
//		
//		fStepBackwardAction = new StepBackwardAction(this);
//		fStepForwardAction = new StepForwardAction(this);
//		fRunBackwardAction = new RunBackwardAction(this);
//		fRunForwardAction = new RunForwardAction(this);
//		fPauseAction = new PauseAction(this);
//		
//		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
//		stepManager.addStepListener(fStepBackwardAction);
//		stepManager.addStepListener(fStepForwardAction);
//		stepManager.addStepListener(fRunBackwardAction);
//		stepManager.addStepListener(fRunForwardAction);
//		stepManager.addStepListener(fPauseAction);
//		
//		addUpdatableAction(fStepBackwardAction);
//		addUpdatableAction(fStepForwardAction);
//		addUpdatableAction(fRunBackwardAction);
//		addUpdatableAction(fRunForwardAction);
//		addUpdatableAction(fPauseAction);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager manager) {
		super.configureToolBar(manager);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, fRunBackwardAction);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, fStepBackwardAction);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, fPauseAction);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, fStepForwardAction);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, fRunForwardAction);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDefaultContentDescription()
	 */
	@Override
	protected String getDefaultContentDescription() {
		return "No contour models to display at this time.";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayTargetDropDownText()
	 */
	@Override
	protected String getDisplayTargetDropDownText() {
		return "Display Contour Model";
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
	 * {@code Contour}s as model elements.
	 * 
	 * @see Contour
	 * @author Jeffrey K Czyz
	 */
	protected class ContourModelContentProvider extends AbstractJiveContentProvider implements ITreeContentProvider, ContourModel.Listener {
		
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
				ContourModel model = target.getContourModel();
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
			Contour parent = (Contour) parentElement;
			ContourModel model = parent.containingModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				if (model.contains(parent)) {
					return model.getChildren(parent).toArray();
				}
				else {
					return new Object[0];
				}
			}
			finally {
				modelLock.unlock();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			Contour child = (Contour) element;
			ContourModel model = child.containingModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				if (model.contains(child)) {
					Contour parent = model.getParent(child);
					return parent == null ? getViewer().getInput() : parent;
				}
				else {
					return null;
				}
			}
			finally {
				modelLock.unlock();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			Contour contour = (Contour) element;
			ContourModel model = contour.containingModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				if (model.contains(contour)) {
					return model.countChildren(contour) > 0;
				}
				else {
					return false;
				}
			}
			finally {
				modelLock.unlock();
			}
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#registerWithModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void registerWithModel(IJiveDebugTarget newInput) {
			newInput.getContourModel().addListener(this);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#unregisterFromModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void unregisterFromModel(IJiveDebugTarget oldInput) {
			oldInput.getContourModel().removeListener(this);
			fMemberTableViewer.setInput(null);
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#contourAdded(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.contour.Contour)
		 */
		public void contourAdded(final ContourModel model, final Contour contour, final Contour parent) {
			Display display = JiveUIPlugin.getStandardDisplay();
			if (display.getThread() == Thread.currentThread()) {
				addContour(model, contour, parent);
			}
			else {
				display.asyncExec(new Runnable() {
					public void run() {
						addContour(model, contour, parent);
					}
				});
			}
		}
		
		/**
		 * Adds the supplied contour to the tree viewer.  This method is called
		 * by {@link #contourAdded(ContourModel, Contour, Contour)}.
		 * 
		 * @param model the model in which the contour was added
		 * @param contour the contour that was added
		 * @param parent the parent of the added contour
		 */
		private void addContour(ContourModel model, Contour contour, Contour parent) {
			TreeViewer viewer = getViewer();
			if (!viewer.getControl().isDisposed()) {
				if (parent == null) {
					viewer.add(viewer.getInput(), contour);
				}
				else {
					ReentrantLock modelLock = model.getModelLock();
					modelLock.lock();
					try {
						if (model.contains(parent)) {
							TreePath path = createTreePath(model, parent);
							viewer.add(path, contour);
							viewer.expandToLevel(path, 1);
						}
						else {
							viewer.add(parent, contour);
						}
					}
					finally {
						modelLock.unlock();
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#contourRemoved(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.contour.Contour)
		 */
		public void contourRemoved(final ContourModel model, final Contour contour, final Contour oldParent) {
			Display display = JiveUIPlugin.getStandardDisplay();
			if (display.getThread() == Thread.currentThread()) {
				removeContour(model, contour, oldParent);
			}
			else {
				display.asyncExec(new Runnable() {
					public void run() {
						removeContour(model, contour, oldParent);
					}
				});
			}
		}
		
		/**
		 * Removes the supplied contour from the tree viewer.  This method is
		 * called by {@link #removeContour(ContourModel, Contour, Contour)}.
		 * 
		 * @param model the model in which the contour was removed
		 * @param contour the contour that was removed
		 * @param oldParent the parent of the removed contour
		 */
		private void removeContour(ContourModel model, Contour contour, Contour oldParent) {
			TreeViewer viewer = getViewer();
			if (!viewer.getControl().isDisposed()) {
				Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (contour.equals(selection)) {
					fMemberTableViewer.setInput(null);
				}
				
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					// The old parent might not be in the model if the remove
					// was a result of a rollback
					if (model.contains(oldParent)) {
						TreePath path = createTreePath(model, oldParent);
						viewer.remove(path.createChildPath(contour));
					}
					else {
						viewer.remove(contour);
					}
				}
				finally {
					modelLock.unlock();
				}
			}
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#valueChanged(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.util.VariableID, edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public void valueChanged(ContourModel model, final Contour contour, VariableID variableID, Value newValue, Value oldValue) {
			// If stepping is in progress, we must be running on the UI thread
			Display display = JiveUIPlugin.getStandardDisplay();
			if (display.getThread() == Thread.currentThread()) {
				updateContourMemberTable(contour);
			}
			else {
				display.asyncExec(new Runnable() {
					public void run() {
						updateContourMemberTable(contour);
					}
				});
			}
		}
		
		/**
		 * Updates the contour member table if the supplied contour is selected.
		 * 
		 * @param contour the contour whose member table has been updated
		 */
		private void updateContourMemberTable(Contour contour) {
			TreeViewer viewer = getViewer();
			if (!viewer.getControl().isDisposed()) {
				Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (contour.equals(selection)) {
					fMemberTableViewer.refresh();
				}
			}
		}
		
		/**
		 * Creates a {@code TreePath} for the supplied contour, so that it may
		 * be used to efficiently add (remove) a contour to (from) the tree
		 * viewer.
		 * 
		 * @param model the model containing the contour
		 * @param contour the contour for which to construct a path
		 * @return a tree path from the root to the contour, inclusive
		 */
		private TreePath createTreePath(ContourModel model, Contour contour) {
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				LinkedList<Contour> segmentList = new LinkedList<Contour>();
				do {
					segmentList.addFirst(contour);
					contour = model.getParent(contour);
				}
				while (contour != null);
				
				return new TreePath(segmentList.toArray());
			}
			finally {
				modelLock.unlock();
			}
		}
	}
	
	/**
	 * An {@code ILabelProvider} for {@code JavaContour}s.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class ContourModelLabelProvider extends LabelProvider {
		
		/**
		 * The last contour for which a label was provided.  This is used so a
		 * contour is not exported more than once.
		 */
		private JavaContour fLastContour = null;
		
		/**
		 * The label provider for the contour being processed.
		 */
		private IJiveLabelProvider fLabelProvider = null;
		
		/**
		 * A label provider for {@code StaticContour}s.
		 */
		private StaticContourLabelProvider fStaticContourExporter = new StaticContourLabelProvider();
		
		/**
		 * A label provider for {@code InstanceContour}s.
		 */
		private InstanceContourLabelProvider fInstanceContourExporter = new InstanceContourLabelProvider();
		
		/**
		 * A label provider for {@code MethodContour}s.
		 */
		private MethodContourLabelProvider fMethodContourExporter = new MethodContourLabelProvider();
		
		/**
		 * A visitor for {@code JavaContour}s that is used to export the contour
		 * with the appropriate label provider.
		 */
		private JavaContour.Visitor fVisitor = new JavaContour.Visitor() {

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.InstanceContour, java.lang.Object)
			 */
			public Object visit(InstanceContour contour, Object arg) {
				fInstanceContourExporter.initialize();
				contour.export(fInstanceContourExporter);
				fLabelProvider = fInstanceContourExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.StaticContour, java.lang.Object)
			 */
			public Object visit(StaticContour contour, Object arg) {
				fStaticContourExporter.initialize();
				contour.export(fStaticContourExporter);
				fLabelProvider = fStaticContourExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.MethodContour, java.lang.Object)
			 */
			public Object visit(MethodContour contour, Object arg) {
				fMethodContourExporter.initialize();
				contour.export(fMethodContourExporter);
				fLabelProvider = fMethodContourExporter;
				return null;
			}
		};
		
		/**
		 * Exports the supplied contour using a contour visitor if the contour
		 * was not exported last.  This prevents the contour from being exported
		 * twice; once for the text label and again for the image label.
		 * 
		 * @param contour the contour to export
		 */
		private void exportContour(JavaContour contour) {
			if (contour != fLastContour) {
				contour.accept(fVisitor, null);
				fLastContour = contour;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			exportContour((JavaContour) element);
			return fLabelProvider.getText();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			exportContour((JavaContour) element);
			return fLabelProvider.getImage();
		}
	}
	
	/**
	 * A content provider for the contour member table.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class ContourMemberTableContentProvider implements IStructuredContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			Contour contour = (Contour) inputElement;
			final List<ContourMember> memberList = new LinkedList<ContourMember>();
			Contour.Exporter exporter = new Contour.Exporter() {

				public void addID(ContourID id) {
					// do nothing
				}

				public void addMember(ContourMember member) {
					memberList.add(member);
				}

				public void exportFinished() {
					// do nothing
				}
			};
			
			contour.export(exporter);
			return memberList.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// do nothing
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}
		
	}
	
	/**
	 * An {@code ITableLabelProvider} for {@code ContourMember}s.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class ContourMemberTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		/**
		 * The label provider for the contour member being processed.
		 */
		private IJiveTableRowLabelProvider fLabelProvider = null;
		
		/**
		 * A label provider for {@code MethodDeclaration}s.
		 */
		private MethodDeclarationLabelProvider fMethodDeclarationExporter = new MethodDeclarationLabelProvider();
		
		/**
		 * A label provider for {@code Variable}s.
		 */
		private VariableLabelProvider fVariableLabelProvider = new VariableLabelProvider();
		
		/**
		 * A label provider for {@code InnerClass}es.
		 */
		private InnerClassLabelProvider fInnerClassLabelProvider = new InnerClassLabelProvider();
		
		/**
		 * A visitor for {@code ContourMember}s that is used to export the
		 * contour member with the appropriate label provider.
		 */
		private ContourMember.Visitor visitor = new ContourMember.Visitor() {

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.ContourMember.Visitor#visit(edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration, java.lang.Object)
			 */
			public Object visit(MethodDeclaration m, Object arg) {
				m.export(fMethodDeclarationExporter);
				fLabelProvider = fMethodDeclarationExporter;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.ContourMember.Visitor#visit(edu.bsu.cs.jive.contour.ContourMember.Variable, java.lang.Object)
			 */
			public Object visit(Variable v, Object arg) {
				v.export(fVariableLabelProvider);
				fLabelProvider = fVariableLabelProvider;
				return null;
			}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.ContourMember.Visitor#visit(edu.bsu.cs.jive.contour.ContourMember.InnerClass, java.lang.Object)
			 */
			public Object visit(InnerClass c, Object arg) {
				c.export(fInnerClassLabelProvider);
				fLabelProvider = fInnerClassLabelProvider;
				return null;
			}
		};
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// do nothing
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			// Only export the contour member once
			if (columnIndex == 0) {
				ContourMember member = (ContourMember) element;
				member.accept(visitor, null);
			}
			
			return fLabelProvider.getColumnText(columnIndex);
		}
	}
}
