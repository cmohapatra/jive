package edu.buffalo.cse.jive.ui.search.queries;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
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
 * An {@code IJiveSearchQuery} that is used to check whether a method was
 * called.  The query is capable of checking for method calls on a single
 * instance or over all instances of a class (if an instance number is not
 * provided).
 * 
 * @author Jeffrey K Czyz
 */
public class MethodCalledSearchQuery extends ExecutionHistorySearchQuery {

	/**
	 * An exporter used to examine {@code CallEvent}s.
	 */
	protected CallEventExporter exporter;
	
	/**
	 * A search pattern for the method call.
	 */
	protected JiveSearchPattern pattern;
	
	/**
	 * Constructs a new search query with the supplied pattern.  The variable
	 * name provided by the pattern is ignored.  An instance number is optional.
	 * 
	 * @param pattern the pattern to match
	 */
	public MethodCalledSearchQuery(JiveSearchPattern pattern) {
		this.exporter = new CallEventExporter();
		this.pattern = pattern;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	@Override
	public Class<? extends Object> getResultType() {
		return CallEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + pattern.toString() +
		"' - " + matchCount + (matchCount == 1 ? " call" : " calls");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_CALL_EVENT_ICON_KEY);
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.MessageSend)
	 */
	public void visit(MessageSend event) {
		if (event.underlyingEvent() instanceof CallEvent) {
			CallEvent callEvent = (CallEvent) event.underlyingEvent();
			if (checkForMatch(callEvent)) {
				addMatch(event);
			}
		}
	}
	
	/**
	 * Checks if the supplied event matches the search pattern.
	 * 
	 * @param event the call event
	 * @return <code>true</code> if the event matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(CallEvent event) {
		event.export(exporter);
		return exporter.checkForMatch(pattern);
	}
	
	/**
	 * An exporter used to examine {@code CallEvent}s and to determine if the
	 * event is a call on a method represented by a {@code JiveSearchPattern}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class CallEventExporter implements CallEvent.Exporter {

		/**
		 * The method that was called.
		 */
		protected Target target;
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addActualParams(java.util.List)
		 */
		public void addActualParams(List<Value> actuals) {}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addCaller(edu.bsu.cs.jive.events.CallEvent.Caller)
		 */
		public void addCaller(Caller caller) {}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.CallEvent.Exporter#addTarget(edu.bsu.cs.jive.events.CallEvent.Target)
		 */
		public void addTarget(Target target) {
			this.target = target;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addNumber(long)
		 */
		public void addNumber(long n) {}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.Event.Exporter#addThreadID(edu.bsu.cs.jive.util.ThreadID)
		 */
		public void addThreadID(ThreadID thread) {}
		
		/**
		 * Returns whether the event is a method call represented by the
		 * supplied {@code JiveSearchPattern}.
		 * 
		 * @param pattern the pattern to check against
		 * @return <code>true</code> if the method call can be associated with the pattern,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch(JiveSearchPattern pattern) {
			if (!(target instanceof Target.InModel)) {
				return false;
			}
			
			ContourID id = ((Target.InModel) target).contour();
			ContourIDTokenizer parser = new ContourIDTokenizer(id);
			
			String typeName = parser.getClassName();
			String instanceNumber = parser.getInstanceNumber();
			String methodName = parser.getMethodName();
			
			if (pattern.getClassName() != null) {
				if (!pattern.getClassName().equals(typeName)) {
					return false;
				}
			}
			else {
				return false;
			}
			
			if (pattern.getInstanceNumber() != null) {
				if (!pattern.getInstanceNumber().equals(instanceNumber)) {
					return false;
				}
			}
			
			if (pattern.getMethodName() != null) {
				return pattern.getMethodName().equals(methodName);
			}
			else {
				return false;
			}
		}
	}
}
