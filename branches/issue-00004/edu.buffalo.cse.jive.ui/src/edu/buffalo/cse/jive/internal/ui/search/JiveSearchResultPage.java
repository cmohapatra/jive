package edu.buffalo.cse.jive.internal.ui.search;

import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.IPageSite;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.IStepManager;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunPastEventAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.RunToEventAction;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.ContourDiagramView;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.AssignEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.CallEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.CatchEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.EOSEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.JiveEventLogView;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.NewEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.ReturnEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.eventlog.ThrowEventLabelProvider;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.SequenceDiagramView;
import edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts.SequenceDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.views.sequence.model.SequenceModelView;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.IJiveSearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchResult;
import edu.buffalo.cse.jive.ui.search.queries.InvariantViolatedSearchQuery;

/**
 * 
 * @author Jeffrey K Czyz
 * @author Dennis Patrone
 */
public class JiveSearchResultPage extends AbstractTextSearchViewPage {
	
	protected static final String LAYOUT_GROUP = "layoutGroup";
	
	protected StructuredViewer viewer;
	
	protected Class<? extends Object> resultType;
	
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		
		IMenuManager menuManager = pageSite.getActionBars().getMenuManager();
		menuManager.insertBefore(IContextMenuConstants.GROUP_PROPERTIES, new Separator(LAYOUT_GROUP));
		
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new Action("Preferences...") {
			public void run() {
				String pageId = "org.eclipse.search.preferences.SearchPreferencePage"; //$NON-NLS-1$
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				PreferencesUtil.createPreferenceDialogOn(shell, pageId, null, null).open();
			}
		});
	}
	
	public void setInput(ISearchResult newSearch, Object viewState) {
		super.setInput(newSearch, viewState);
		
		if (newSearch != null) {
			if (getLayout() == FLAG_LAYOUT_FLAT) {
				JiveSearchResult result = (JiveSearchResult) newSearch;
				IJiveSearchQuery query = (IJiveSearchQuery) result.getQuery();
				resultType = query.getResultType();
				setLayout(FLAG_LAYOUT_TREE);
				setLayout(FLAG_LAYOUT_FLAT);
			}
			
			
			
			// Display the sequence diagram and notify it of changing results
			IViewPart viewPart = showView(IJiveUIConstants.SEQUENCE_DIAGRAM_VIEW_ID);
			SequenceDiagramView diagram = (SequenceDiagramView) viewPart;
			if (diagram.getDisplayed() != null) {
				GraphicalViewer viewer = diagram.getViewer();
				SequenceDiagramEditPart contents = (SequenceDiagramEditPart) viewer.getContents();
				contents.queryFinished(newSearch.getQuery());
			}
		}
	}
	
	protected void showMatch(Match match, int currentOffset, int currentLength, boolean activate) throws PartInitException {
		if (match != null) {
			EventOccurrence event = (EventOccurrence) match.getElement();
			showInSequenceDiagram(event);
			showInContourDiagram(event.containingExecution().context());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void showInSequenceDiagram(EventOccurrence event) {
		IViewPart viewPart = showView(IJiveUIConstants.SEQUENCE_DIAGRAM_VIEW_ID);
		SequenceDiagramView diagram = (SequenceDiagramView) viewPart;
		
		// TODO Find and display the correct target instead (?)
		SequenceModel model = event.containingExecution().containingModel();
		IJiveDebugTarget target = diagram.getDisplayed();
		if (target != null && target.getSequenceModel() == model) {
			IStepManager stepManager = JiveUIPlugin.getDefault().getStepManager();
			IStepAction action;
			
			if (event.underlyingEvent() instanceof EOSEvent) {
				action = new RunPastEventAction(target, event.underlyingEvent());
			}
			else if (event.underlyingEvent() instanceof CatchEvent) {
				action = new RunPastEventAction(target, event.underlyingEvent());
			}
			else {
				action = new RunToEventAction(target, event.underlyingEvent());
			}
			
			stepManager.pause(target);
			stepManager.run(target, action);
			
			GraphicalViewer viewer = diagram.getViewer();
			Map<Object, EditPart> editPartRegistry = viewer.getEditPartRegistry();
			if (editPartRegistry.containsKey(event)) {
				EditPart editPart = editPartRegistry.get(event);
				viewer.reveal(editPart);
			}
		}
	}
	
	private void showInContourDiagram(ContourID id) {
		IViewPart viewPart = showView(IJiveUIConstants.CONTOUR_DIAGRAM_VIEW_ID);
		ContourDiagramView diagram = (ContourDiagramView) viewPart;
		
		// TODO Find and display the correct target
	}
	
	private IViewPart showView(String viewId) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			return page.showView(viewId);
		}
		catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method is called whenever the set of matches for the given elements
	 * changes. This method is guaranteed to be called in the UI thread. Note
	 * that this notification is asynchronous. i.e. further changes may have
	 * occurred by the time this method is called. They will be described in a
	 * future call.
	 * 
	 * @param objects
	 *            array of objects that has to be refreshed
	 */
	protected void elementsChanged(Object[] objects) {
		viewer.refresh();
	}

	/**
	 * This method is called whenever all elements have been removed from the
	 * shown <code>AbstractSearchResult</code>. This method is guaranteed to
	 * be called in the UI thread. Note that this notification is asynchronous.
	 * i.e. further changes may have occurred by the time this method is called.
	 * They will be described in a future call.
	 */
	protected void clear() {
		viewer.refresh();
	}

	/**
	 * Configures the given viewer. Implementers have to set at least a content
	 * provider and a label provider. This method may be called if the page was
	 * constructed with the flag <code>FLAG_LAYOUT_TREE</code>.
	 * 
	 * @param viewer
	 *            the viewer to be configured
	 */
	protected void configureTreeViewer(TreeViewer viewer) {
		this.viewer = viewer;
		viewer.setContentProvider(new JiveSearchTreeContentProvider(viewer));
		viewer.setLabelProvider(new SequenceModelView.SequenceModelLabelProvider());
	}

	/**
	 * Configures the given viewer. Implementers have to set at least a content
	 * provider and a label provider. This method may be called if the page was
	 * constructed with the flag <code>FLAG_LAYOUT_FLAT</code>.
	 * 
	 * @param viewer
	 *            the viewer to be configured
	 */
	protected void configureTableViewer(TableViewer viewer) {
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		if (resultType == null) {
			configureDefaultTable(table);
		}
		else if (resultType.equals(AssignEvent.class)) {
			configureAssignEventTable(table);
		}
		else if (resultType.equals(CallEvent.class)) {
			configureCallEventTable(table);
		}
		else if (resultType.equals(CatchEvent.class)) {
			configureCatchEventTable(table);
		}
		else if (resultType.equals(EOSEvent.class)) {
			configureEOSEventTable(table);
		}
		else if (resultType.equals(NewEvent.class)) {
			configureNewEventTable(table);
		}
		else if (resultType.equals(ReturnEvent.class)) {
			configureReturnEventTable(table);
		}
		else if (resultType.equals(ThrowEvent.class)) {
			configureThrowEventTable(table);
		}
		else if (resultType.equals(InvariantViolatedSearchQuery.class)) {
			configureInvariantViolatedTable(table);
		}
		else {
			configureDefaultTable(table);
		}
		
		this.viewer = viewer;
		viewer.setContentProvider(new JiveSearchTableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		//throw new InternalError("Jive search does not handle table viewers");
	}
	
	private void configureDefaultTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Details" };
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 260};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureAssignEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Context", "Variable", "Value"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT};
		int[] columnWidths = {60, 50, 110, 120, 80, 60};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureCallEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Caller", "Method", "Actual Parameters"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 120, 120, 120};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureCatchEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Catcher", "Variable", "Exception"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 140, 80, 140};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureEOSEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Filename", "Line"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.RIGHT};
		int[] columnWidths = {60, 50, 110, 200, 60};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureNewEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Object" };
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 200};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureReturnEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event",  "Method", "Return Value"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.RIGHT};
		int[] columnWidths = {60, 50, 110, 120, 120, 120};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureThrowEventTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event", "Thrower", "Exception", "Frame Popped?"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 140, 140, 90};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
	
	private void configureInvariantViolatedTable(Table table) {
		String[] columnNames = {"Thread", "Number", "Event",  "Violated At"};
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 120, 120};
		configureTable(table, columnNames, columnAlignments, columnWidths);
	}
		
	private void configureTable(Table table, String[] columnNames, int[] columnAlignments, int[] columnWidths) {
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, columnAlignments[i]);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}
	}
	
	// TODO this is temporary
	private class TableLabelProvider extends JiveEventLogView.JiveEventLogLabelProvider {
		
		public TableLabelProvider() {
			super();
			assignEventExporter = new AssignEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // context
						return fContext;
					case 4:
						return fVariable;
					case 5:
						return fValue;
					default:
						return "";
					}
				}
			};
			
			callEventExporter = new CallEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // caller
						return fCaller;
					case 4:
						return fTarget;
					case 5:
						return fActuals;
					default:
						return "";
					}
				}
			};
			
			returnEventExporter = new ReturnEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // returner
						return fReturner;
					case 4:
						return fValue;
					default:
						return "";
					}
				}
			};
			
			eosEventExporter = new EOSEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // filename
						return fFile;
					case 4:
						return fLine;
					default:
						return "";
					}
				}
			};
			
			newEventExporter = new NewEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // object
						return fObject;
					default:
						return "";
					}
				}
			};
			
			throwEventExporter = new ThrowEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // thrower
						return fThrower;
					case 4:
						return fException;
					case 5:
						return fFramePopped;
					default:
						return "";
					}
				}
			};
			
			catchEventExporter = new CatchEventLabelProvider() {
				/* (non-Javadoc)
				 * @see edu.buffalo.cse.jive.internal.ui.views.eventlog.ITableRowLabelProvider#getColumnText(int)
				 */
				public String getColumnText(int columnIndex) {
					switch (columnIndex) {
					case THREAD_NAME_COLUMN:
						return getThreadName();
					case EVENT_NUMBER_COLUMN:
						return getEventNumber();
					case EVENT_NAME_COLUMN:
						return getEventName();
					case EVENT_DETAILS_COLUMN:  // thrower
						return fCatcher;
					case 4:
						return fVariable;
					case 5:
						return fException;
					default:
						return "";
					}
				}
			};
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			EventOccurrence event = (EventOccurrence) element;
			exportEvent(event.underlyingEvent());
			return labelProvider.getColumnText(columnIndex);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			EventOccurrence event = (EventOccurrence) element;
			exportEvent(event.underlyingEvent());
			return labelProvider.getColumnImage(columnIndex);
		}
	}
}
