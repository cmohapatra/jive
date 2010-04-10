package edu.buffalo.cse.jive.ui.search;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.text.Match;

import edu.bsu.cs.jive.events.Event;
import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;

/**
 * An abstract search query over the execution history of an
 * {@code IJiveDebugTarget}.  The query implements the
 * {@code SequenceModel.EventVisitor} interface, but leaves the details up to
 * extending classes.  This class is responsible for traversing the sequence
 * model and periodically checking if the user has canceled the query.  It also
 * provides a mechanism for adding matches to the result set.
 * 
 * @see #addMatch(EventOccurrence)
 * @author Jeffrey K Czyz
 */
public abstract class ExecutionHistorySearchQuery extends AbstractJiveSearchQuery implements SequenceModel.EventVisitor {
	
	/**
	 * Creates an execution history query.
	 */
	protected ExecutionHistorySearchQuery() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#performSearch(org.eclipse.core.runtime.IProgressMonitor, edu.buffalo.cse.jive.core.IJiveDebugTarget)
	 */
	public IStatus performSearch(IProgressMonitor monitor, IJiveDebugTarget target) {
		SequenceModel model = target.getSequenceModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			model.accept(new EventVisitorProxy(monitor));
			return new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Completed searching " + target + ".");
		}
		catch (OperationCanceledException e) {
			return new Status(IStatus.CANCEL, JiveUIPlugin.PLUGIN_ID, "Search canceled by the user.");
		}
		catch (Exception e) {
			return new Status(IStatus.ERROR, JiveUIPlugin.PLUGIN_ID, "An error occurred while searching.", e);
		}
		finally {
			modelLock.unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultType()
	 */
	public Class<? extends Object> getResultType() {
		return Event.class;
	}
	
	/**
	 * A {@code SequenceModel.EventVisitor} that delegates to the enclosing
	 * class and periodically checks for user cancellation.
	 * 
	 * @author Jeffrey K Czyz
	 */
	private class EventVisitorProxy implements SequenceModel.EventVisitor {

		/**
		 * Frequency in milliseconds for checking for user cancellation.
		 */
		private static final int CANCELLATION_CHECK_FREQUENCY = 500;
		
		/**
		 * The number of events visited.
		 */
		private int visitCount = 0;
		
		/**
		 * The progress monitor to check for cancellation.
		 */
		private IProgressMonitor monitor;
		
		/**
		 * Constructs a new {@code EventVisitorProxy} with the given progress
		 * monitor.
		 * 
		 * @param monitor the progress monitor to check for cancellation
		 */
		public EventVisitorProxy(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
		 */
		public void visit(EventOccurrence event) {
			ExecutionHistorySearchQuery.this.visit(event);
			checkForCancellation();
		}

		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.MessageSend)
		 */
		public void visit(MessageSend event) {
			ExecutionHistorySearchQuery.this.visit(event);
			checkForCancellation();
		}
		
		/**
		 * Checks for user cancellation after a set number of events have been
		 * traversed. 
		 */
		private void checkForCancellation() {
			visitCount++;
			if (visitCount == CANCELLATION_CHECK_FREQUENCY) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				else {
					visitCount = 0;
				}
			}
		}
	}
	
	/**
	 * Adds a search query match to the search result.
	 * 
	 * @param event the match to add to the search result
	 */
	protected void addMatch(EventOccurrence event) {
		JiveSearchResult result = (JiveSearchResult) getSearchResult();
		Match match = new Match(event, 0, 1);
		result.addMatch(match);
	}
}
