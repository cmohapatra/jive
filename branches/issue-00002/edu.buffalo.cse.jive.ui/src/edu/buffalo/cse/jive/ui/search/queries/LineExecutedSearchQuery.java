package edu.buffalo.cse.jive.ui.search.queries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.events.EOSEvent;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery;

/**
 * An {@code IJiveSearchQuery} that is used to check whether a line is executed.
 * A relative path of a Java source file and a line number are required as
 * input.  The query returns a match for each time the line is executed.
 * 
 * @author Jeffrey K Czyz
 */
public class LineExecutedSearchQuery extends ExecutionHistorySearchQuery {

	/**
	 * A relative path to the source file of interest.
	 */
	protected String sourcePath;
	
	/**
	 * A line number within the source file.
	 */
	protected int lineNumber;
	
	/**
	 * Creates a new search query with the supplied source path and line number.
	 * 
	 * @param sourcePath the source path of the file
	 * @param lineNumber the line number of the file
	 */
	public LineExecutedSearchQuery(String sourcePath, int lineNumber) {
		this.sourcePath = sourcePath;
		this.lineNumber = lineNumber;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	public Class<? extends Object> getResultType() {
		return EOSEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + sourcePath + ", line " + lineNumber + "' - " + matchCount + (matchCount == 1 ? " execution" : " executions");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_EOS_EVENT_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof EOSEvent) {
			EOSEvent eosEvent = (EOSEvent) event.underlyingEvent();
			if (checkForMatch(eosEvent)) {
				addMatch(event);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.MessageSend)
	 */
	public void visit(MessageSend event) {
		// do nothing
	}
	
	/**
	 * Checks if the supplied event matches the query input.
	 * 
	 * @param event the end-of-statement event to check
	 * @return <code>true</code> if the source path and line number match,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(EOSEvent event) {
		return event.getFilename().equals(sourcePath) &&
			event.getLineNumber() == lineNumber;
	}

}
