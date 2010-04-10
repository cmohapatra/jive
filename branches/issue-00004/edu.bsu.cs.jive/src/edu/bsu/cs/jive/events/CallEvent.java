package edu.bsu.cs.jive.events;

import java.util.List;

import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.ContourID;

/**
 * An event corresponding to a method call.
 * 
 * @author pvg
 */
public interface CallEvent extends Event {

  /**
   * The caller of a call event.
   * 
   * @author pvg
   */
  public interface Caller {

    /**
     * A caller that is in the model (<em>i.e.</em>, has a contour)
     */
    public interface InModel extends Caller {
    	
      /**
       * Get the contour for the caller.
       * 
       * @return method contour that is the caller
       */
      public ContourID contour();
    }

    /**
     * A caller that is not in the model.
     */
    public interface OutOfModel extends Caller {
    	
      /**
       * Get a brief description of this out-of-model caller.
       * 
       * @return description of the caller
       */
      public String description();
    }

    /**
     * The system caller, used for the first call on a thread.
     */
    public interface System extends Caller {
    }

    /**
     * A visitor for caller classes.
     * 
     * @author pvg
     */
    public interface Visitor {
    	
      /**
       * Visits an in-model caller.
       * 
       * @param caller the caller
       * @param obj an argument
       * @return the result
       */
      public Object visit(Caller.InModel caller, Object obj);

      /**
       * Visits an out-of-model caller.
       * 
       * @param caller the caller
       * @param obj an argument
       * @return the result
       */
      public Object visit(Caller.OutOfModel caller, Object obj);

      /**
       * Visits a system caller.
       * 
       * @param caller the caller
       * @param obj an argument
       * @return the result
       */
      public Object visit(Caller.System caller, Object obj);
    }

    /**
     * Accept a visitor.
     * 
     * @param v the visitor
     * @param arg an argument
     * @return visitation result
     */
    public Object accept(Visitor v, Object arg);

  }

  /**
   * The target of a call event.
   * The target is the context in which the call takes place,
   * so in a contour model, it is the contour that will have the method
   * contour added into it.
   * 
   * @author pvg
   */
  public interface Target {

    /**
     * A target that is in the model (<i>i.e.</i> has a contour)
     */
    public interface InModel extends Target {
    	
    	// TODO Plug-in change.
    	/**
    	 * Get the contour ID of this target.
    	 * 
    	 * @return contour's id
    	 */
    	public ContourID contour();
            
      /**
       * Get the contour ID of the contour (object or static) enclosing
       * this contour.
       * @return enclosing contour's id
       */
      public ContourID enclosing();

      /**
       * Export this in-model call target to the given reverse-builder.
       * @param e exporter
       */
      public void export(InModel.Exporter e);
      
      /**
       * A builder for an in-model target.
       * @author pvg
       */
      public interface Importer {
        /**
         * Get a contour creation record that describes the method contour. If the
         * method does not have a contour (i.e. it is out of the model), then this
         * will return null.
         * 
         * @return a contour creation record or null
         */
        public MethodContourCreationRecord provideContourCreationRecord();
      }

      /**
       * A reverse-builder for an in-model target.
       * @author pvg
       */
      public interface Exporter {
        /**
         * Add the contour creation record, which describes the method contour
         * that is created. This may provide null if the contour is not in the
         * model.
         * 
         * @param ccr contour creation record
         */
        public void addContourCreationRecord(MethodContourCreationRecord ccr);
      }
    }

    /**
     * A target that is not in the model.
     */
    public interface OutOfModel extends Target {
      /**
       * Get a brief description of this out-of-model target.
       * 
       * @return description of the target
       */
      public String description();
    }

    /**
     * A visitor for target classes.
     * 
     * @author pvg
     */
    public interface Visitor {
    	
    	/**
       * Visits an in-model target.
       * 
       * @param target the target
       * @param obj an argument
       * @return the result
       */
      public Object visit(InModel target, Object obj);

      /**
       * Visits an out-of-model target.
       * 
       * @param target the target
       * @param obj an argument
       * @return the result
       */
      public Object visit(OutOfModel target, Object obj);
    }

    /**
     * Accept a visitor
     * 
     * @param v
     * @param arg
     * @return visitation result
     */
    public Object accept(Visitor v, Object arg);
  }

  /**
   * Get the actual parameters of this call.
   * 
   * @return list of actual parameters
   */
  // See if we can get away with only having this in the exporter for now
  // public List<Value> actualParams();
  /**
   * Get the caller of this call event. The caller is the method from which the
   * call eminates.
   * 
   * @return caller
   * @see Caller.InModel
   * @see Caller.OutOfModel
   */
  // public Caller caller();
  /**
   * Get the target of this call event. The target is the entity that represents
   * the call, such as a contour.
   * 
   * @return target
   * @see Target.InModel
   * @see Target.OutOfModel
   */
  // public Target target();
  
  /**
   * Exports the event.
   * @param exporter the reverse-builder
   */
  public void export(Exporter exporter);

  /**
   * Importer (builder) for call events.
   * 
   * @author pvg
   */
  public interface Importer extends Event.Importer {
  	
    /**
     * Provides the caller of the call.
     * 
     * @return the caller
     */
    public Caller provideCaller();

    /**
     * Provides the target of the call.
     * 
     * @return the target
     */
    public Target provideTarget();

    /**
     * Provides the actual parameter values of the call.
     * 
     * @return a list of actual parameter values
     */
    public List<Value> provideActualParams();
  }

  /**
   * Exporter (reverse-builder) for call events.
   * 
   * @author pvg
   */
  public interface Exporter extends Event.Exporter {
  	
    /**
     * Adds the caller of the call.  This method is called by
     * {@code CallEvent#export(Exporter)}.
     * 
     * @param caller the caller
     */
    public void addCaller(Caller caller);

    /**
     * Adds the target of the call.  This method is called by
     * {@code CallEvent#export(Exporter)}.
     * 
     * @param target the caller
     */
    public void addTarget(Target target);

    /**
     * Adds the actual parameters of the call.  This method is called by
     * {@code CallEvent#export(Exporter)}.
     * 
     * @param actuals the actual parameter values
     */
    public void addActualParams(List<Value> actuals);
  }
}
