package edu.buffalo.cse.jive.internal.ui.views.sequence.diagram.editparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.ReturnEvent;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.Message;
import edu.buffalo.cse.jive.sequence.MessageReceive;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.MultiThreadedSequenceModel;
import edu.buffalo.cse.jive.sequence.java.FilteredMethodActivation;
import edu.buffalo.cse.jive.sequence.java.ThreadActivation;

/**
 * A component to manage the parent-child, source-connection, and
 * target-connection relationships of the sequence model.  It traverses the
 * model once to compute these relationships.  Accessor methods are provided so
 * that the {@code EditPart}s corresponding to sequence model objects can
 * obtain their relationships.   
 * 
 * @author Jeffrey K Czyz
 */
public class ModelManager {

	/**
	 * The compaction manager being used on the model.  Traversal of the model
	 * is pruned at collapsed executions.
	 */
	private CompactionManager compactionManager;
	
	/**
	 * The state of the sequence diagram's lifelines.  When lifelines are
	 * expanded, there is a lifeline for each context of the object if a method
	 * was called on it.  Otherwise, all method activations are placed on the
	 * context of the object's derived-most class.
	 */
	private boolean expandLifelines;
	
	/**
	 * The state of the sequence diagram's thread activations.
	 */
	private boolean showThreadActivations;
	
	/**
	 * The most recent execution seen in the model traversal that has a call to
	 * a filtered method.  This is reset once the next unfiltered method is
	 * reached. 
	 */
	private ExecutionOccurrence unfilteredCaller;
	
	/**
	 * The most recent execution seen in the model traversal that has a return
	 * to a filtered method.  This is reset once the next unfiltered method is
	 * reached, if a filtered method along the way back is terminated by an
	 * exception, or if another unfiltered returner is found.
	 */
	private ExecutionOccurrence unfilteredReturner;
	
	/**
	 * The event number of the message send that resulted in the setting the
	 * {@code unfilteredCaller}. 
	 */
	private long unfilteredCallerSendEventNumber;
	
	/**
	 * The set of lifeline model objects.  These can be either {@code ContourID}s
	 * or {@code ThreadID}s.
	 */
	private Set<Object> lifelines;
	
	/**
	 * A mapping from a lifeline to a list executions to be placed on the
	 * lifeline. 
	 */
	private Map<ContourID, List<ExecutionOccurrence>> lifelineChildren;
	
	/**
	 * A mapping from a lifeline to the connections for which it is the source.
	 * Lifelines are the source of found messages.
	 */
	private Map<ContourID, List<Message>> lifelineSourceConnections;
	
	/**
	 * A mapping from a lifeline to the connections for which it is the target.
	 * Lifelines are the target of lost messages.
	 */
	private Map<ContourID, List<Message>> lifelineTargetConnections;
	
	/**
	 * A mapping from an execution to connections for which it is the source.
	 * Executions are the source of the calls it makes and its return.
	 */
	private Map<ExecutionOccurrence, List<Message>> executionSourceConnections;
	
	/**
	 * A mapping from an execution to connections for which it is the target.
	 * Executions are the target of it's initiating call and returns to it.
	 */
	private Map<ExecutionOccurrence, List<Message>> executionTargetConnections;
	
	/**
	 * A mapping from a broken message to the event number of its source (for
	 * calls) or target (for returns) location.  This is used to determine where
	 * to position the broken message.
	 * 
	 * A broken message is a message that has an undisturbed path back to an
	 * unfiltered method through only filtered methods.  
	 */
	private Map<Message, Long> brokenMessageEventNumbers;
	
	/**
	 * Constructs a model manager using the supplied compaction manager.
	 * 
	 * @param compactionManager the compaction manager used to determine how
	 *        the model traversal should be pruned
	 */
	public ModelManager(CompactionManager compactionManager) {
		this.compactionManager = compactionManager;
		expandLifelines = false;
		showThreadActivations = false;
		unfilteredCaller = null;
		unfilteredReturner = null;
		unfilteredCallerSendEventNumber = 0;
		lifelines = new LinkedHashSet<Object>();
		lifelineChildren = new HashMap<ContourID, List<ExecutionOccurrence>>();
		lifelineSourceConnections = new HashMap<ContourID, List<Message>>();
		lifelineTargetConnections = new HashMap<ContourID, List<Message>>();
		executionSourceConnections = new HashMap<ExecutionOccurrence, List<Message>>();
		executionTargetConnections = new HashMap<ExecutionOccurrence, List<Message>>();
		brokenMessageEventNumbers = new HashMap<Message, Long>();
	}
	
