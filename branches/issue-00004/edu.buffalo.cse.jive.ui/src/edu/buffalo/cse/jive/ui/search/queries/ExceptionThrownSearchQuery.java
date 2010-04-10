package edu.buffalo.cse.jive.ui.search.queries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ThrowEvent;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower;
import edu.bsu.cs.jive.events.ThrowEvent.Thrower.InModel;
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
 * An {@code IJiveSearchQuery} that is used to check whether an exception was
 * thrown.  The query is capable of finding thrown exceptions of a particular
 * type or thrown by a particular class, instance, or in a certain method.
 * 
 * @author Jeffrey K Czyz
 */
public class ExceptionThrownSearchQuery extends ExecutionHistorySearchQuery {

	/**
	 * An exporter used to examine {@code ThrowEvent}s.
	 */
	protected ThrowEventExporter exporter;
	
	/**
	 * A search pattern for the thrower of the exception.
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
	 * @param pattern the pattern describing the thrower of the exception
	 * @param exceptionName the name of the exception
	 */
	public ExceptionThrownSearchQuery(JiveSearchPattern pattern, String exceptionName) {
		exporter = new ThrowEventExporter();
		this.pattern = pattern;
		this.exceptionName = exceptionName;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultType()
	 */
	public Class<? extends Object> getResultType() {
		return ThrowEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		if (exceptionName.equals("")) {
			return "'" + pattern + "'" + 
				" - " + matchCount + (matchCount == 1 ? " exception thrown" : " exceptions thrown");
		}
		else {
			return "'" + exceptionName + "' in '" + pattern + "' " +  
			"' - " + matchCount + (matchCount == 1 ? " exception thrown" : " exceptions thrown");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_THROW_EVENT_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof ThrowEvent) {
			ThrowEvent throwEvent = (ThrowEvent) event.underlyingEvent();
			if (checkForMatch(throwEvent)) {
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
	 * Checks if the supplied {@code ThrowEvent} matches the pattern and
	 * exception value.
	 * 
	 * @param event the throw event to check
	 * @return <code>true</code> if the exception matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(ThrowEvent event) {
		event.export(exporter);
		return exporter.checkForMatch();
	}
	
	/**
	 * An exporter used to examine {@code ThrowEvent}s and to determine if the
	 * event is for an exception thrown by a {@code JiveSearchPattern} and whose
	 * exception is of a given type.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class ThrowEventExporter implements ThrowEvent.Exporter {

		/**
		 * The method that threw the exception.
		 */
		protected Thrower thrower;
		
		/**
		 * The exception that was caught.
		 */
		protected Value exceptionValue;
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addThrower(edu.bsu.cs.jive.events.ThrowEvent.Thrower)
		 */
		public void addThrower(Thrower thrower) {
			this.thrower = thrower;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.ThrowEvent.Exporter#addException(edu.bsu.cs.jive.contour.Value)
		 */
		public void addException(Value exception) {
			exceptionValue = exception;
		}
		
		public void addFramePopped(boolean framePopped) {
			// do nothing
			// TODO Add functionality to allow users to specify whether a frame was popped
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
		 * {@code JiveSearchPattern} and the exception thrown starts with the
		 * exception name.
		 * 
		 * @return <code>true</code> if the assignment can be associated with the pattern,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch() {
			if (!(thrower instanceof InModel)) {
				return false;
			}
			
			ContourID id = ((Thrower.InModel) thrower).contour();
			ContourIDTokenizer tokenizer = new ContourIDTokenizer(id);
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
