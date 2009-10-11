package edu.buffalo.cse.jive.internal.ui.views.eventlog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.events.EOSEvent;
import edu.bsu.cs.jive.events.Event;
import edu.bsu.cs.jive.events.EventSource;
import edu.bsu.cs.jive.events.ExceptionEvent;
import edu.bsu.cs.jive.events.ExitEvent;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.StartEvent;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.internal.ui.views.IJiveTableRowLabelProvider;
import edu.buffalo.cse.jive.ui.AbstractStructuredJiveView;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;

/**
 * A view part used to present an <code>IJiveEventLog</code> of an
 * <code>IJiveDebugTarget</code>.  The view is in tabular form with columns
 * for the thread in which the event occurs, the event number, the event name,
 * and the details of the event.
 * 
 * @author Jeffrey K Czyz
 */
public class JiveEventLogView extends AbstractStructuredJiveView {
	
	/**
	 * Constructs the view.
	 */
	public JiveEventLogView() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createContentProvider()
	 */
	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new JiveEventLogContentProvider();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new JiveEventLogLabelProvider();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createSorter()
	 */
	@Override
	protected ViewerSorter createSorter() {
		// TODO Add ability to sort by different columns
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected StructuredViewer createViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		
		String[] columnNames = {"Thread", "Number", "Event", "Details" };
		int[] columnAlignments = {SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
		int[] columnWidths = {60, 50, 110, 260};
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, columnAlignments[i]);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
		}
		
		return viewer;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDefaultContentDescription()
	 */
	@Override
	protected String getDefaultContentDescription() {
		return "No event logs to display at this time.";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayTargetDropDownText()
	 */
	@Override
	protected String getDisplayTargetDropDownText() {
		return "Display Event Log";
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayDropDownEnabledImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getDisplayDropDownEnabledImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_LIST_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView#getDisplayDropDownDisabledImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getDisplayDropDownDisabledImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.DISABLED_LIST_ICON_KEY);
	}
	
	/**
	 * An implementation of an <code>AbstractJiveContentProvider</code> that
	 * provides JIVE events as model elements.
	 * 
	 * @see edu.bsu.cs.jive.events.Event
	 * @author Jeffrey K Czyz
	 */
	protected class JiveEventLogContentProvider extends AbstractJiveContentProvider implements EventSource.Listener {
		
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
			return target.getJiveEventLog().getEvents();
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#registerWithModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void registerWithModel(IJiveDebugTarget newInput) {
			newInput.getJiveEventLog().addListener(this);
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.internal.ui.views.AbstractJiveView.AbstractJiveContentProvider#unregisterFromModel(edu.buffalo.cse.jive.core.IJiveDebugTarget)
		 */
		@Override
		protected void unregisterFromModel(IJiveDebugTarget oldInput) {
			oldInput.getJiveEventLog().removeListener(this);
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.EventSource.Listener#eventOccurred(edu.bsu.cs.jive.events.EventSource, edu.bsu.cs.jive.events.Event)
		 */
		public void eventOccurred(EventSource source, final Event event) {
			Display display = JiveUIPlugin.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					TableViewer viewer = (TableViewer) getViewer();
					if (!viewer.getControl().isDisposed()) {
						viewer.add(event);
					}
				}
			});
		}
	}
	
	// TODO Move to separate class
	/**
	 * An <code>ITableLabelProvider</code> for JIVE events.
	 * 
	 * @see edu.bsu.cs.jive.events.Event
	 * @author Jeffrey K Czyz
	 */
	public static class JiveEventLogLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		/**
		 * The last event for which a label was provided.  This is used to
		 * determine if the event should be exported.  Labels are provided cell
		 * by cell (not row by row), so we do not want to export the same event
		 * more than once.
		 */
		private Event lastEvent = null;
		
		/**
		 * The the label provider that was used for the last event. 
		 */
		protected IJiveTableRowLabelProvider labelProvider = null;
		
		/**
		 * A label provider for {@code StartEvent}s. 
		 */
		protected StartEventLabelProvider startEventExporter = new StartEventLabelProvider();
		
		/**
		 * A label provider for {@code ExitEvent}s. 
		 */
		protected ExitEventLabelProvider exitEventExporter = new ExitEventLabelProvider();
		
		/**
		 * A label provider for {@code LoadEvent}s.
		 */
		protected LoadEventLabelProvider loadEventExporter = new LoadEventLabelProvider();
		
		/**
		 * A label provider for {@code NewEvent}s. 
		 */
		protected NewEventLabelProvider newEventExporter = new NewEventLabelProvider();
		
		/**
		 * A label provider for {@code CallEvent}s. 
		 */
		protected CallEventLabelProvider callEventExporter = new CallEventLabelProvider();
		
		/**
		 * A label provider for {@code ReturnEvent}s. 
		 */
		protected ReturnEventLabelProvider returnEventExporter = new ReturnEventLabelProvider();
		
		/**
		 * A label provider for {@code ThrowEvent}s.
		 */
		protected ThrowEventLabelProvider throwEventExporter = new ThrowEventLabelProvider();
		
		/**
		 * A label provider for {@code CatchEvent}s.
		 */
		protected CatchEventLabelProvider catchEventExporter = new CatchEventLabelProvider();
		
		/**
		 * A label provider for {@code EOSEvent}s. 
		 */
		protected EOSEventLabelProvider eosEventExporter = new EOSEventLabelProvider();
		
		/**
		 * A label provider for {@code AssignEvent}s. 
		 */
		protected AssignEventLabelProvider assignEventExporter = new AssignEventLabelProvider();
		
		/**
		 * An event visitor used to export events.
		 */
		private Event.Visitor visitor = new Event.Visitor() {

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
		 * Exports the event if it hasn't been done already.
		 * 
		 * @param event the event to export
		 */
		protected void exportEvent(Event event) {
			if (event != lastEvent) {
				event.accept(visitor, null);
				lastEvent = event;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			exportEvent((Event) element);
			return labelProvider.getColumnText(columnIndex);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			exportEvent((Event) element);
			return labelProvider.getColumnImage(columnIndex);
		}
	}
}
