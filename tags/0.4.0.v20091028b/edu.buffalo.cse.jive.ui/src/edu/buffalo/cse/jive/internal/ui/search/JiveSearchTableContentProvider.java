package edu.buffalo.cse.jive.internal.ui.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.bsu.cs.jive.events.Event;
import edu.buffalo.cse.jive.sequence.EventOccurrence;

public class JiveSearchTableContentProvider implements IStructuredContentProvider {
	
	public Object[] getElements(Object inputElement) {
		if ((inputElement != null) && (inputElement instanceof edu.buffalo.cse.jive.ui.search.JiveSearchResult)) {
			edu.buffalo.cse.jive.ui.search.JiveSearchResult results = (edu.buffalo.cse.jive.ui.search.JiveSearchResult) inputElement;
			Object[] elements = results.getElements();
			EventOccurrence[] temp = new EventOccurrence[elements.length];
			System.arraycopy(elements, 0, temp, 0, elements.length);
			List<EventOccurrence> events = new ArrayList<EventOccurrence>(temp.length);
			for (EventOccurrence e : temp) {
				events.add(e);
			}
			
			Collections.sort(events, new Comparator<EventOccurrence>() {

				public int compare(EventOccurrence e1, EventOccurrence e2) {
					if (e1.underlyingEvent().number() < e2.underlyingEvent().number()) {
						return -1;
					}
					else if (e1.underlyingEvent().number() > e2.underlyingEvent().number()) {
						return 1;
					}
					else {
						return 0;
					}
				}
				
			});
			return events.toArray();
		}
		else {
			return new Object[] {};
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
