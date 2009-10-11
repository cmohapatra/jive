package edu.buffalo.cse.jive.ui.search;

import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * An abstract implementation of an {@code IJiveSearchPage} used to update the
 * status of the Search button.  This class also implements the
 * {@code ModifyListener} interface, so input controls that need their content
 * checked for validity can simply add this class as a listener.
 * 
 * @author Jeffrey K Czyz
 */
public abstract class AbstractJiveSearchPage implements IJiveSearchPage, ModifyListener {

	/**
	 * The container of the page.
	 */
	private ISearchPageContainer container;
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQueryPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
	 */
	public void setContainer(ISearchPageContainer container) {
		this.container = container;
	}
	
	/**
	 * Returns the search page container in which this page is hosted.
	 * 
	 * @return the search page container
	 */
	protected ISearchPageContainer getContainer() {
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		updatePerformAction();
	}
	
	/**
	 * Updates the search button based on the validity of the input.
	 */
	protected void updatePerformAction() {
		getContainer().setPerformActionEnabled(isInputValid());
	}

}
