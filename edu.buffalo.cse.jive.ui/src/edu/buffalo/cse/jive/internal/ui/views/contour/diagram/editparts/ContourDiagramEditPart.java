package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.widgets.Display;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourDiagramFigure;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.graph.ContourGraph;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorListener;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * An {@code EditPart} serving as a controller for a {@code ContourModel}, which
 * is visualized by a {@code ContourDiagramFigure}.  The edit part also serves
 * as a contour model listener.  It handles contour model events by delegating
 * to the appropriate edit part.
 * 
 * @see ContourEditPart
 * @see ContourReferenceEditPart
 * @see ContourModel
 * @see ContourDiagramFigure
 * @author Jeffrey K Czyz
 */
public class ContourDiagramEditPart extends AbstractContourModelEditPart
implements ContourModel.Listener, IDebugEventSetListener, IPropertyChangeListener, IThreadColorListener, IStepListener {
	
	public enum State {MINIMIZED, EXPANDED, STACKED}
	
	private State contourState;
	
	private boolean showMemberTables;
	
	private boolean scrollLock;
	
	public ContourDiagramEditPart() {
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		updateContourState(prefs.getString(IJiveUIConstants.PREF_CONTOUR_STATE));
		updateShowMemberTables(prefs.getBoolean(IJiveUIConstants.PREF_SHOW_MEMBER_TABLES));
		updateScrollLock(prefs.getBoolean(IJiveUIConstants.PREF_SCROLL_LOCK));
		updateUpdateInterval(prefs.getLong(IJiveUIConstants.PREF_UPDATE_INTERVAL));
	}
	
	public IJiveDebugTarget getModel() {
		return (IJiveDebugTarget) super.getModel();
	}
	
	public State getContourState() {
		return contourState;
	}
	
	public boolean areShowMemberTables() {
		return showMemberTables;
	}
	
	private ContourGraph graph;
	
	private long updateInterval;
	
	private final Job updateJob = new Job("Update Job") {

		protected IStatus run(IProgressMonitor monitor) {
			synchronized (ContourDiagramEditPart.this) {
				try {
					if (hasModelChanged()) {
						update();
						setModelChanged(false);
					}
					
					return Status.OK_STATUS;
				}
				catch (Exception e) {
					JiveUIPlugin.log(e);
					return Status.OK_STATUS;
				}
				finally {
					schedule(updateInterval);
				}
			}
		}
		
	};
	
	private boolean hasModelChanged = false;
	
	private synchronized void setModelChanged(boolean flag) {
		hasModelChanged = flag;
	}
	
	private synchronized boolean hasModelChanged() {
		return hasModelChanged;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	public void activate() {
		super.activate();
		IJiveDebugTarget target = (IJiveDebugTarget) getModel();
		ContourModel model = target.getContourModel();
		model.addListener(this);
		
		DebugPlugin.getDefault().addDebugEventListener(this);
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(this);
		
		IThreadColorManager colorManager = JiveUIPlugin.getDefault().getThreadColorManager();
		colorManager.addThreadColorListener(this);
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.addStepListener(this);
		
		updateJob.setSystem(true);
		updateJob.schedule();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	public void deactivate() {
		IJiveDebugTarget target = (IJiveDebugTarget) getModel();
		ContourModel model = target.getContourModel();
		model.removeListener(this);
		
		DebugPlugin.getDefault().removeDebugEventListener(this);
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.removePropertyChangeListener(this);
		
		IThreadColorManager manager = JiveUIPlugin.getDefault().getThreadColorManager();
		manager.removeThreadColorListener(this);
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.removeStepListener(this);
		
		updateJob.cancel();
		setSelected(SELECTED_NONE);  // TODO Determine if this is needed
		super.deactivate();
	}
	
	@SuppressWarnings("unchecked")
	public void refresh() {
		IJiveDebugTarget target = (IJiveDebugTarget) getModel();
		graph = new ContourGraph(target.getContourModel());
		super.refresh();
	}
	
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure childFigure = ((GraphicalEditPart)childEditPart).getFigure();
		if (graph != null) {
			Contour c = (Contour) childEditPart.getModel();
			getFigure().add(childFigure, graph.getPosition(c), index);
		}
		else {
			System.out.println("The contour graph was not constructed.");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new ContourDiagramFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Determine what edit policies are needed
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List<Contour> getModelChildren() {
		IJiveDebugTarget target = (IJiveDebugTarget) getModel();
		ContourModel model = target.getContourModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			return model.getRoots();
		}
		finally {
			modelLock.unlock();
		}
	}

	public void contourAdded(final ContourModel model, Contour contour, Contour parent) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			setModelChanged(true);
		}
		finally {
			modelLock.unlock();
		}
	}

	public void contourRemoved(final ContourModel model, Contour contour, Contour oldParent) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			setModelChanged(true);
		}
		finally {
			modelLock.unlock();
		}
	}

	public void valueChanged(final ContourModel model, Contour contour, VariableID variableID, Value newValue, Value oldValue) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			setModelChanged(true);
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private void forceUpdate() {
		synchronized (ContourDiagramEditPart.this) {
			update();
			setModelChanged(false);
		}
	}
	
	private void update() {
		Display display = JiveUIPlugin.getStandardDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (isActive()) {
					IJiveDebugTarget target = (IJiveDebugTarget) getModel();
					ContourModel model = target.getContourModel();
					ReentrantLock modelLock = model.getModelLock();
					modelLock.lock();
					try {
						for (Object o : getChildren().toArray()) {
							EditPart part = (EditPart) o;
							removeChild(part);
						}
						
						refresh();
						
						if (!scrollLock) {
							revealLastMethodContour((JavaInteractiveContourModel) model);
						}
					}
					finally {
						modelLock.unlock();
					}
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void revealLastMethodContour(JavaInteractiveContourModel model) {
		ThreadID thread = model.lastThread();
		if (thread == null) {
			return;
		}
		
		try {
			MethodContour contour = model.peek(thread);
			EditPartViewer viewer = getRoot().getViewer();
			Map<Object, EditPart> editPartRegistry = viewer.getEditPartRegistry();
			if (editPartRegistry.containsKey(contour)) {
				EditPart editPart = editPartRegistry.get(contour);
				viewer.flush();
				viewer.reveal(editPart);
			}
		}
		catch (EmptyStackException e) {
			// do nothing
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			if (event.getKind() == DebugEvent.TERMINATE || event.getKind() == DebugEvent.SUSPEND) {
				forceUpdate();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(IJiveUIConstants.PREF_CONTOUR_STATE)) {
			updateContourState((String) event.getNewValue());
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_SHOW_MEMBER_TABLES)) {
			updateShowMemberTables((Boolean) event.getNewValue());
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_SCROLL_LOCK)) {
			updateScrollLock((Boolean) event.getNewValue());
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_UPDATE_INTERVAL)) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			updateUpdateInterval(prefs.getLong(IJiveUIConstants.PREF_UPDATE_INTERVAL));
		}
	}
	
	private void updateContourState(String state) {
		if (state.equals(IJiveUIConstants.PREF_CONTOUR_STATE_MINIMIZE)) {
			contourState = State.MINIMIZED;
		}
		else if (state.equals(IJiveUIConstants.PREF_CONTOUR_STATE_EXPAND)) {
			contourState = State.EXPANDED;
		}
		else if (state.equals(IJiveUIConstants.PREF_CONTOUR_STATE_STACK)) {
			contourState = State.STACKED;
		}
		else {
			// TODO Throw exception or log instead
			contourState = State.STACKED;
		}
	}
	
	private void updateShowMemberTables(boolean showMemberTables) {
		this.showMemberTables = showMemberTables; 
	}
	
	private void updateScrollLock(boolean scrollLock) {
		this.scrollLock = scrollLock;
	}
	
	private void updateUpdateInterval(long interval) {
		updateInterval = interval;
	}

	public void threadColorsChanged(IJiveDebugTarget target) {
		if (getModel() == target) {
			forceUpdate();
		}
	}

	public void steppingInitiated(IJiveDebugTarget target) {
		// TODO Auto-generated method stub
		
	}
	
	public void steppingCompleted(IJiveDebugTarget target) {
		if (getModel() == target) {
			forceUpdate();
		}
	}

