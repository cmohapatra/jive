package edu.buffalo.cse.jive.ui.search.queries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.events.ReturnEvent.Returner;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.ui.JiveUIPlugin;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.ui.IJiveUIConstants;
import edu.buffalo.cse.jive.ui.search.ContourIDTokenizer;
import edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery;
import edu.buffalo.cse.jive.ui.search.JiveSearchPattern;
import edu.buffalo.cse.jive.ui.search.RelationalOperator;

/**
 * An {@code IJiveSearchQuery} that is used to check method returns and
 * optionally conditions on return values.  The query is capable of checking for
 * method returns on a single instance or over all instances of a class (if an
 * instance number is not provided).
 * 
 * @author Jeffrey K Czyz
 */
public class MethodReturnedSearchQuery extends ExecutionHistorySearchQuery {
	
	/**
	 * An exporter used to examine {@code ReturnEvent}s.
	 */
	protected ReturnEventExporter exporter;
	
	/**
	 * A search pattern for the method return.
	 */
	protected JiveSearchPattern pattern;
	
	/**
	 * A relational operator for a condition on the method return value.
	 */
	protected RelationalOperator operator;
	
	/**
	 * A value to be used on the right side of the operator.
	 */
	protected String rightValue;
	
	/**
	 * Constructs a new search query with the supplied pattern and relational
	 * operator.  The variable name provided by the pattern is ignored.  An
	 * instance number is optional.
	 * 
	 * @param pattern the search pattern
	 * @param operator the relational operator
	 * @param rightValue the right operand
	 */
	public MethodReturnedSearchQuery(JiveSearchPattern pattern, RelationalOperator operator, String rightValue) {
		this.exporter = new ReturnEventExporter();
		this.pattern = pattern;
		this.operator = operator;
		this.rightValue = rightValue;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	@Override
	public Class<? extends Object> getResultType() {
		return ReturnEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + pattern.toString() +
		(operator == RelationalOperator.NONE ? "" : " " + operator + " " + rightValue) +
		"' - " + matchCount + (matchCount == 1 ? " return" : " returns");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_RETURN_EVENT_ICON_KEY);
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
		if (event.underlyingEvent() instanceof ReturnEvent) {
			ReturnEvent returnEvent = (ReturnEvent) event.underlyingEvent();
			if (checkForMatch(returnEvent)) {
				addMatch(event);
			}
		}
	}
	
	/**
	 * Checks if the supplied event matches the search pattern.
	 * 
	 * @param event the return event
	 * @return <code>true</code> if the event matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(ReturnEvent event) {
		event.export(exporter);
		return exporter.checkForMatch(pattern, operator, rightValue);
	}

	/**
	 * An exporter used to examine {@code ReturnEvent}s and to determine if the
	 * event is a return from a method represented by a {@code JiveSearchPattern}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class ReturnEventExporter implements ReturnEvent.Exporter {

		/**
		 * The method that returned.
		 */
		protected Returner returner;
		
		/**
		 * The return value used as the left operand of the condition.
		 */
		protected Value leftValue;
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addPreviousContext(edu.bsu.cs.jive.events.ReturnEvent.Returner)
		 */
		public void addPreviousContext(Returner returner) {
			this.returner = returner;
			
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.ReturnEvent.Exporter#addReturnValue(edu.bsu.cs.jive.contour.Value)
		 */
		public void addReturnValue(Value value) {
			leftValue = value;
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
		 * Returns whether the event is a method return represented by the
		 * supplied {@code JiveSearchPattern} and whether the return value
		 * condition holds.
		 * 
		 * @param pattern the pattern to check against
		 * @param operator the relational operator for the return value condition
		 * @param rightValue the right operand
		 * @return <code>true</code> if the method return can be associated with the pattern and the condition holds,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch(JiveSearchPattern pattern, RelationalOperator operator, String rightValue) {
			if (!(returner instanceof Returner.InModel)) {
				return false;
			}
			
			ContourID id = ((Returner.InModel) returner).contour();
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
				if (!pattern.getMethodName().equals(methodName)) {
					return false;
				}
			}
			
			if (leftValue == null) { // void
				return operator == RelationalOperator.NONE;
			}
			
			return operator.evaluate(leftValue, rightValue);
		}
	}
}
