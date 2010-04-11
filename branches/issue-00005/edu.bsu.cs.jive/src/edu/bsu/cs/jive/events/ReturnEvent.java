package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;

/**
 * Event that represents a method's return.
 * The returning method may be in the model or outside of the model.
 *
 * @author pvg
 */
public interface ReturnEvent extends Event {

	/**
	 * The context from which control is returning.
	 * @author pvg
	 */
	public interface Returner {
		
		/**
		 * A returner that is in the model (<i>i.e.</i> has a contour)
		 * @author pvg
		 */
		public interface InModel extends Returner {
			/**
			 * Get the contour that is the returner.
			 * That is, this is the contour from which control is returning.
			 * @return method contour
			 */
			public ContourID contour();
		}
		
		/**
		 * A returner that is not in the model.
		 * This interface is used for method calls that move outside of
		 * the realm of the contour model.
		 * @author pvg
		 */
		public interface OutOfModel extends Returner {
			/**
			 * Get a description of this out-of-model returner.
			 * @return short description, as would appear in a contour
			 */
			public String description();
		}
		
		/**
		 * Visitor for return event returners.
		 *
		 * @see Returner
		 * @author pvg
		 */
		public interface Visitor {
			public Object visit(InModel returner, Object arg);
			public Object visit(OutOfModel returner, Object arg);
		}
		
		/**
		 * Accept a visitor
		 * @param v visitor
		 * @param arg visitor argument
		 * @return visitation result
		 */
		public Object accept(Visitor v, Object arg);
	}
	
	/**
	 * Get the context from which execution has returned.
	 * @return a return context
	 */
	public Returner getPreviousContext();
	
	/**
	 * Get the value from this return event.
	 * If the method was a <tt>void</tt> method, then the result of 
	 * this call is meaningless.
	 * @return method return value
	 */
	public Value getReturnValue();
	
	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(ReturnEvent.Exporter exporter);
	
	/**
	 * Importer (builder) for return events.
	 * @author pvg
	 */
	public interface Importer extends Event.Importer {
		
		/**
		 * Provides the value returned from the method.
		 * 
		 * @return the return value
		 */
		public Value provideReturnValue();
		
		/**
		 * Provides the previous context.
		 * 
		 * @return the previous context.
		 */
		public Returner providePreviousContext();
	}
	
	/**
	 * Exporter (reverse-builder) for return events
	 * @author pvg
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the value returned from the method.  This is called by
		 * {@code ReturnEvent#export(Exporter)}.
		 * 
		 * @param value the return value
		 */
		public void addReturnValue(Value value);
		
		/**
		 * Adds the previous context.  This method is called by
		 * {@code ReturnEvent#export(Exporter)}.
		 * 
		 * @param returner the previous context
		 */
		public void addPreviousContext(Returner returner);
	}
	
}
