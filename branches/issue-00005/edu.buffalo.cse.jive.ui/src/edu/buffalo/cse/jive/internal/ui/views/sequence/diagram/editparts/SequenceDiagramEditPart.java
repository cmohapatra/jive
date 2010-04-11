package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.search.ui.text.RemoveAllEvent;
import org.eclipse.swt.widgets.Display;

import edu.bsu.cs.jive.contour.InteractiveContourModel;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepListener;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorListener;
import edu.buffalo.cse.jive.internal.ui.views.sequence.IThreadColorManager;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.figures.SequenceDiagramFigure;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchResult;

/**
 * 
 * @author Jeffrey K Czyz
 * @author Nirupama Chakravarti
 *
 */
public class SequenceDiagramEditPart extends AbstractGraphicalEditPart
implements SequenceModel.Listener, IDebugEventSetListener, IPropertyChangeListener, IQueryListener, ISearchResultListener, IStepListener, IThreadColorListener {
	
	private CompactionManager compactionManager;
	
	private ModelManager modelManager;
	
	public SequenceDiagramEditPart() {
		compactionManager = new CompactionManager();
		modelManager = new ModelManager(compactionManager);
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		updateThreadActivationsState(prefs.getBoolean(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS));
		updateLifelinesState(prefs.getBoolean(IJiveUIConstants.PREF_EXPAND_LIFELINES));
		updateUpdateInterval(prefs.getLong(IJiveUIConstants.PREF_UPDATE_INTERVAL));
	}
	
	public IJiveDebugTarget getModel() {
		return (IJiveDebugTarget) super.getModel();
	}
	
	List<ExecutionOccurrence> getLifelineChildren(ContourID id) {
		return modelManager.getLifelineChildren(id);
	}
	
	List<ExecutionOccurrence> getLifelineChildren(ThreadID id) {
		IJiveDebugTarget target = getModel();
		final MultiThreadedSequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			return Collections.singletonList(model.getRoot(id));
		}
		finally {
			modelLock.unlock();
		}
	}
	
	List<Message> getSourceMessages(ExecutionOccurrence execution) {
		return modelManager.getSourceConnections(execution);
	}
	
	List<Message> getTargetMessages(ExecutionOccurrence execution) {
		return modelManager.getTargetConnections(execution);
	}
	
	List<Message> getFoundMessages(ContourID context) {
		return modelManager.getSourceConnections(context);
	}
	
	List<Message> getLostMessages(ContourID context) {
		return modelManager.getTargetConnections(context);
	}
	
	boolean isBroken(Message message) {
		return modelManager.isBroken(message);
	}
	
	long getFilteredMessageEventNumber(Message message) {
		return modelManager.getFilteredMessageEventNumber(message);
	}

	public void collapseExecution(ExecutionOccurrence execution) {
		compactionManager.collapseExecution(execution);
		forceUpdate();
	}
	
	public void expandExecution(ExecutionOccurrence execution) {
		compactionManager.expandExecution(execution);
		forceUpdate();
	}
	
	public boolean isCollapsed(ExecutionOccurrence execution) {
		return compactionManager.isCollapsed(execution);
	}
	
	public int calculateExecutionStart(ExecutionOccurrence execution) {
		long eventNumber = execution.events().get(0).underlyingEvent().number();
		return compactionManager.calculateAdjustedPosition(eventNumber);
	}
	
	public int calculateExecutionLength(ExecutionOccurrence execution) {
		long startEventNumber = execution.events().get(0).underlyingEvent().number();
		long endEventNumber = startEventNumber + execution.duration() - 1;
		
		int startPosition = compactionManager.calculateAdjustedPosition(startEventNumber);
		int endPosition = compactionManager.calculateAdjustedPosition(endEventNumber);
		
		return endPosition - startPosition + 1;
	}
	
	public int calculateAdjustedEventNumber(long currentEventNumber) {
		return compactionManager.calculateAdjustedPosition(currentEventNumber);
	}
	
	private long updateInterval;
	
	private final Job updateJob = new Job("Update Job") {

		protected IStatus run(IProgressMonitor monitor) {
			synchronized (SequenceDiagramEditPart.this) {
				try {
					if (hasModelChanged()) {
						update();
						setModelChanged(false);
					}
					
					return Status.OK_STATUS;
				}
				catch(Exception e) {
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
		IJiveDebugTarget target = getModel();
		SequenceModel model = target.getSequenceModel();
		model.addListener(this);
		
		DebugPlugin.getDefault().addDebugEventListener(this);
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener(this);
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.addStepListener(this);
		
		IThreadColorManager threadColorManager = JiveUIPlugin.getDefault().getThreadColorManager();
		threadColorManager.addThreadColorListener(this);
		
		NewSearchUI.addQueryListener(this);
		
		updateJob.setSystem(true);
		updateJob.schedule();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	public void deactivate() {
		IJiveDebugTarget target = getModel();
		SequenceModel model = target.getSequenceModel();
		model.removeListener(this);
		
		DebugPlugin.getDefault().removeDebugEventListener(this);
		
		Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
		prefs.removePropertyChangeListener(this);
		
		IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
		stepManager.removeStepListener(this);
		
		IThreadColorManager threadColorManager = JiveUIPlugin.getDefault().getThreadColorManager();
		threadColorManager.removeThreadColorListener(this);
		
		NewSearchUI.removeQueryListener(this);
		
		updateJob.cancel();
		setSelected(SELECTED_NONE);  // TODO Determine if this is needed
		super.deactivate();
	}
	
	protected IFigure createFigure() {
		return new SequenceDiagramFigure();
	}

	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	protected void refreshVisuals() {
		SequenceDiagramFigure figure = (SequenceDiagramFigure) getFigure();
		IJiveDebugTarget target = getModel();
		
		if (target.isTerminated() || target.isDisconnected() || target.isSuspended()) {
			InteractiveContourModel model = target.getContourModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				if (model.readyToRecord()) {
					int eventNumber = getLastEventNumber(target);
					figure.setCurrentEventNumber(eventNumber);
				}
				else if (model.canStepBackward()) {
					int transactionIndex = model.getPrevTransactionIndex();
					long eventNumber = model.getEventNumber(transactionIndex);
					eventNumber = calculateAdjustedEventNumber(eventNumber);
					figure.setCurrentEventNumber((int) eventNumber);
				}
				else {
					figure.setCurrentEventNumber(0);
				}
			}
			finally {
				modelLock.unlock();
			}
		}
		else {
			int eventNumber = getLastEventNumber(target);
			figure.setCurrentEventNumber(eventNumber);
		}
	}
	
	private int getLastEventNumber(IJiveDebugTarget target) {
		SequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			long result = calculateAdjustedEventNumber(model.lastEventNumber());
			return (int) result;
		}
		finally {
			modelLock.unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List<Object> getModelChildren() {
		IJiveDebugTarget target = getModel();
		final MultiThreadedSequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			modelManager.computeRelationships(model);
			return modelManager.getLifelines();
		}
		finally {
			modelLock.unlock();
		}
	}

	public void eventAdded(final SequenceModel model, EventOccurrence event, ExecutionOccurrence execution) {
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			setModelChanged(true);
		}
		finally {
			modelLock.unlock();
		}
	}

	public void executionAdded(final SequenceModel model, ExecutionOccurrence execution, MessageSend initiator) {
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
		synchronized (SequenceDiagramEditPart.this) {
			update();
			setModelChanged(false);
		}
	}
	
	private void update() {
		Display display = JiveUIPlugin.getStandardDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (isActive()) {
					IJiveDebugTarget target = getModel();
					SequenceModel model = target.getSequenceModel();
					ReentrantLock modelLock = model.getModelLock();
					modelLock.lock();
					try {
						for (Object o : getChildren().toArray()) {
							EditPart part = (EditPart) o;
							removeChild(part);
						}
						
						refresh();
					}
					finally {
						modelLock.unlock();
					}
				}
			}
		});	
	}

	private Map<ExecutionOccurrence, List<EventOccurrence>> searchResultMap = new HashMap<ExecutionOccurrence, List<EventOccurrence>>();
	
	public List<EventOccurrence> getSearchResults(ExecutionOccurrence execution) {
		if (searchResultMap.containsKey(execution)) {
			return searchResultMap.get(execution);
		}
		else {
			return Collections.emptyList();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(IJiveUIConstants.PREF_SHOW_THREAD_ACTIVATIONS)) {
			updateThreadActivationsState((Boolean) event.getNewValue());
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_EXPAND_LIFELINES)) {
			updateLifelinesState((Boolean) event.getNewValue());
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_ACTIVATION_WIDTH)) {
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_EVENT_HEIGHT)) {
			forceUpdate();
		}
		else if (property.equals(IJiveUIConstants.PREF_UPDATE_INTERVAL)) {
			Preferences prefs = JiveUIPlugin.getDefault().getPluginPreferences();
			updateUpdateInterval(prefs.getLong(IJiveUIConstants.PREF_UPDATE_INTERVAL));
		}
	}
	
	private void updateThreadActivationsState(boolean showThreadActivations) {
		modelManager.setShowThreadActivations(showThreadActivations);
	}
	
	private void updateLifelinesState(boolean expandedLifelines) {
		modelManager.setExpandLifelines(expandedLifelines);
	}
	
	private void updateUpdateInterval(long interval) {
		updateInterval = interval;
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
				forceUpdate();
				break;
			}
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

	public void threadColorsChanged(IJiveDebugTarget target) {
		if (getModel() == target) {
			forceUpdate();
		}
	}

	public void queryAdded(ISearchQuery query) {}

	public void queryFinished(ISearchQuery query) {
		searchResultMap.clear();
		
		if (query instanceof IJiveSearchQuery) {
			updateSearchResults((IJiveSearchQuery) query);
			query.getSearchResult().addListener(this);
			// TODO Keep track of result in order to remove listener in case the view is closed
		}
		
		forceUpdate();
	}

	public void queryRemoved(ISearchQuery query) {
		if (query instanceof IJiveSearchQuery) {
			searchResultMap.clear();
			forceUpdate();
			query.getSearchResult().removeListener(this);
		}
	}

	public void queryStarting(ISearchQuery query) {}

	public void searchResultChanged(SearchResultEvent e) {
		ISearchResult result = e.getSearchResult();
		if (result instanceof JiveSearchResult) {
			if (e instanceof MatchEvent) {
				MatchEvent matchEvent = (MatchEvent) e;
				switch (matchEvent.getKind()) {
				case MatchEvent.ADDED:
					addSearchResults(matchEvent.getMatches());
					break;
				case MatchEvent.REMOVED:
					removeSearchResults(matchEvent.getMatches());
					break;
				}
				
				forceUpdate();
			}
			else if (e instanceof RemoveAllEvent) {
				searchResultMap.clear();
				forceUpdate();
			}
		}
	}
	
	private void updateSearchResults(IJiveSearchQuery query) {
		ISearchResult result = query.getSearchResult();
		JiveSearchResult jiveResult = (JiveSearchResult) result;
		for (Object element : jiveResult.getElements()) {
			addSearchResults(jiveResult.getMatches(element));
		}
	}
	
	private void addSearchResults(Match[] matches) {
		IJiveDebugTarget target = getModel();
		SequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			for (Match match : matches) {
				EventOccurrence event = (EventOccurrence) match.getElement();
				ExecutionOccurrence execution = event.containingExecution();
				
				if (!searchResultMap.containsKey(execution)) {
					searchResultMap.put(execution, new LinkedList<EventOccurrence>());
				}
				
				searchResultMap.get(execution).add(event);
			}
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private void removeSearchResults(Match[] matches) {
		IJiveDebugTarget target = getModel();
		SequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			for (Match match : matches) {
				EventOccurrence event = (EventOccurrence) match.getElement();
				ExecutionOccurrence execution = event.containingExecution();
				
				if (searchResultMap.containsKey(execution)) {
					List<EventOccurrence> results = searchResultMap.get(execution);
					results.remove(event);
					if (results.isEmpty()) {
						searchResultMap.remove(execution);
					}
				}
			}
		}
		finally {
			modelLock.unlock();
		}
	}
}
