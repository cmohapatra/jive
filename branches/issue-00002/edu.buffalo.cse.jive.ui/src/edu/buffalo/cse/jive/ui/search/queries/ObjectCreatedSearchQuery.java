package edu.buffalo.cse.jive.ui.search.queries;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.contour.ContourFormat;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.ContourIDTokenizer;
import edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchPattern;

/**
 * An {@code IJiveSearchQuery} that is used to search for object creations. 
 * The query is capable of searching for a single instance creation or
 * for when all instances of a class created (if an instance number is not
 * provided).
 * 
 * @author Jeffrey K Czyz
 */
public class ObjectCreatedSearchQuery extends ExecutionHistorySearchQuery {
	
	/**
	 * An exporter used to examine {@code CallEvent}s.
	 */
	protected NewEventExporter exporter;
	
	/**
	 * A search pattern for the object creation.
	 */
	protected JiveSearchPattern pattern;
	
	/**
	 * Constructs a new search query with the supplied pattern.  The method and
	 * variable names provided by the pattern are ignored.  An instance number
	 * is optional.
	 * 
	 * @param pattern the search pattern
	 */
	public ObjectCreatedSearchQuery(JiveSearchPattern pattern) {
		this.exporter = new NewEventExporter();
		this.pattern = pattern;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	@Override
	public Class<? extends Object> getResultType() {
		return NewEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + pattern.toString() +
		"' - " + matchCount + (matchCount == 1 ? " creation" : " creations");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_NEW_EVENT_ICON_KEY);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof NewEvent) {
			NewEvent newEvent = (NewEvent) event.underlyingEvent();
			if (checkForMatch(newEvent)) {
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
	 * Checks if the supplied event matches the search pattern.
	 * 
	 * @param event the new event
	 * @return <code>true</code> if the event matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(NewEvent event) {
		event.export(exporter);
		return exporter.checkForMatch(pattern);
	}
	
	/**
	 * An exporter used to examine {@code NewEvent}s and to determine if the
	 * event is a object creation represented by a {@code JiveSearchPattern}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class NewEventExporter implements NewEvent.Exporter {
		
		/**
		 * A flag used to determine if a match was found by the
		 * {@link #recordExporter}.
		 */
		protected boolean matchFound = false;
		
		/**
		 * An exporter used to examine {@code ContourCreationRecord}s and to
		 * determine if any match the search pattern.
		 */
		protected ContourCreationRecord.Exporter recordExporter = new ContourCreationRecord.Exporter() {

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourFormat(edu.bsu.cs.jive.contour.ContourFormat)
			 */
			public void addContourFormat(ContourFormat cf) {}

			/* (non-Javadoc)
			 * @see edu.bsu.cs.jive.contour.ContourCreationRecord.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
			 */
			public void addContourID(ContourID id) {
				ContourIDTokenizer parser = new ContourIDTokenizer(id);
				
				if (pattern.getClassName().equals(parser.getClassName())) {
					
					String instanceNumber = pattern.getInstanceNumber();
					if (instanceNumber == null) {
						matchFound = true;
					}
					else {
						matchFound = instanceNumber.equals(parser.getInstanceNumber());
					}
				}
				else {
					matchFound = false;
				}
			}
			
		};

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addContourCreationRecords(java.util.List)
		 */
		public void addContourCreationRecords(List<ContourCreationRecord> creationRecords) {
			matchFound = false;
			for (ContourCreationRecord record : creationRecords) {
				record.export(recordExporter);
				
				if (matchFound) {
					break;
				}
			}
			
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.NewEvent.Exporter#addEnclosingContourID(edu.bsu.cs.jive.util.ContourID)
		 */
		public void addEnclosingContourID(ContourID enclosingID) {}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
		 */
		public void addNumber(long n) {}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
		 */
		public void addThreadID(ThreadID thread) {}
		
		/**
		 * Returns whether the event is a object creation represented by the
		 * supplied {@code JiveSearchPattern}.
		 * 
		 * @param pattern the pattern to check against
		 * @return <code>true</code> if the object creation can be associated with the pattern,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch(JiveSearchPattern pattern) {
			return matchFound;
		}
	}
}
