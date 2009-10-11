package edu.buffalo.cse.jive.internal.ui.search;

import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.SearchResultEvent;

import edu.bsu.cs.jive.events.Event;

// TODO Determine if we should use this and move out of internal package - jkczyz
/**
 * 
 * @author Dennis Patrone
 */
public class JiveSearchEvent extends SearchResultEvent {

	public enum Type {
		Added, Removed
	};

	private static final long serialVersionUID = -4642570411169022998L;

	private Event event;

	private Type type;

	public JiveSearchEvent(ISearchResult searchResult, Event event, Type type) {
		super(searchResult);
		this.event = event;
		this.type = type;
	}

	public Event getEvent() {
		return event;
	}

	public Type getType() {
		return type;
	}

}
