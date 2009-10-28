package edu.buffalo.cse.jive.ui.search.queries;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
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
 * An {@code IJiveSearchQuery} that is used to check whether a class invariant
 * was violated.  The check occurs at the return of each method of the class.
 * The query is capable of checking for invariant violations on a single
 * instance or over all instances of a class (if an instance number is not
 * provided).
 * 
 * @author Jeffrey K Czyz
 */
public class InvariantViolatedSearchQuery extends ExecutionHistorySearchQuery {
	
	/**
	 * An exporter used to examine {@code AssignEvent}s.
	 */
	protected AssignEventExporter assignmentExporter;
	
	/**
	 * A search pattern for the invariant's left operand.
	 */
	protected JiveSearchPattern leftPattern;
	
	/**
	 * The invariant's relational operator.
	 */
	protected RelationalOperator operator;
	
	/**
	 * A search pattern for the invariant's right operand.
	 */
	protected JiveSearchPattern rightPattern;
	
	/**
	 * A mapping between an instance number and a value currently assigned
	 * with the variable of the left search pattern.  As the program's execution
	 * history is traversed by this visitor, the values in the map are updated.
	 */
	protected Map<String, Value> instanceToLeftValueMap = new HashMap<String, Value>();
	
	/**
	 * A mapping between an instance number and a value currently assigned
	 * with the variable of the right search pattern.  As the program's execution
	 * history is traversed by this visitor, the values in the map are updated.
	 */
	protected Map<String, Value> instanceToRightValueMap = new HashMap<String, Value>();
	
	/**
	 * Constructs a new search query with the supplied patterns and relational
	 * operator.  The method names provided by the patterns are ignored.  An
	 * instance number is optional.
	 * 
	 * @param leftPattern the left operand
	 * @param operator the relational operator
	 * @param rightPattern the right operand
	 */
	public InvariantViolatedSearchQuery(JiveSearchPattern leftPattern, RelationalOperator operator, JiveSearchPattern rightPattern) {
		this.assignmentExporter = new AssignEventExporter();
		this.leftPattern = leftPattern;
		this.operator = operator;
		this.rightPattern = rightPattern;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.ExecutionHistorySearchQuery#getResultType()
	 */
	@Override
	public Class<? extends Object> getResultType() {
		return InvariantViolatedSearchQuery.class;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getResultLabel(int)
	 */
	public String getResultLabel(int matchCount) {
		return "'" + leftPattern + " "+ operator + " " + rightPattern +
			"' - " + matchCount + (matchCount == 1 ? " violation" : " violations");
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.ui.search.IJiveSearchQuery#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageRegistry registry = JiveUIPlugin.getDefault().getImageRegistry();
		return registry.getDescriptor(IJiveUIConstants.ENABLED_INVARIANT_VIOLATED_ICON_KEY);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.EventOccurrence)
	 */
	public void visit(EventOccurrence event) {
		if (event.underlyingEvent() instanceof AssignEvent) {
			AssignEvent assignEvent = (AssignEvent) event.underlyingEvent();
			checkForUpdates(assignEvent);
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.jive.sequence.SequenceModel.EventVisitor#visit(edu.buffalo.cse.jive.sequence.MessageSend)
	 */
	public void visit(MessageSend event) {
		if (event.underlyingEvent() instanceof ReturnEvent) {
			if (checkInvariantViolated(event)) {
				addMatch(event);
			}
		}
	}
	
	/**
	 * Checks if the instance-to-value maps need updating.
	 * 
	 * @param event the event causing a change to a variable
	 */
	protected void checkForUpdates(AssignEvent event) {
		event.export(assignmentExporter);
		String instanceNumber = assignmentExporter.instanceNumber;
		Value value = assignmentExporter.value;
		if (assignmentExporter.matchesPattern(leftPattern)) {
			instanceToLeftValueMap.put(instanceNumber, value);
		}
		
		if (assignmentExporter.matchesPattern(rightPattern)) {
			instanceToRightValueMap.put(instanceNumber, value);
		}
	}
	
	/**
	 * Checks whether the invariant was violated at the current point in the
	 * execution history.  The supplied event wraps a {@code ReturnEvent}.
	 * 
	 * @param event the message send wrapping a return event
	 * @return <code>true</code> if the invariant is violated,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkInvariantViolated(MessageSend event) {
		ContourID id = event.containingExecution().context();
		ContourIDTokenizer parser = new ContourIDTokenizer(id);
		
		if (!leftPattern.getClassName().equals(parser.getClassName())) {
			return false;
		}
		
		String instanceNumber = parser.getInstanceNumber();
		if (!instanceToLeftValueMap.containsKey(instanceNumber)) {
			return false;
		}
		
		if (!instanceToRightValueMap.containsKey(instanceNumber)) {
			return false;
		}
		
		Value leftValue = instanceToLeftValueMap.get(instanceNumber);
		Value rightValue = instanceToRightValueMap.get(instanceNumber);
		return !operator.evaluate(leftValue, rightValue);
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
		public void addVariableID(VariableID variableID) {
			variableName = variableID.toString();
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
		 * by the supplied {@code JiveSearchPattern}.
		 * 
		 * @param pattern the pattern to check against
		 * @return <code>true</code> if the assignment can be associated with the pattern,
		 *         <code>false</code> otherwise
		 */
		public boolean matchesPattern(JiveSearchPattern pattern) {
			if (methodName != null) {
				return false;
			}
			
			if (!pattern.getClassName().equals(className)) {
				return false;
			}
			
			// TODO Handle static variables
			String patternInstanceNumber = pattern.getInstanceNumber();
			if (patternInstanceNumber != null) {
				if (!patternInstanceNumber.equals(instanceNumber)) {
					return false;
				}
			}
			
			return pattern.getVariableName().equals(variableName);
		}
	}
}
