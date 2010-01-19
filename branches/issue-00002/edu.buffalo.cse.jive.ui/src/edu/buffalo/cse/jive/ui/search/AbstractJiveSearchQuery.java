package edu.buffalo.cse.jive.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import edu.buffalo.cse.jive.core.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;

/**
 * An abstract implementation of an {@code IJiveSearchQuery}.  This class
 * handles much of the common implementation details required by the
 * {@code ISearchQuery} interface.  The
 * {@link ISearchQuery#run(IProgressMonitor)} method is implemented to call
 * {@link IJiveSearchQuery#performSearch(IProgressMonitor, IJiveDebugTarget)}
 * for each available target.  It also handles updating the progress monitor
 * and canceling searches.
 * 
 * @see ISearchQuery
 * @see IJiveSearchQuery
 * @see IProgressMonitor
 * @author Jeffrey K Czyz
 */
public abstract class  AbstractJiveSearchQuery implements IJiveSearchQuery {
	
	/**
	 * The search result associated with the query.
	 */
	private IJiveSearchResult result;

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		((AbstractTextSearchResult)result ).removeAll(); // TODO Make the result an IQueryListener instead
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		List<IJiveDebugTarget> targetList = new ArrayList<IJiveDebugTarget>();

		for (IDebugTarget target : manager.getDebugTargets()) {
			if (target instanceof IJiveDebugTarget) {
				targetList.add((IJiveDebugTarget) target);
			}
		}
		
		monitor.beginTask("Searching for events...", targetList.size());
		
		for (IJiveDebugTarget target : targetList) {
			IStatus status = performSearch(monitor, target);
			switch (status.getSeverity()) {
			case IStatus.OK:
			case IStatus.INFO:
				monitor.worked(1);
				break;
			case IStatus.WARNING:
			case IStatus.ERROR:
				JiveUIPlugin.log(status);
				monitor.worked(1);
				break;
			case IStatus.CANCEL:
				monitor.done();
				return status;
			}
			
			if (monitor.isCanceled()) {
				monitor.done();
				return new Status(IStatus.CANCEL, JiveUIPlugin.PLUGIN_ID, "Search canceled by the user.");
			}
		}
		
		monitor.done();
		JiveSearchResult result = (JiveSearchResult) getSearchResult();
		return new Status(IStatus.OK, JiveUIPlugin.PLUGIN_ID, "Found " + result.getMatchCount() + " matches.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		if (result == null) {
			result = createSearchResult();
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public String getLabel() {
		return "JIVE Search";
	}
	
	/**
	 * Creates the search result to use for the query.  This method is called by
	 * {@link #getSearchResult()} when initializing the search result.
	 * <p>
	 * This method may be overridden by subclasses to provide different types of
	 * search results.
	 * <p>
	 * <em>NOTE:  In the future a more robust form of search results will be
	 * provided with different ways of viewing it in the Search view.</em>  
	 * 
	 * @return a newly created search result.
	 */
	protected IJiveSearchResult createSearchResult() {
		return new JiveSearchResult(this);
	}
}
