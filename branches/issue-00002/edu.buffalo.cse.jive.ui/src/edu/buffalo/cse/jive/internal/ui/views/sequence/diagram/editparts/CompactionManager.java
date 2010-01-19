package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.SequenceModel;

public class CompactionManager {
	
	private Set<ExecutionOccurrence> collapsedExecutions;
	
	private BitSet compactedEvents;
	
	private Map<Long, Integer> adjustedPositionCache;
	
	public CompactionManager() {
		collapsedExecutions = new HashSet<ExecutionOccurrence>();
		compactedEvents = new BitSet();
		adjustedPositionCache = new HashMap<Long, Integer>();
	}
	
	public boolean isCollapsed(ExecutionOccurrence execution) {
		return collapsedExecutions.contains(execution);
	}
	
	public void collapseExecution(ExecutionOccurrence execution) {
		if (!execution.isTerminated()) {
			throw new IllegalArgumentException("Only terminated executions can be collapsed.");
		}
		
		if (collapsedExecutions.contains(execution)) {
			throw new IllegalStateException("The execution is already collapsed.");
		}
		
		SequenceModel model = execution.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			collapsedExecutions.add(execution);
			compact(execution);
			adjustedPositionCache.clear();
		}
		finally {
			modelLock.unlock();
		}
	}
	
	public void expandExecution(ExecutionOccurrence execution) {
		if (!execution.isTerminated()) {
			throw new IllegalArgumentException("Only terminated executions can be expanded.");
		}
		
		if (!collapsedExecutions.contains(execution)) {
			throw new IllegalStateException("The execution is already expanded.");
		}
		
		SequenceModel model = execution.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			collapsedExecutions.remove(execution);
			
			// TODO Make this more efficient by clearing only the necessary bits 
			compactedEvents.clear();
			for (ExecutionOccurrence e : collapsedExecutions) {
				compact(e);
			}
			
			adjustedPositionCache.clear();
		}
		finally {
			modelLock.unlock();
		}
	}
	
	private void compact(ExecutionOccurrence execution) {
		Set<Long> uncompactedEvents = new HashSet<Long>(); 
		for (EventOccurrence event : execution.events()) {
			long eventNumber = event.underlyingEvent().number();
			uncompactedEvents.add(eventNumber);
		}
		
		SequenceModel model = execution.containingModel();
		Iterator<EventOccurrence> iter = model.iterator(execution);
		while (iter.hasNext()) {
			long eventNumber = iter.next().underlyingEvent().number();
			if (!uncompactedEvents.contains(eventNumber)) {
				int bitIndex = (int) eventNumber;
				compactedEvents.set(bitIndex);
			}
		}
	}
	
	public int calculateAdjustedPosition(long eventNumber) {
		if (adjustedPositionCache.containsKey(eventNumber)) {
			return adjustedPositionCache.get(eventNumber);
		}
		
		int start = (int) eventNumber;
		int result = start;
		
		for (int i = compactedEvents.nextSetBit(0); i >= 0; i = compactedEvents.nextSetBit(i + 1)) {
			if (start >= i) {
				result--;
			}
			else {
				break;
			}
		}
		
		adjustedPositionCache.put(eventNumber, result);
		return result;
	}
}
