package edu.bsu.cs.jive.events;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;

/**
 * An event corresponding to an exception being thrown.  This event is generated
 * at the initial throw (regardless of whether the exception is caught there)
 * and also for any methods that do not handle the exception.
 * 
 * @author Jeffrey K Czyz
 */
public interface ThrowEvent extends Event {

	/**
	 * The thrower of an exception.
	 */
	public interface Thrower {
		
		/**
		 * A thrower that is in the model (<em>i.e.</em>, has a contour).
		 */
		public interface InModel extends Thrower {
			
			/**
			 * Returns the contour ID of the method activation that threw the
			 * exception.
			 * 
			 * @return the thrower's contour ID
			 */
			public ContourID contour();
		}
		
		/**
		 * A thrower of a class not in the model.
		 */
		public interface OutOfModel extends Thrower {
			
			/**
			 * Returns a description of the thrower.
			 * 
			 * @return the thrower's description
			 */
			public String description();
		}
		
		/**
		 * A visitor for thrower classes.
		 */
		public interface Visitor {
			
			/**
			 * Visits an in-model thrower.
			 * 
			 * @param thrower the thrower
			 * @param arg an argument
			 * @return the result
			 */
			public Object visit(InModel thrower, Object arg);
			
			/**
			 * Visits an out-of-model thrower.
			 * 
			 * @param thrower the thrower
			 * @param arg an argument
			 * @return the result
			 */
			public Object visit(OutOfModel thrower, Object arg);
		}
		
		/**
		 * Accepts a visitor.
		 * 
		 * @param v a visitor
		 * @param arg an argument
		 * @return the visitation result
		 */
		public Object accept(Visitor v, Object arg);
	}
	
	/**
	 * Returns the method activation that threw the exception.
	 * 
	 * @return the thrower
	 */
	public Thrower getThrower();
	
	/**
	 * Returns the exception that was thrown.
	 * 
	 * @return the thrown exception
	 */
	public Value getException();
	
	/**
	 * Returns whether the thrown exception resulted in a popped stack frame
	 * (<em>i.e.</em>, whether it went unhandled here).
	 * 
	 * @return <code>true</code> if the exception went unhandled, otherwise
	 *         <code>false</code>
	 */
	public boolean wasFramePopped();
	
	/**
	 * Exports the event using the provided exporter.
	 * 
	 * @param exporter the exporter
	 */
	public void export(ThrowEvent.Exporter exporter);
	
	/**
	 * Importer (builder) for throw events.
	 */
	public interface Importer extends Event.Importer {
		
		/**
		 * Provides the method activation that threw the exception.
		 * 
		 * @return the thrower
		 */
		public Thrower provideThrower();
		
		/**
		 * Provides the exception that was thrown.
		 * 
		 * @return the thrown exception
		 */
		public Value provideException();
		
		/**
		 * Provides whether the thrown exception resulted in a popped stack frame.
		 * 
		 * @return <code>true</code> if the exception went unhandled, otherwise
  	 *         <code>false</code>
		 */
		public boolean provideFramePopped();
	}
	
	/**
	 * Exporter (reverse-builder) for throw events.
	 */
	public interface Exporter extends Event.Exporter {
		
		/**
		 * Adds the method activation that threw the exception.
		 * 
		 * @param thrower the thrower
		 */
		public void addThrower(Thrower thrower);
		
		/**
		 * Adds the exception that was thrown.
		 * 
		 * @param exception the thrown exception
		 */
		public void addException(Value exception);
		
		/**
		 * Adds whether the thrown exception resulted in a popped stack frame.
		 * 
		 * @param framePopped <code>true</code> if the exception went unhandled,
		 *                    otherwise <code>false</code>
		 */
		public void addFramePopped(boolean framePopped);
	}
}