	/**
	 * Sets the state of the sequence diagram's lifelines.  When lifelines are
	 * expanded, there is a lifeline for each context of the object if a method
	 * was called on it.  Otherwise, all method activations are placed on the
	 * context of the object's derived-most class.
	 * 
	 * @param expanded whether lifelines should be expanded
	 */
	public void setExpandLifelines(boolean expanded) {
		expandLifelines = expanded;
	}
	
	/**
	 * Sets whether thread activations should be shown.  Hidden thread
	 * activations are treated as filtered methods.
	 * 
	 * @param show whether thread activations should be shown
	 */
	public void setShowThreadActivations(boolean show) {
		showThreadActivations = show;
	}
	
	/**
	 * Resets the manager to its an initial state.
	 */
	public void reset() {
		unfilteredCaller = null;
		unfilteredReturner = null;
		unfilteredCallerSendEventNumber = 0;
		lifelines.clear();
		lifelineChildren.clear();
		lifelineSourceConnections.clear();
		lifelineTargetConnections.clear();
		executionSourceConnections.clear();
		executionTargetConnections.clear();
		brokenMessageEventNumbers.clear();
	}
	
	/**
	 * Computes the relationships for the model's objects.
	 * 
	 * @param model the model for which to compute relationships
	 */
	public void computeRelationships(MultiThreadedSequenceModel model) {
		reset();
		for (ExecutionOccurrence root : model.getRoots()) {
			visitExecution(root);
			
			// Reset the unfiltered caller between threads
			if (unfilteredCaller != null) {
				addFoundMessage(unfilteredCaller);
				unfilteredCaller = null;
				unfilteredCallerSendEventNumber = 0;
			}
			
			// Reset the unfiltered returner between threads
			if (unfilteredReturner != null) {
				addLostMessage(unfilteredReturner);
				unfilteredReturner = null;
			}
		}
		
		// Sort each lifeline's executions by start value if there are multiple
		// threads in the model
		if (model.getThreads().size() > 1) {
			for (List<ExecutionOccurrence> executionList : lifelineChildren.values()) {
				Collections.sort(executionList, new Comparator<ExecutionOccurrence>() {
					public int compare(ExecutionOccurrence eo1, ExecutionOccurrence eo2) {
						// TODO Add start() method to ExecutionOccurrence or SequenceModel
						long firstEvent1 = eo1.events().get(0).underlyingEvent().number();
						long firstEvent2 = eo2.events().get(0).underlyingEvent().number();
						if (firstEvent1 < firstEvent2) {
							return -1;
						}
						else if (firstEvent1 > firstEvent2) {
							return 1;
						}
						else {
							return 0;
						}
					}
				});
			}
		}
	}
	
	/**
	 * Visits the supplied execution during the model traversal.
	 * 
	 * @param execution the execution to visit
	 */
	private void visitExecution(ExecutionOccurrence execution) {
		if (isFiltered(execution)) {
			visitFilteredExecution(execution);
		}
		else {
			visitUnfilteredExecution(execution);
		}
	}
	
	
	/**
	 * Returns whether the supplied execution is filtered.
	 * 
	 * @param execution the execution
	 * @return whether the execution is filtered
	 */
	private boolean isFiltered(ExecutionOccurrence execution) {
		return execution instanceof FilteredMethodActivation ||
				(!showThreadActivations && execution instanceof ThreadActivation);
	}
	
	/**
	 * Visits an filtered execution.  Filtered executions are not shown on the
	 * diagram but their calls may lead to unfiltered executions.
	 * 
	 * @param execution the filtered execution
	 */
	private void visitFilteredExecution(ExecutionOccurrence execution) {
		// Visit any called executions
		for (EventOccurrence event : execution.events()) {
			if (event instanceof MessageSend) {
				MessageSend sendEvent = (MessageSend) event;
				if (sendEvent.underlyingEvent() instanceof CallEvent) {
					visitExecution(sendEvent.message().receiveEvent().containingExecution());
				}
			}
		}
		
		// Produce a lost message if the traversal ends here or if terminated
		// by an exception
		if (!execution.isTerminated() || execution.terminator() == null) {
			if (unfilteredReturner != null) {
				addLostMessage(unfilteredReturner);
				unfilteredReturner = null;
			}
		}
	}
	
