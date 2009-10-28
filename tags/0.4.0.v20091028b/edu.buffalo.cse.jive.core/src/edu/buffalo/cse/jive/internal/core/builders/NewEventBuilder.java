package edu.buffalo.cse.jive.internal.core.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.NewEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.JDIRequestFilter;
import edu.bsu.cs.jive.runtime.builders.ContourCreationRecordBuilder;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class NewEventBuilder extends AbstractEventBuilder implements NewEvent.Importer {

	private List<ContourCreationRecord> creationRecords;
	
	private ContourID enclosingID;
	
	public static NewEventBuilder create(ObjectReference object, ThreadReference thread, ContourUtils contourManager, StackManager stackManager, JDIRequestFilter filter, ContourID enclosingID) {
		ThreadID threadID = stackManager.threadID(thread);
		List<ContourCreationRecord> creationRecords = determineCreationRecords(object, contourManager, filter);
		return new NewEventBuilder(threadID, creationRecords, enclosingID, contourManager);
	}
	
	private static List<ContourCreationRecord> determineCreationRecords(ObjectReference object, ContourUtils contourManager, JDIRequestFilter filter) {
		List<ContourID> idList = contourManager.createInstanceContourIDs(object);
		List<ContourCreationRecord> creationRecords = new ArrayList<ContourCreationRecord>(idList.size());
		
		ClassType type = (ClassType) object.referenceType();
		Iterator<ContourID> iter = idList.iterator();
		while (iter.hasNext()) {
			assert type != null;
			ContourID id = iter.next();
			boolean isInModel = filter.acceptsClass(type.name());
			ContourCreationRecord record = new ContourCreationRecord(
					ContourCreationRecordBuilder.create(id, type, false, isInModel));
			creationRecords.add(record);
		    type = type.superclass();
		}
		
		return creationRecords;
	}
	
	private NewEventBuilder(ThreadID thread, List<ContourCreationRecord> creationRecords, ContourID enclosingID, ContourUtils contourManager) {
		super(thread, contourManager);
		this.creationRecords = creationRecords;
		this.enclosingID = enclosingID;
	}
	
	public List<ContourCreationRecord> provideContourCreationRecords() {
		return creationRecords;
	}

	public ContourID provideEnclosingContourID() {
		return enclosingID;
	}

}
