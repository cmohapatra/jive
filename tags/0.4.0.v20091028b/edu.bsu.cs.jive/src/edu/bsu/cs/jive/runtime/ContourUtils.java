package edu.bsu.cs.jive.runtime;

import java.util.List;

import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.MethodEntryEvent;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;


/**
 * Provides a facade to the contour management that occurs while
 * processing JDI events.
 * <p>
 * This interface is used heavily by 
 * {@link edu.bsu.cs.jive.runtime.JDIToJiveLogEventAdapter}.
 * 
 * @author pvg
 */
public interface ContourUtils {

  /**
   * Get the next available event number.
   * Subsequent calls will return increasing event numbers.
   * @return next available event number
   */
  public long nextEventNumber();
  
  /**
   * Check if a static contour has been created for a specific class.
   * @param clazz the fully-qualified name of a class
   * @return true if there is a static contour for clazz already 
   */
  public boolean staticContourExistsFor(String clazz);
  
  /**
   * Check if an object contour has been created for a specified object.
   * 
   * @param object the query object
   * @return true if there is an object contour for that object ID
   * @see #createInstanceContourIDs(ObjectReference)
   * @see #getInstanceContourID(ObjectReference)
   * @see #getInstanceContourID(ObjectReference, String)
   */
  public boolean instanceContourExistsFor(ObjectReference object);
  // TODO: do we need another variation on this method that takes a class name as well?
  
  /**
   * Get the thread identifier for a thread reference.
   * @param ref a thread reference from JPDA
   * @return contour model thread identifier
   */
  public ThreadID threadID(ThreadReference ref);
  
  /**
   * Get the contour ID for a static contour.
   * This will throw an exception if the static contour is not
   * in the model.
   * @param clazz the name of the class whose static contour id is sought
   * @return static contour id
   * @throws IllegalArgumentException if the contour is not in the model
   * @see #staticContourExistsFor(String)
   */
  public ContourID getStaticContourID(String clazz);
  
  /**
   * Get the contour ID for an instance contour.
   * This will return the most specific isntance contour for the given object.
   * <p>
   * This method will throw an exception if the instance contour
   * is not yet in the model.
   * @param object the object's unique identifier
   * @return contour ID
   * @throws IllegalArgumentException if the contour is not in the model
   * @see #instanceContourExistsFor(ObjectReference)
   * @see #getInstanceContourID(ObjectReference, String)
   * @see #createInstanceContourIDs(ObjectReference)
   */
  public ContourID getInstanceContourID(ObjectReference object);
  
  /**
   * Get the instance contour ID for an object reference.
   * The contour ID returned is the ID for the specific class in the
   * object's contour stack.
   * <p>
   * This method will throw an exception if the contour ID is not yet
   * in the model.
   * 
   * @param object object whose instance contour is being sought
   * @param clazz the specific class of the contour.
   *  This must be either object's class or one of its superclasses.
   *  
   * @return contour ID for the matching object and class.
   * @see #getInstanceContourID(ObjectReference)
   * @see #instanceContourExistsFor(ObjectReference)
   * @see #createInstanceContourIDs(ObjectReference)
   */
  public ContourID getInstanceContourID(ObjectReference object, String clazz);
  
  /**
   * Create a static contour ID for a class.
   * If this is called more than once on the same input, an exception
   * will be thrown.
   * @param name class name
   * @return static contour id
   * @see #getStaticContourID(String)
   */
  public ContourID createStaticContourID(String name);
  
  /**
   * Create the object contour identifiers for an object.
   * All instance contour IDs for the object are created (that is,
   * virtual contours' IDs are created).
   * <p>
   * If this is called more than once on the same input, an exception
   * will be thrown.
   * @param object
   * @return list of instance contour ID, from most specific to most generic
   * @see #getInstanceContourID(ObjectReference)
   * @see #instanceContourExistsFor(ObjectReference)
   */
  public List<ContourID> createInstanceContourIDs(ObjectReference object);
  
  // TODO Plug-in change
  /**
   * Create a method contour ID for a method invocation.
   * @param method method called
   * @param context context of the method
   * @return method contour ID
   */
  public ContourID createMethodContourID(Method method, ContourID context);
  
  // TODO Plug-in change
  /**
   * Push a method contour ID onto the stack for a specific thread.  A
   * <code>null</code> ID is considered an out-of-model method call.
   * 
   * @param methodContourID the method contour ID to push
   * @param threadID the stack on which to push
   * @see #popStack(ThreadID)
   * @see #peekStack(ThreadID)
   * @see #stackSize(ThreadID)
   */
  public void pushStack(ContourID methodContourID, ThreadID threadID);
  
  // TODO Plug-in change
  /**
   * Pop and return the top method contour ID from a stack.  A <code>null</code>
   * ID is considered an out-of-model method call.
   * 
   * @param threadID
   * @return the top of the stack
   * @see #pushStack(ContourID, ThreadID)
   * @see #peekStack(ThreadID)
   * @see #stackSize(ThreadID)
   */
  public ContourID popStack(ThreadID threadID);
  
  // TODO Plug-in change
  /**
   * Peek at the top method contour ID on a specific thread's stack.  A
   * <code>null</code> ID is considered an out-of-model method call.
   * 
   * @param threadID the thread
   * @return the top method contour ID on that stack.
   * @see #popStack(ThreadID)
   * @see #pushStack(ContourID, ThreadID)
   * @see #stackSize(ThreadID)
   */
  public ContourID peekStack(ThreadID threadID);
  
  // TODO Plug-in change
  /**
   * Returns the number of stack frames on a specific thread's stack.  A stack
   * frame is represented as either a {@code ContourID} for in-model methods
   * or <code>null</code> for out-of-model methods.
   * 
   * @param threadID the thread
   * @return the size of the stack
   * @see #popStack(ThreadID)
   * @see #pushStack(ContourID, ThreadID)
   * @see #peekStack(ThreadID)
   */
  public int stackSize(ThreadID threadID);
}
