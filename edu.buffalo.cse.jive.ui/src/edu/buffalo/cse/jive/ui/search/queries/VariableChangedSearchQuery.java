package edu.buffalo.cse.jive.ui.search.queries;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
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
import edu.buffalo.cse.jive.ui.search.RelationalOperator;

/**
 * An {@code IJiveSearchQuery} that is used to check where a variable has
 * changed and also when a condition on the new value holds.  The query is
 * capable of checking for variable changes on a single instance or over all
 * instances of a class (if an instance number is not provided).
 * 
 * @author Jeffrey K Czyz
 */
public class VariableChangedSearchQuery extends ExecutionHistorySearchQuery {
	
	/**
	 * An exporter used to examine {@code AssignEvent}s.
	 */
	protected AssignEventExporter exporter;
	
	/**
	 * A search pattern for the variable change.
	 */
	protected JiveSearchPattern pattern;
	
	/**
	 * A relational operator for a condition on the variable value.
	 */
	protected RelationalOperator operator;
	
	/**
	 * A value to be used on the right side of the operator.
	 */
	protected String rightValue;
	
	/**
	 * Constructs a new search query with the supplied pattern, relational
	 * operator, and optional value.  An instance number provided by the pattern
	 * is optional.
	 * 
	 * @param pattern the search pattern
	 * @param operator the relational operator
	 * @param value the right operand
	 */
	public VariableChangedSearchQuery(JiveSearchPattern pattern, RelationalOperator operator, String value) {
		this.exporter = new AssignEventExporter();
		this.pattern = pattern;
		this.operator = operator;
		this.rightValue = value;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	@Override
	public Class<? extends Object> getResultType() {
		return AssignEvent.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + pattern.toString() +
			(operator == RelationalOperator.NONE ? "" : " " + operator + " " + rightValue) +
			"' - " + matchCount + (matchCount == 1 ? " assignment" : " assignments");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_ASSIGN_EVENT_ICON_KEY);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof AssignEvent) {
			AssignEvent assignEvent = (AssignEvent) event.underlyingEvent();
			if (checkForMatch(assignEvent)) {
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
	 * @param event the assign event
	 * @return <code>true</code> if the event matches the pattern,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkForMatch(AssignEvent event) {
		event.export(exporter);
		return exporter.checkForMatch(pattern, operator, rightValue);
	}
	
	/**
	 * An exporter used to examine {@code AssignEvent}s and to determine if the
	 * event is an assignment to a variable represented by a
	 * {@code JiveSearchPattern}.
	 * 
	 * @author Jeffrey K Czyz
	 */
	protected class AssignEventExporter implements AssignEvent.Exporter {
		
		/**
		 * The fully-qualified class name containing the variable assignment.
		 */
		protected String className;
		
		/**
		 * The instance number of the class containing the variable assignment.
		 */
		protected String instanceNumber;
		
		/**
		 * The method name where the variable assignment is occurring if the
		 * variable is a parameter or local variable.
		 */
		protected String methodName;
		
		/**
		 * The variable which is being assigned a value.
		 */
		protected String variableName;
		
		/**
		 * The value assigned to the variable.
		 */
		protected Value value;
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addContourID(edu.bsu.cs.jive.util.ContourID)
		 */
		public void addContourID(ContourID id) {
			ContourIDTokenizer parser = new ContourIDTokenizer(id);
			className = parser.getClassName();
			instanceNumber = parser.getInstanceNumber();
			methodName = parser.getMethodName();
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addNewValue(edu.bsu.cs.jive.contour.Value)
		 */
		public void addNewValue(Value v) {
			value = v;
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.events.AssignEvent.Exporter#addVariableID(edu.bsu.cs.jive.util.VariableID)
		 */
		public void addVariableID(VariableID id) {
			variableName = id.toString();
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
		 * Returns whether the event is an assignment to a variable represented
		 * by the supplied {@code JiveSearchPattern} and the condition on the
		 * value holds.
		 * 
		 * @param pattern the pattern to check against
		 * @param operator the relational operator
		 * @param rightValue the right operand
		 * @return <code>true</code> if the assignment can be associated with the pattern and the condition holds,
		 *         <code>false</code> otherwise
		 */
		public boolean checkForMatch(JiveSearchPattern pattern, RelationalOperator operator, String rightValue) {
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
			
			if (pattern.getVariableName().equals(variableName)) {
				return operator.evaluate(value, rightValue);
			}
			else {
				return false;
			}
		}
	}
}