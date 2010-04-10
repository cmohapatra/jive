package edu.buffalo.cse.jive.ui.search;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;

/**
 * A search result for an {@code IJiveSearchQuery}.  Currently, this uses much
 * of the functionality provided by {@link AbstractTextSearchResult}.
 * <p>
 * <em>NOTE:  This dependency will be removed in the future.</em>
 * 
 * @author Jeffrey K Czyz
 */
public class JiveSearchResult extends AbstractTextSearchResult implements IJiveSearchResult {

	/**
	 * The query associated with the result.
	 */
	private IJiveSearchQuery query;
	
	/**
	 * Constructs a {@code JiveSearchResult} for the supplied query.
	 * 
	 * @param query the query associated with the result
	 */
	public JiveSearchResult(IJiveSearchQuery query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
	 */
	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
	 */
	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel() {
		return query.getResultLabel(getMatchCount());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return query.getImageDescriptor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return query;
	}
}
