package edu.buffalo.cse.jive.ui.search;

import java.util.HashMap;
import java.util.Map;

import edu.bsu.cs.jive.contour.Value;

/**
 * A representation of a relational operator for use by an
 * {@code IJiveSearchQuery}.  The operator can be used to compare a
 * {@link Value} object with either another {@code Value} or with a
 * {@code String}.
 * <p>
 * {@code Value#Encoded} objects may be compared using any of the available
 * operators.  All other {@code Value} objects can only be compared with
 * {@link #NONE}, {@link #EQUAL}, or {@link #NOT_EQUAL}.  
 * 
 * @see #evaluate(Value, Value)
 * @see #evaluate(Value, String)
 * @author Jeffrey K Czyz
 */
public enum RelationalOperator {
	
	/**
	 * A relational operator which always evaluates to <code>true</code>.
	 */
	NONE("") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			return true;
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			return true;
		}
	},
	
	/**
	 * A relational operator used to determine if two values are equal. 
	 */
	EQUAL("==") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			return evaluate(left, right.toString());
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			return left.toString().equals(right);
		}
	},
	
	/**
	 * A relational operator used to determine if two values are not equal. 
	 */
	NOT_EQUAL("!=") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			return !EQUAL.evaluate(left, right);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			return !EQUAL.evaluate(left, right);
		}
	},
	
	/**
	 * A relational operator used to determine if one value is less than another
	 * value. 
	 */
	LESS_THAN("<") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			if (left instanceof Value.Encoded && right instanceof Value.Encoded) {
				return compare(left.toString(), right.toString());
			}
			else {
				return false;
			}
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			if (left instanceof Value.Encoded) {
				return compare(left.toString(), right);
			}
			else {
				return false;
			}
		}
	},
	
	/**
	 * A relational operator used to determine if one value is less than or
	 * equal to another value. 
	 */
	LESS_THAN_OR_EQUAL("<=") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			return LESS_THAN.evaluate(left, right) || EQUAL.evaluate(left, right);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			return LESS_THAN.evaluate(left, right) || EQUAL.evaluate(left, right);
		}
	},
	
	/**
	 * A relational operator used to determine if one value is greater than
	 * another value. 
	 */
	GREATER_THAN(">") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			if (left instanceof Value.Encoded && right instanceof Value.Encoded) {
				return compare(left.toString(), right.toString());
			}
			else {
				return false;
			}
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			if (left instanceof Value.Encoded) {
				return compare(left.toString(), right);
			}
			else {
				return false;
			}
		}
	},
	
	/**
	 * A relational operator used to determine if one value is greater than or
	 * equal to another value. 
	 */
	GREATER_THAN_OR_EQUAL(">=") {
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, edu.bsu.cs.jive.contour.Value)
		 */
		public boolean evaluate(Value left, Value right) {
			return GREATER_THAN.evaluate(left, right) || EQUAL.evaluate(left, right);
		}
		
		/* (non-Javadoc)
		 * @see edu.buffalo.cse.jive.ui.search.RelationalOperator#evaluate(edu.bsu.cs.jive.contour.Value, java.lang.String)
		 */
		public boolean evaluate(Value left, String right) {
			return GREATER_THAN.evaluate(left, right) || EQUAL.evaluate(left, right);
		}
	};
	
	/**
	 * The single quote used to demarcate a character literal.
	 */
	private static final String SINGLE_QUOTE = "'";
	
	/**
	 * The single quote used to demarcate a string literal.
	 */
	private static final String DOUBLE_QUOTE = "\"";
	
	/**
	 * A mapping between operator representations and the actual operator.
	 */
	private static Map<String, RelationalOperator> stringToEnumMap = new HashMap<String, RelationalOperator>();
	
	static {
		stringToEnumMap.put(NONE.toString(), NONE);
		stringToEnumMap.put(EQUAL.toString(), EQUAL);
		stringToEnumMap.put(NOT_EQUAL.toString(), NOT_EQUAL);
		stringToEnumMap.put(LESS_THAN.toString(), LESS_THAN);
		stringToEnumMap.put(LESS_THAN_OR_EQUAL.toString(), LESS_THAN_OR_EQUAL);
		stringToEnumMap.put(GREATER_THAN.toString(), GREATER_THAN);
		stringToEnumMap.put(GREATER_THAN_OR_EQUAL.toString(), GREATER_THAN_OR_EQUAL);
	}
	
	/**
	 * The string representation of the operator.
	 */
	private String operator;
	
	/**
	 * Constructs a new operator with the supplied representation.
	 * 
	 * @param operator the operator representation
	 */
	private RelationalOperator(String operator) {
		this.operator = operator;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return operator;
	}
	
	/**
	 * Evaluates the operator on the supplied values.
	 * 
	 * @param left the left operand
	 * @param right the right operand
	 * @return the evaluation result
	 */
	public abstract boolean evaluate(Value left, Value right);
	
	/**
	 * Evaluates the operator on the supplied values.
	 * 
	 * @param left the left operand
	 * @param right the right operand
	 * @return the evaluation result
	 */
	public abstract boolean evaluate(Value left, String right);
	
	/**
	 * Compares the supplied strings by first converting them to the appropriate
	 * type.
	 * 
	 * @param left the left operand
	 * @param right the right operand
	 * @return the comparison result
	 */
	protected boolean compare(String left, String right) {
		if (left.startsWith(DOUBLE_QUOTE) && right.startsWith(DOUBLE_QUOTE)) {
			return compare(left, right);
		}
		else if (left.startsWith(SINGLE_QUOTE) && right.startsWith(SINGLE_QUOTE)) {
			return compare(left.charAt(1), right.charAt(1));
		}
		else {
			try {
				return compare(Integer.parseInt(left), Integer.parseInt(right));
			}
			catch (NumberFormatException e) {}
			
			try {
				return compare(Long.parseLong(left), Long.parseLong(right));
			}
			catch (NumberFormatException e) {}
			
			try {
				return compare(Float.parseFloat(left), Float.parseFloat(right));
			}
			catch (NumberFormatException e) {}
			
			try {
				return compare(Double.parseDouble(left), Double.parseDouble(right));
			}
			catch (NumberFormatException e) {}
			
			return compare(Boolean.parseBoolean(left), Boolean.parseBoolean(right));
		}
	}
	
	/**
	 * Compares the two operands using the {@link Comparable} interface.
	 * 
	 * @param <T> the type of the operands
	 * @param left the left operand
	 * @param right the right operand
	 * @return the comparison result
	 */
	protected <T> boolean compare(Comparable<T> left, T right) {
		switch (this) {
		case LESS_THAN:
			return left.compareTo(right) < 0;
		case GREATER_THAN:
			return left.compareTo(right) > 0;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Returns the {@link RelationalOperator} represented by the supplied
	 * string.
	 * 
	 * @param operator the string representation
	 * @return the operator represented by the string
	 */
	public static RelationalOperator fromString(String operator) {
		if (stringToEnumMap.containsKey(operator)) {
			return stringToEnumMap.get(operator);
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}
