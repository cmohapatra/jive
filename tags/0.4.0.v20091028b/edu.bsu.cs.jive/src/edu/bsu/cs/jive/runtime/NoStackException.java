package edu.bsu.cs.jive.runtime;

import java.util.EmptyStackException;


/**
 * An exception that indicates that there is no stack for a specific thread.
 * 
 * @author pvg
 * @deprecated
 */
public final class NoStackException extends EmptyStackException {

  private static final long serialVersionUID = -4627468148181772458L;
  
  private final String mesg;
  
  public NoStackException() { 
    this(null); 
  } 
  
  public NoStackException(String mesg) {
    super();
    this.mesg=mesg;
  }
  
  @Override
  public String toString() { 
    return mesg!=null?mesg:"NoStackException";
  }
}