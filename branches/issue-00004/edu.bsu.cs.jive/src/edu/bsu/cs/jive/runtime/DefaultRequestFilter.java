/*
 * Created on Jul 2, 2003
 */
package edu.bsu.cs.jive.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;


/**
 * Implements common behavior for request filters.
 * 
 * @author Paul Gestwicki (pvg@cse.buffalo.edu)
 */
public class DefaultRequestFilter implements JDIRequestFilter {
	
  /**
   * The list of excluded package filters.  Each element is a String specifying a
   * regular expression filter, such as "java.*".
   */
  private List<String> exclusionList = new ArrayList<String>();
  
  /**
   * A set containing classes that are known to be accepted by the filter.
   */
  private Set<String> acceptedClassCache = new HashSet<String>();
  
  /**
   * A set containing classes that are known to be rejected by the filter.
   */
  private Set<String> rejectedClassCache = new HashSet<String>();
  
  /**
   * Adds a package filter to the exclusion list.
   * @param filter the regular expression filter, such as "java.*"
   */
  public void addExclusionFilter(String filter) {
    if (!exclusionList.contains(filter)) 
      exclusionList.add(filter);
    acceptedClassCache.clear();
    rejectedClassCache.clear();
  }
  
  /*
   * (non-Javadoc)
   * @see edu.buffalo.cse.jive.jpda.RequestFilter#applyFilterTo(com.sun.jdi.request.MethodEntryRequest)
   */
  public void filter(ClassPrepareRequest request) {
    for (String filter : exclusionList)
      request.addClassExclusionFilter(filter);
  }

  /*
   * (non-Javadoc)
   * @see edu.buffalo.cse.jive.jpda.RequestFilter#applyFilterTo(com.sun.jdi.request.MethodEntryRequest)
   */
  public void filter(MethodEntryRequest request) {
    for (String filter : exclusionList)
      request.addClassExclusionFilter(filter);
  }

  /*
   * (non-Javadoc)
   * @see edu.buffalo.cse.jive.jpda.RequestFilter#applyFilterTo(com.sun.jdi.request.MethodExitRequest)
   */
  public void filter(MethodExitRequest request) {
    for (String filter : exclusionList)
      request.addClassExclusionFilter(filter);
  }

  /*
   * (non-Javadoc)
   * @see edu.buffalo.cse.jive.jpda.RequestFilter#applyFilterTo(com.sun.jdi.request.StepRequest)
   */
  public void filter(StepRequest request) {
    for (String filter : exclusionList)
      request.addClassExclusionFilter(filter);
  }
  
  /* (non-Javadoc)
   * @see edu.bsu.cs.jive.runtime.JDIRequestFilter#acceptsClass(java.lang.String)
   */
  public boolean acceptsClass(String clazz) {
    if (acceptedClassCache.contains(clazz)) {
      return true;
    }
    else if (rejectedClassCache.contains(clazz)) {
      return false;
    }
    else {
      for (String filter : exclusionList) {
        if (match(clazz,filter)) {
          rejectedClassCache.add(clazz);
          return false;
        }
      }
      
      acceptedClassCache.add(clazz);
      return true;
    }
  }
  
  /**
   * Match an input string against a pattern.  Patterns may contain the wildcard
   * character '*' at either the beginning or end of the pattern (but not both).
   * 
   * @param input the string to match against the pattern
   * @param pattern the pattern
   * @return true if 'in' matches 'pat'
   */
  private static boolean match(String input, String pattern) {
  	// TODO Plug-in change
  	int wildcardIndex = pattern.indexOf('*');
  	if (wildcardIndex == -1) {
  		return input.equals(pattern);
  	}
  	else if (wildcardIndex == 0) {
  		if (pattern.length() == 1) {
  			return true;
  		}
  		else {
  			return input.endsWith(pattern.substring(1));
  		}
  	}
  	else if (wildcardIndex == pattern.length() - 1) {
  		return input.startsWith(pattern.substring(0, wildcardIndex - 1));
  	}
  	else {
  		return input.equals(pattern);
  	}
  }

}
