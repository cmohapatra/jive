package edu.buffalo.cse.jive.ui;

//import java.util.ArrayList;
//import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;

/**
 * An abstract {@code IGraphicalJiveView} using a {@code ScalableRootEditPart}
 * as its {@code RootEditPart}.  It also provides zooming controls for its
 * tool bar.
 * 
 * @see IGraphicalJiveView
 * @see GraphicalViewer
 * @see ScalableRootEditPart
 * @author Jeffrey K Czyz
 */
public abstract class AbstractGraphicalJiveView extends AbstractJiveView implements IGraphicalJiveView {
	
	/**
	 * The group used to hold actions having to do with diagram zooming.
	 */
	protected static final String ZOOM_CONTROLS_GROUP = "zoomControlsGroup";
	
	/**
	 * The viewer used to display the model.
	 */
	private GraphicalViewer viewer;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.IGraphicalJiveView#getViewer()
	 */
	public GraphicalViewer getViewer() {
		return viewer;
	}
	
	// TODO Determine if a zoom combo box is needed and (if so) how to make it
	// the correct width
//	private ZoomComboContributionItem zoomCombo;
	
//	private List<String> zoomLevels;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#initializeViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void initializeViewer(Composite parent) {
		parent.setLayout(new FillLayout());
		// Create the viewer using the supplied parent
		viewer = createGraphicalViewer();
		Control c = viewer.createControl(parent);
		c.setBackground(ColorConstants.white);
		
		// Create and initialize the root edit part
		ScalableRootEditPart root = new ScalableRootEditPart();
		
//		zoomLevels = new ArrayList<String>(3);
//		zoomLevels.add(ZoomManager.FIT_ALL);
//		zoomLevels.add(ZoomManager.FIT_WIDTH);
//		zoomLevels.add(ZoomManager.FIT_HEIGHT);
//		zoomCombo = new ZoomComboContributionItem(getSite().getPage(), zoomLevels.toArray(new String[]{}));
		
//		ZoomManager zoomManager = root.getZoomManager();
//		zoomManager.setZoomLevelContributions(zoomLevels);
//		zoomManager.setZoomAnimationStyle(ZoomManager.ANIMATE_ZOOM_IN_OUT);
		
		
		viewer.setRootEditPart(root);
		viewer.setEditDomain(new DefaultEditDomain(null));
		
		// Create and initialize the edit part factory
		EditPartFactory factory = createEditPartFactory();
		viewer.setEditPartFactory(factory);
		
		// Register the viewer as a selection provider
		getSite().setSelectionProvider(viewer);
	}
	
	/**
	 * Creates a {@code GraphicalViewer} to be used as the view part's viewer.
	 * This method is called by {@link #initializeViewer(Composite)}, and the
	 * resulting viewer is used during the lifetime of the view part.
	 * 
	 * @return the viewer that was created
	 */
	protected abstract GraphicalViewer createGraphicalViewer();
	
	/**
	 * Creates the {@code EditPartFactory} to be used by the viewer in order to
	 * create {@code EditPart}s for elements of the model.  This method is
	 * called by {@link #initializeViewer(Composite)}, and the resulting factory
	 * is used during the lifetime of the view part.
	 * 
	 * @return the edit part factory the was created.
	 */
	protected abstract EditPartFactory createEditPartFactory();

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createContextMenu()
	 */
	protected void createContextMenu() {
		ContextMenuProvider manager = createContextMenuProvider();
		viewer.setContextMenu(manager);
		
		// Register the context menu such that other plug-ins may contribute to it
		getSite().registerContextMenu(manager, viewer);
	}
	
	/**
	 * Creates a {@code ContextMenuProvider} to be used as the
	 * {@code MenuManager} for the viewer's context menu.  This method is called
	 * by {@link #createContextMenu()}, and the resulting manager is used during
	 * the lifetime of the view part.
	 * 
	 * @return the context menu provider that was created
	 */
	protected abstract ContextMenuProvider createContextMenuProvider();
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#configureToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager manager) {
		super.configureToolBar(manager);
		ScalableRootEditPart root = (ScalableRootEditPart) viewer.getRootEditPart();
		IAction zoomIn = new ZoomInAction(root.getZoomManager());
		IAction zoomOut = new ZoomOutAction(root.getZoomManager());
		
		// TODO Determine the correct way to accomplish this
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);
		
		manager.insertBefore(DISPLAY_TARGET_GROUP, new Separator(ZOOM_CONTROLS_GROUP));
		manager.appendToGroup(ZOOM_CONTROLS_GROUP, zoomIn);
		manager.appendToGroup(ZOOM_CONTROLS_GROUP, zoomOut);
//		manager.appendToGroup(INTERNAL_TOOL_BAR_ADDITIONS_GROUP, zoomCombo);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#setViewerInput(edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	protected void setViewerInput(IJiveDebugTarget target) {
		viewer.setContents(target);
		viewer.getRootEditPart().refresh();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class) {
			return viewer.getProperty(ZoomManager.class.toString());
		}
		
		return super.getAdapter(type);
	}
}
