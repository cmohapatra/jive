/*
 * Created on Jul 2, 2003
 */
package edu.bsu.cs.jive.runtime;

import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;

/**
 * Defines an object that is capable of applying filters to JPDA event 
 * requests. 
 * Specific implementors may use inclusion or exclusion filters at 
 * any level; this interface only defines an object that can apply filters
 * to the types of requests corresponding to the defined methods.
 * 
 * @author Paul Gestwicki (pvg@cse.buffalo.edu)
 */
public interface JDIRequestFilter {
	
	// TODO Plug-in change
  /**
   * Apply this object's filter to the given event request.
   * @param request the event request object
   */
  public void filter(ClassPrepareRequest request);
  
  /**
   * Apply this object's filter to the given event request.
   * @param request the event request object
   */
  public void filter(MethodEntryRequest request);
  
  /**
   * Apply this object's filter to the given event request.
   * @param request the event request object
   */
  public void filter(MethodExitRequest request);

  /**
   * Apply this object's filter to the given event request.
   * @param request the event request object
   */
  public void filter(StepRequest request);
  
  /**
   * Check if this filter would accept the given class name
   * @param clazz
   * @return true if clazz is accepted
   */
  public boolean acceptsClass(String clazz);

}
