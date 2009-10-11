package edu.buffalo.cse.jive.internal.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.buffalo.cse.jive.sequence.EventOccurrence;
import edu.buffalo.cse.jive.sequence.ExecutionOccurrence;
import edu.buffalo.cse.jive.sequence.MessageSend;
import edu.buffalo.cse.jive.sequence.SequenceModel;
import edu.buffalo.cse.jive.sequence.Message.MessageSort;

public class JiveSearchTreeContentProvider implements ITreeContentProvider {

	Viewer viewer;
	edu.buffalo.cse.jive.ui.search.JiveSearchResult results;

	/**
	 * "targetEvents" are those events that are explicitly in the results of the
	 * JiveSearchResult.
	 */
	HashSet<EventOccurrence> targetEvents;

	/**
	 * "eventsOfInterest" are the events that are explicitly in the results of
	 * the JiveSearchResult (targetEvents), plus the call event chain needed to
	 * get from the root of a call branch to the targetEvents, for all
	 * targetEvents. This is used to prune unneeded/uninteresting event children
	 * from ExecutionOccurrences when building the search tree.
	 */
	HashSet<EventOccurrence> eventsOfInterest;

	JiveSearchTreeContentProvider(Viewer viewer) {
		this.viewer = viewer;
		targetEvents = new HashSet<EventOccurrence>();
		eventsOfInterest = new HashSet<EventOccurrence>();
	}

	public Object[] getChildren(Object parentElement) {
		if (results == null) {
			return new Object[0];
		}

		ReentrantLock modelLock = null;
		try {
			if (parentElement instanceof ExecutionOccurrence) {
				ExecutionOccurrence parent = (ExecutionOccurrence) parentElement;
				SequenceModel model = parent.containingModel();
				modelLock = model.getModelLock();
				modelLock.lock();
				List<EventOccurrence> events = parent.events();
				events.retainAll(eventsOfInterest);
				List<Object> children = new ArrayList<Object>();
				for (EventOccurrence eo : events) {
					if (targetEvents.contains(eo)) {
						// We add the actual event if it is in the result of
						// the query
						children.add(eo);
					} else if (eo instanceof MessageSend) {
						// Or, this must be a message send event which leads to
						// an
						// event we care about. Add the ExecutionOccurrence
						// target
						// to the children...
						MessageSend msg = (MessageSend) eo;
						// but only if it is a call -- jkczyz
						if (msg.message().messageSort() != MessageSort.REPLY) {
							children.add(msg.message().receiveEvent().containingExecution());
						}
					} else {
						throw new InternalError("Unexpected type: " + eo);
					}
				}
				return children.toArray();
			} else if (parentElement instanceof MessageSend) {
				MessageSend msg = (MessageSend) parentElement;
				if (msg.message().messageSort() == MessageSort.REPLY) {
					return new Object[0];
				}
				else {
					return getChildren(msg.message().receiveEvent().containingExecution());
				}
			} else if (parentElement instanceof EventOccurrence) {
				return new Object[0];
			} else {
				throw new InternalError("Should not get here.");
			}
		} finally {
			if (modelLock != null) {
				modelLock.unlock();
			}
		}
	}

	public Object getParent(Object element) {
		ReentrantLock modelLock = null;
		try {
			if (element instanceof ExecutionOccurrence) {
				ExecutionOccurrence exe = (ExecutionOccurrence) element;
				modelLock = exe.containingModel().getModelLock();
				modelLock.lock();
				return exe.initiator();
			} else if (element instanceof EventOccurrence) {
				EventOccurrence evt = (EventOccurrence) element;
				modelLock = evt.containingExecution().containingModel()
						.getModelLock();
				modelLock.lock();
				return evt.containingExecution();
			} else {
				throw new InternalError("Should not get here.");
			}
		} finally {
			if (modelLock != null) {
				modelLock.unlock();
			}
		}
	}

	public boolean hasChildren(Object element) {
		// TODO Inefficient test...
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		ReentrantLock modelLock = null;
		try {
			if ((inputElement != null)
					&& (inputElement instanceof edu.buffalo.cse.jive.ui.search.JiveSearchResult)) {
				results = (edu.buffalo.cse.jive.ui.search.JiveSearchResult) inputElement;
				Object[] elements = results.getElements();
				EventOccurrence[] events = new EventOccurrence[elements.length];
				System.arraycopy(elements, 0, events, 0, elements.length);

				eventsOfInterest.clear();
				targetEvents.clear();
				targetEvents.addAll(Arrays.asList(events));
				eventsOfInterest.addAll(targetEvents);

				if (events.length > 0) {
					modelLock = events[0].containingExecution()
							.containingModel().getModelLock();
					modelLock.lock();
				}
			
				LinkedList<ExecutionOccurrence> roots = new LinkedList<ExecutionOccurrence>();
				for (EventOccurrence ev : events) {
					ExecutionOccurrence ex = null;
					while (ev != null) {
						ex = ev.containingExecution();
						eventsOfInterest.add(ev);
						ev = ex.initiator();
					}
					if (!roots.contains(ex)) {
						roots.add(ex);
					}
				}
				return roots.toArray();
			} else {
				results = null;
				targetEvents.clear();
				eventsOfInterest.clear();
				return new Object[] {};
			}
		} finally {
			if (modelLock != null) {
				modelLock.unlock();
			}
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