	/**
	 * Visits an unfiltered execution.  Unfiltered executions are shown on the
	 * diagram.  An unfiltered execution can be expanded or collapsed.
	 * 
	 * @param execution the unfiltered execution
	 */
	private void visitUnfilteredExecution(ExecutionOccurrence execution) {
		addExecution(execution);
		
		assert !executionSourceConnections.containsKey(execution);
		assert !executionTargetConnections.containsKey(execution);

		if (compactionManager.isCollapsed(execution)) {
			visitCollapsed(execution);
		}
		else {
			visitExpanded(execution);
		}
		
		assert executionSourceConnections.containsKey(execution);
		assert executionTargetConnections.containsKey(execution);
	}
	
	/**
	 * Adds the supplied execution as a child to the appropriate lifeline.
	 * 
	 * @param execution the execution to add
	 */
	private void addExecution(ExecutionOccurrence execution) {
		if (execution instanceof ThreadActivation) {
			if (showThreadActivations) { 
				ThreadActivation activation = (ThreadActivation) execution;
				lifelines.add(activation.thread());
			}
		}
		else {
			ContourID lifeline = determineLifeline(execution);
			if (!lifelineChildren.containsKey(lifeline)) {
				lifelineChildren.put(lifeline, new ArrayList<ExecutionOccurrence>());
			}
			
			lifelineChildren.get(lifeline).add(execution);
			lifelines.add(lifeline);
		}
		
	}
	
	/**
	 * Visits a collapsed execution.  The model traversal is pruned at collapsed
	 * executions.
	 * 
	 * @param execution the collapsed execution.
	 */
	private void visitCollapsed(ExecutionOccurrence execution) {
		// A collapsed execution is only a target for its initiator
		MessageSend initiator = execution.initiator();
		if (initiator != null) {
			List<Message> targetList = new ArrayList<Message>(1);
			addMessage(initiator.message().receiveEvent(), targetList);
			executionTargetConnections.put(execution, targetList);
		}
		else {
			List<Message> emptyList = Collections.emptyList();
			executionTargetConnections.put(execution, emptyList);
		}
		
		// A collapsed execution is only a source for its terminator
		if (execution.isTerminated()) {
			MessageSend terminator = execution.terminator();
			if (terminator != null) {
				List<Message> sourceList = new ArrayList<Message>(1);
				addMessage(terminator, sourceList);
				executionSourceConnections.put(execution, sourceList);
				return;
			}
		}
		
		List<Message> emptyList = Collections.emptyList();
		executionSourceConnections.put(execution, emptyList);
	}
	
	/**
	 * Visits an expanded execution and all its resulting executions
	 * recursively.
	 * 
	 * @param execution the expanded execution
	 */
	private void visitExpanded(ExecutionOccurrence execution) {
		List<Message> sourceList = new ArrayList<Message>();
		List<Message> targetList = new ArrayList<Message>();
		executionSourceConnections.put(execution, sourceList);
		executionTargetConnections.put(execution, targetList);
		
		for (EventOccurrence event : execution.events()) {
			if (event instanceof MessageSend) {
				MessageSend sendEvent = (MessageSend) event;
				addMessage(sendEvent, sourceList);
				
				if (sendEvent.underlyingEvent() instanceof CallEvent) {
					visitExecution(sendEvent.message().receiveEvent().containingExecution());
				}
			}
			else if (event instanceof MessageReceive) {
				MessageReceive receiveEvent = (MessageReceive) event;
				addMessage(receiveEvent, targetList);
			}
			else {
				// do nothing
			}
		}
	}
	
