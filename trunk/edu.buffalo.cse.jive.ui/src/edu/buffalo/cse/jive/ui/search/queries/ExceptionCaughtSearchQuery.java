package edu.buffalo.cse.jive.ui.search.queries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CatchEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.ContourIDTokenizer;
import edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchPattern;

/**
 * An {@code IJiveSearchQuery} that is used to check whether an exception was
 * caught.  The query is capable of finding caught exceptions of a particular
 * type or caught by a particular class, instance, or in a certain method.
 * 
 * @author Jeffrey K Czyz
 */
public class ExceptionCaughtSearchQuery extends ExecutionHistorySearchQuery {

	/**
	 * An exporter used to examine {@code CatchEvent}s.
	 */
	protected CatchEventExporter exporter;
	
	/**
	 * A search pattern for the catcher of the exception.
	 */
	protected JiveSearchPattern pattern;
	
	/**
	 * The exception name for which to search.
	 */
	protected String exceptionName;
	
	/**
	 * Constructs a new search query with the supplied pattern and exception
	 * name.
	 * 
	 * @param pattern the pattern describing the catcher of the exception
	 * @param exceptionName the name of the exception
	 */
	public ExceptionCaughtSearchQuery(JiveSearchPattern pattern, String exceptionName) {
		exporter = new CatchEventExporter();
		this.pattern = pattern;
		this.exceptionName = exceptionName;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultType()
	 */
	public Class<? extends Object> getResultType() {
		return CatchEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		if (exceptionName.equals("")) {
			return "'" + pattern + "'" + 
				" - " + matchCount + (matchCount == 1 ? " exception caught" : " exceptions caught");
		}
		else {
			return "'" + exceptionName + "' in '" + pattern + "' " +  
			"' - " + matchCount + (matchCount == 1 ? " exception caught" : " exceptions caught");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_CATCH_EVENT_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof CatchEvent) {
			CatchEvent catchEvent = (CatchEvent) event.underlyingEvent();
			if (checkForMatch(catchEvent)) {
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
	 * Checks if the supplied {@code CatchEvent} matches the pattern and
	 * exception value.
	 * 
	 * @param event the catch event to check
	 * @return <code>true</code> if the exception matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(CatchEvent event) {
		event.export(exporter);
		return exporter.checkForMatch();
	}
	
	/**
	 * An exporter used to examine {@code CatchEvent}s and to determine if the
	 * event is for an exception caught by a {@code JiveSearchPattern} and whose
	 * exception is of a given type.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class CatchEventExporter implements CatchEvent.Exporter {

		/**
		 * The method that caught the exception.
		 */
		protected ContourID catcher;
		
		/**
		 * The exception that was caught.
		 */
		protected Value exceptionValue;
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addCatcher(edu.bsu.cs.jive.util.ContourID)
		 */
		public void addCatcher(ContourID catcher) {
			this.catcher = catcher;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
		 */
		public void addException(Value exception) {
			exceptionValue = exception;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CatchEvent.Exporter#addVariable(edu.bsu.cs.jive.util.VariableID)
		 */
		public void addVariable(VariableID v) {
			// do nothing
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
		 */
		public void addNumber(long n) {
			// do nothing
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
		 */
		public void addThreadID(ThreadID thread) {
			// do nothing
		}
		
		/**
		 * Returns whether the exception catcher matches the
		 * {@code JiveSearchPattern} and the exception caught starts with the
		 * exception name.
		 * 
		 * @return <code>true</code> if the assignment can be associated with the pattern,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch() {
			ContourIDTokenizer tokenizer = new ContourIDTokenizer(catcher);
			String className = tokenizer.getClassName();
			String instanceNumber = tokenizer.getInstanceNumber();
			String methodName = tokenizer.getMethodName();
			
			if (pattern.getClassName() != null) {
				if (!pattern.getClassName().equals(className)) {
					return false;
				}
			}
			
			if (pattern.getInstanceNumber() != null) {
				if (!pattern.getInstanceNumber().equals(instanceNumber)) {
					return false;
				}
			}
			
			if (pattern.getMethodName() != null) {
				if (!pattern.getMethodName().equals(methodName)) {
					return false;
				}
			}
			
			if (exceptionName.equals("")) {
				return true;
			}
			else {
				return exceptionValue.toString().startsWith(exceptionName);
			}
		}
	}
}
