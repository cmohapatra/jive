package edu.bsu.cs.jive.runtime;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.UnmodifiableStack;

/**
 * Default implementation of a thread stacks manager.
 *
 * @author pvg
 * @deprecated
 */
class ThreadStacks {

	private Map<ThreadID,Stack<ContourID>> map = new HashMap<ThreadID,Stack<ContourID>>();
	
	public Stack<ContourID> methodStack(ThreadID threadID) {
		if (threadID==null) throw new IllegalArgumentException();
		
		Stack<ContourID> stack = map.get(threadID);
		if (stack==null)
			return new UnmodifiableStack<ContourID>();
		else
			return new UnmodifiableStack<ContourID>(stack);
	}

	public ContourID peekStack(ThreadID threadID) throws EmptyStackException,NoStackException{
		if (threadID==null) throw new IllegalArgumentException();
		
		Stack<ContourID> stack = map.get(threadID);
		if (stack==null) 
			throw new NoStackException("No stack for threadID: " + threadID);
    else if (stack.size()==0)
      throw new EmptyStackException();
		else
			return stack.peek();
	}

	public ContourID popStack(ThreadID threadID) throws EmptyStackException,NoStackException {
		if (threadID==null) throw new IllegalArgumentException();
		
		Stack<ContourID> stack = map.get(threadID);
		if (stack==null) 
			throw new NoStackException("No stack for threadID: " + threadID);
    else if (stack.size()==0)
      throw new EmptyStackException();
		else
			return stack.pop();
	}

	public void pushStack(ContourID contourID, ThreadID threadID) {
		// TODO Plug-in change
		if (threadID==null) 
			throw new IllegalArgumentException();
		
		Stack<ContourID> stack = map.get(threadID);
		// If there isn't a stack, create it and record it
		if (stack==null) {
			stack = new Stack<ContourID>();
			map.put(threadID, stack);
		}
		stack.push(contourID);
	}
	
}