//	/* (non-Javadoc)
//	 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#contourAdded(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.contour.Contour)
//	 */
//	public void contourAdded(final ContourModel model, final Contour contour, final Contour parent) {
//		Display display = JiveUIPlugin.getStandardDisplay();
//		if (display.getThread() == Thread.currentThread()) {
//			addContour(model, contour, parent);
//		}
//		else {
//			display.asyncExec(new Runnable() {
//				public void run() {
//					addContour(model, contour, parent);
//				}
//			});
//		}
//	}
//	
//	/**
//	 * Adds an edit part for the supplied contour as a child of the edit part of
//	 * the given parent.  This mehtod is called by
//	 * {@link #contourAdded(ContourModel, Contour, Contour)}.
//	 * 
//	 * @param model the model in which the contour was added
//	 * @param contour the contour that was added
//	 * @param parent the parent of the added contour
//	 */
//	private void addContour(ContourModel model, Contour contour, Contour parent) {
//		ReentrantLock modelLock = model.getModelLock();
//		modelLock.lock();
//		try {
//			if (parent == null) {
//				addRootContour(contour);
//			}
//			else {
//				ContourEditPart parentPart = getContourEditPart(parent);
//				parentPart.addChildContour(contour);
//			}
//		}
//		finally {
//			modelLock.unlock();
//		}
//	}
//	
//	/**
//	 * Adds an edit part of the supplied contour as a child element of the
//	 * contour diagram edit part.  In the contour model, such a contour does not
//	 * have a parent.
//	 *  
//	 * @param contour the contour that was added
//	 */
//	private void addRootContour(Contour contour) {
//		EditPart childPart = createChild(contour);
//		addChild(childPart, -1);
//	}
//	
//	/* (non-Javadoc)
//	 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#contourRemoved(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.contour.Contour)
//	 */
//	public void contourRemoved(final ContourModel model, final Contour contour, final Contour oldParent) {
//		Display display = JiveUIPlugin.getStandardDisplay();
//		if (display.getThread() == Thread.currentThread()) {
//			removeContour(model, contour, oldParent);
//		}
//		else {
//			display.asyncExec(new Runnable() {
//				public void run() {
//					removeContour(model, contour, oldParent);
//				}
//			});
//		}
//	}
//	
//	/**
//	 * Removes the edit part of the supplied contour, which is a child of the
//	 * edit part of the given parent.
//	 * 
//	 * @param model the model in which the contour was removed
//	 * @param contour the contour that was removed
//	 * @param oldParent the parent of the removed contour
//	 */
//	private void removeContour(ContourModel model, Contour contour, Contour oldParent) {
//		ReentrantLock modelLock = model.getModelLock();
//		modelLock.lock();
//		try {
//			if (oldParent == null) {
//				removeRootContour(contour);
//			}
//			else {
//				ContourEditPart parentPart = getContourEditPart(oldParent);
//				parentPart.removeChildContour(contour);
//			}
//		}
//		finally {
//			modelLock.unlock();
//		}
//	}
//	
//	/**
//	 * Removes the edit part for the supplied contour, which is a child of the
//	 * contour diagram edit part.  In the contour model, such a contour does not
//	 * have a parent.
//	 * 
//	 * @param contour the contour that was removed
//	 */
//	private void removeRootContour(Contour contour) {
//		EditPart childPart = getContourEditPart(contour);
//		removeChild(childPart);
//	}
//
//	/* (non-Javadoc)
//	 * @see edu.bsu.cs.jive.contour.ContourModel.Listener#valueChanged(edu.bsu.cs.jive.contour.ContourModel, edu.bsu.cs.jive.contour.Contour, edu.bsu.cs.jive.util.VariableID, edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
//	 */
//	public void valueChanged(final ContourModel model, final Contour contour, final VariableID variableID, final Value newValue, final Value oldValue) {
//		Display display = JiveUIPlugin.getStandardDisplay();
//		if (display.getThread() == Thread.currentThread()) {
//			changeValue(model, contour, variableID, newValue, oldValue);
//		}
//		else {
//			display.asyncExec(new Runnable() {
//				public void run() {
//					changeValue(model, contour, variableID, newValue, oldValue);
//				}
//			});
//		}
//	}
//	
//	// TODO Determine if there is a more efficient way of accomplishing this
//	/**
//	 * Updates the supplied contour to reflect the change to made to the given
//	 * variable.  This includes updating the contour's member table and links. 
//	 * 
//	 * @param model the model in which the variable change was made
//	 * @param contour the contour containing the changed variable
//	 * @param variableID the variable that was changed
//	 * @param newValue the value to which the variable was changed
//	 * @param oldValue the previous value of the changed variable
//	 */
//	private void changeValue(ContourModel model, Contour contour, VariableID variableID, Value newValue, Value oldValue) {
//		ReentrantLock modelLock = model.getModelLock();
//		modelLock.lock();
//		try {
//			if (newValue instanceof Value.ContourReference) {
//				Value.ContourReference ref = (Value.ContourReference) newValue;
//				ContourID id = ref.getContourID();
//				if (model.contains(id)) {
//					ContourEditPart part = getContourEditPart(model.getContour(id));
//					part.refresh();
//				}
//				// TODO Otherwise, do we need to search for the edit part?
//				else {
//					System.out.println("ContourDiagramEditPart#changeValue : newValue = " + id);
//				}
//			}
//			
//			if (oldValue instanceof Value.ContourReference) {
//				Value.ContourReference ref = (Value.ContourReference) oldValue;
//				ContourID id = ref.getContourID();
//				if (model.contains(id)) {
//					ContourEditPart part = getContourEditPart(model.getContour(id));
//					part.refresh();
//				}
//				// TODO Otherwise, do we need to search for the edit part?
//				else {
//					System.out.println("ContourDiagramEditPart#changeValue : oldValue = " + id);
//				}
//			}
//			
//			ContourEditPart part = getContourEditPart(contour);
//			if (part != null) {
//				part.refresh();
//			}
//			else {
//				System.out.println("ContourDiagramEditPart#changeValue : contour = " + contour.id());
//			}
//		}
//		finally {
//			modelLock.unlock();
//		}
//	}
}