	/**
	 * Adds the message corresponding to the supplied {@code MessageSend} to the
	 * given list.  This method takes care of adding lost and found messages
	 * when appropriate.  It also tracks unfiltered callers and returners in
	 * case a broken message will be later formed.
	 * 
	 * @param event the event source of the message
	 * @param sourceList the list in which to add the message, if necessary
	 */
	private void addMessage(MessageSend event, List<Message> sourceList) {
		Message message = event.message();
		ExecutionOccurrence target = message.receiveEvent().containingExecution();
		
		// Determine if the message should be added when the target is filtered
		if (isFiltered(target)) {
			ExecutionOccurrence source = event.containingExecution();
			assert !isFiltered(source);
			
			// Track the source as it will act as the source of the next
			// unfiltered execution's initiator message
			if (event.underlyingEvent() instanceof CallEvent) {
				
				// Add a found message if overwriting a previously tracked source 
				if (unfilteredCaller != null) {
					addFoundMessage(unfilteredCaller);
				}
				
				unfilteredCaller = source;
				unfilteredCallerSendEventNumber = event.underlyingEvent().number();
			}
			// Track the source as its terminator message may connect back to
			// an unfiltered ancestor execution
			else {
				assert event.underlyingEvent() instanceof ReturnEvent;
				
				// Add a lost message if overwriting a previously tracked source
				if (unfilteredReturner != null) {
					addLostMessage(unfilteredReturner);
				}
				
				unfilteredReturner = source;
				sourceList.add(message);  // source execution's terminator
			}
		}
		else {
			sourceList.add(message);
		}
	}
	
	/**
	 * Adds the message corresponding to the supplied {@code MessageReceive} to
	 * the given list.  This method takes care of adding lost and found messages
	 * when appropriate.  It also tracks unfiltered callers and returners in
	 * case a broken message will be later formed.
	 * 
	 * @param event the event target of the message
	 * @param targetList the list in which to add the message, if necessary
	 */
	private void addMessage(MessageReceive event, List<Message> targetList) {
		Message message = event.message();
		ExecutionOccurrence source = message.sendEvent().containingExecution();
		
		// Determine if the message should be added when the source is filtered
		if (isFiltered(source)) {
			
			// Attempt to connect the previously tracked source with the execution
			if (event.underlyingEvent() instanceof CallEvent) {
				
				// Connect the previously tracked source with the target using
				// the target's initiator message
				if (unfilteredCaller != null) {
					executionSourceConnections.get(unfilteredCaller).add(message);
					brokenMessageEventNumbers.put(message, unfilteredCallerSendEventNumber);
					unfilteredCaller = null;
					unfilteredCallerSendEventNumber = 0;
				}
				// Otherwise, the message is connected with the lifeline as the
				// source (i.e., it is considered found)
				else {
					addFoundMessage(event.containingExecution());
				}
				
				targetList.add(message);  // target execution's initiator
			}
			else {
				assert event.underlyingEvent() instanceof ReturnEvent;
				
				// Connect the previously tracked source with the target using
				// the source's terminator message
				if (unfilteredReturner != null) {
					assert unfilteredReturner.isTerminated();
					assert unfilteredReturner.terminator() != null;
					message = unfilteredReturner.terminator().message();
					targetList.add(message);  // source execution's terminator
					brokenMessageEventNumbers.put(message, event.underlyingEvent().number());
					unfilteredReturner = null;
				}
				// Otherwise, the message is connected with the lifeline as the
				// target (i.e., it is considered lost)
				else {
					addLostMessage(event.containingExecution());
				}
			}
		}
		else {
			targetList.add(message);
		}
	}
	
	/**
	 * Adds the initiator of the supplied execution to the list of source
	 * connections of the execution's lifeline.
	 * 
	 * @param execution the execution
	 */
	private void addFoundMessage(ExecutionOccurrence execution) {
		if (execution instanceof ThreadActivation) {
			return;
		}
		
		ContourID lifeline = determineLifeline(execution);
		assert lifeline != null;
		
		if (!lifelineSourceConnections.containsKey(lifeline)) {
			lifelineSourceConnections.put(lifeline, new ArrayList<Message>());
		}
		
		Message message = execution.initiator().message();
		lifelineSourceConnections.get(lifeline).add(message);
	}
	
