package edu.buffalo.cse.jive.ui;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;

/**
 * An abstract view part used to present {@code IJiveDebugTarget}s using a JFace
 * {@code StructuredViewer}.  JFace viewers use content providers to obtain
 * model elements and label providers to obtain the visual representation of
 * those elements.
 * 
 * @see #createViewer(Composite)
 * @see #createContentProvider()
 * @see #createLabelProvider()
 * @author Jeffrey K Czyz
 */
public abstract class AbstractStructuredJiveView extends AbstractJiveView implements IStructuredJiveView {

	/**
	 * The viewer used to present the active target.
	 */
	private StructuredViewer viewer;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#initializeViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void initializeViewer(Composite parent) {
		viewer = createViewer(parent);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setSorter(createSorter());
		
		// Register the viewer as a selection provider
		getSite().setSelectionProvider(viewer);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.IStructuredJiveView#getViewer()
	 */
	public StructuredViewer getViewer() {
		return viewer;
	}
	
	/**
	 * Creates a {@code StructuredViewer} to be used as the view part's viewer.
	 * This method is called by {@link #initializeViewer(Composite)}, and the
	 * resulting viewer is used during the lifetime of the view part.
	 * 
	 * @param parent the parent widget of the viewer
	 * @return the viewer that was created
	 * @see #getViewer()
	 */
	protected abstract StructuredViewer createViewer(Composite parent);
	
	/**
	 * Creates an {@code IStructuredContentProvider} to be used as the content
	 * provider for the viewer created by {@link #createViewer(Composite)}.
	 * This mehtod is called by {@link #initializeViewer(Composite)}.
	 * 
	 * @return the content provider that was created
	 */
	protected abstract IStructuredContentProvider createContentProvider();
	
	/**
	 * Creates an {@code IBaseLabelProvider} to be used as the label provider
	 * for the viewer created by {@link #createViewer(Composite)}.  This method
	 * is called by {@link #initializeViewer(Composite)}.
	 * 
	 * @return the label provider that was created
	 */
	protected abstract IBaseLabelProvider createLabelProvider();
	
	/**
	 * Creates a {@code ViewerSorter} to be used as the sorter for the viewer
	 * created by {@link #createViewer(Composite)}.  This mehtod is called by
	 * {@link #initializeViewer(Composite)}.
	 * 
	 * @return the viewer sorter that was created
	 */
	protected abstract ViewerSorter createSorter();
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createContextMenu()
	 */
	protected void createContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		
		Control menuControl = viewer.getControl();
		Menu menu = manager.createContextMenu(menuControl);
		menuControl.setMenu(menu);

		// Register the context menu such that other plug-ins may contribute to it
		getSite().registerContextMenu(manager, viewer);
	}
	
	/**
	 * Fills the context menu with actions.  Subclasses should override this
	 * method if a context menu is desired.
	 * 
	 * @param manager the context menu to fill
	 */
	protected void fillContextMenu(IMenuManager manager) {
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#setViewerInput(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	@Override
	protected void setViewerInput(IJiveDebugTarget target) {
		viewer.setInput(target);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * An abstract content provider used to provide model elements associated
	 * with {@code IJiveDebugTarget}s.  Methods for providing model elements to
	 * the viewer, registering with a model, and unregistering from a model are
	 * to be implemented by derived classes.  These methods will be called at
	 * the appropraite time by this class.
	 * 
	 * @see #getModelElements(IJiveDebugTarget)
	 * @see #registerWithModel(IJiveDebugTarget)
	 * @see #unregisterFromModel(IJiveDebugTarget)
	 * @author Jeffrey K Czyz
	 */
	public abstract class AbstractJiveContentProvider implements IStructuredContentProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public final void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			if (oldInput != null) {
				unregisterFromModel((IJiveDebugTarget) oldInput);
			}
			
			if (newInput != null) {
				registerWithModel((IJiveDebugTarget) newInput);
			}
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public final Object[] getElements(Object inputElement) {
			return getModelElements((IJiveDebugTarget) inputElement);
		}
		
		/**
		 * Registers itself as a listener to a model of the supplied
		 * {@code IJiveDebugTarget}.  This method is called by
		 * {@link #inputChanged(Viewer, Object, Object)} after calling
		 * {@link #unregisterFromModel(IJiveDebugTarget)} with the old input as
		 * the parameter.
		 * 
		 * @param newInput the new input of the content provider
		 * @see #inputChanged(Viewer, Object, Object)
		 */
		protected abstract void registerWithModel(IJiveDebugTarget newInput);
		
		/**
		 * Unregisters itself from being a listener to a model of the supplied
		 * {@code IJiveDebugTarget}.  This method is called by
		 * {@link #inputChanged(Viewer, Object, Object)} before calling
		 * {@link #registerWithModel(IJiveDebugTarget)} with the new input as
		 * the parameter.
		 * 
		 * @param oldInput the new input of the content provider
		 * @see #inputChanged(Viewer, Object, Object)
		 */
		protected abstract void unregisterFromModel(IJiveDebugTarget oldInput);
		
		/**
		 * Returns the model elements that should be provided to the viewer when
		 * the input has changed.
		 * 
		 * @param target the input of the content provider
		 * @return the model elements to provide
		 * @see #getElements(Object)
		 * @see #inputChanged(Viewer, Object, Object)
		 */
		protected abstract Object[] getModelElements(IJiveDebugTarget target);
	}
}