	/**
	 * Adds the terminator of the supplied execution to the list of target
	 * connections of the execution's lifeline.
	 * 
	 * @param execution the execution
	 */
	private void addLostMessage(ExecutionOccurrence execution) {
		if (execution instanceof ThreadActivation) {
			return;
		}
		
		ContourID lifeline = determineLifeline(execution);
		assert lifeline != null;
		
		assert execution.isTerminated();
		MessageSend terminator = execution.terminator();
		if (terminator != null) {
			if (!lifelineTargetConnections.containsKey(lifeline)) {
				lifelineTargetConnections.put(lifeline, new ArrayList<Message>());
			}
			
			Message message = terminator.message();
			lifelineTargetConnections.get(lifeline).add(message);
		}
	}
	
	/**
	 * Returns the {@code ContourID} to use as the supplied execution's
	 * lifeline.
	 * 
	 * @param execution the execution
	 * @return the execution's lifeline
	 */
	private ContourID determineLifeline(ExecutionOccurrence execution) {
		assert !isFiltered(execution);
		if (expandLifelines) {
			return execution.context();
		}
		else {
			return execution.containingModel().objectContext(execution);
		}
	}
	
	/**
	 * Returns the list of lifelines to be placed on the sequence diagram.
	 * 
	 * @return the sequence diagram's lifelines  
	 */
	public List<Object> getLifelines() {
		return new ArrayList<Object>(lifelines);
	}
	
	/**
	 * Returns the list of executions to be placed on the given lifeline.
	 * 
	 * @param lifeline the lifeline
	 * @return the lifeline's executions
	 */
	public List<ExecutionOccurrence> getLifelineChildren(ContourID lifeline) {
		if (lifelineChildren.containsKey(lifeline)) {
			return lifelineChildren.get(lifeline);
		}
		else {
			throw new IllegalArgumentException("No such lifeline: " + lifeline);
		}
	}
	
	/**
	 * Returns the list of messages for which the supplied lifeline is their
	 * source.
	 * 
	 * @param lifeline the lifeline
	 * @return the lifeline's source connections
	 */
	public List<Message> getSourceConnections(ContourID lifeline) {
		if (lifelineSourceConnections.containsKey(lifeline)) {
			return lifelineSourceConnections.get(lifeline);
		}
		else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Returns a list of messages for which the supplied lifeline is their
	 * target.
	 * 
	 * @param lifeline the lifeline
	 * @return the lifeline's target connections
	 */
	public List<Message> getTargetConnections(ContourID lifeline) {
		if (lifelineTargetConnections.containsKey(lifeline)) {
			return lifelineTargetConnections.get(lifeline);
		}
		else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Returns the list of messages for which the supplied execution is their
	 * source.
	 * 
	 * @param execution the execution
	 * @return the execution's source connections
	 */
	public List<Message> getSourceConnections(ExecutionOccurrence execution) {
		if (executionSourceConnections.containsKey(execution)) {
			return executionSourceConnections.get(execution);
		}
		else {
			throw new IllegalArgumentException("No such execution: " + execution.id());
		}
	}
	
	/**
	 * Returns the list of messages for which the supplied execution is their
	 * target.
	 * 
	 * @param execution the execution
	 * @return the execution's target connections
	 */
	public List<Message> getTargetConnections(ExecutionOccurrence execution) {
		if (executionTargetConnections.containsKey(execution)) {
			return executionTargetConnections.get(execution);
		}
		else {
			throw new IllegalArgumentException("No such execution: " + execution.id());
		}
	}
	
	/**
	 * Returns whether the supplied message is broken.  A message is broken if
	 * there is an undisturbed path between two unfiltered executions,
	 * containing at least one filtered execution, with the message as the first
	 * (for returns) or last (for calls) link. 
	 * 
	 * @param message the message
	 * @return whether the message is broken
	 */
	public boolean isBroken(Message message) {
		return brokenMessageEventNumbers.containsKey(message);
	}
	
	/**
	 * Returns the event number of the supplied message's source (for calls) or
	 * target (for returns) location.  This is used to determine where to
	 * position the broken message.
	 * 
	 * @param message a broken message
	 * @return the source or target event number of the message
	 */
	public long getFilteredMessageEventNumber(Message message) {
		if (brokenMessageEventNumbers.containsKey(message)) {
			return brokenMessageEventNumbers.get(message);
		}
		else {
			throw new IllegalArgumentException("The supplied method is not broken.");
		}
	}
}
