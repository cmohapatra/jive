package edu.buffalo.cse.jive.internal.core.builders;

import java.util.Collections;
import java.util.List;

import com.sun.jdi.ClassType;
import com.sun.jdi.ThreadReference;

import edu.bsu.cs.jive.contour.ContourCreationRecord;
import edu.bsu.cs.jive.events.LoadEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ContourCreationRecordBuilder;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class LoadEventBuilder extends AbstractEventBuilder implements LoadEvent.Importer {

	private ContourCreationRecord creationRecord;
	
	private ContourID superclass;
	
	public static LoadEventBuilder create(ClassType type, ThreadReference thread, boolean isInModel, ContourUtils contourManager, StackManager stackManager) {
		ThreadID threadID = stackManager.threadID(thread);
		ContourCreationRecord creationRecord = determineCreationRecord(type, contourManager, isInModel);
		ContourID superclass = determineSuperClass(type, contourManager);
		return new LoadEventBuilder(threadID, creationRecord, superclass, contourManager);
	}
	
	private static ContourCreationRecord determineCreationRecord(ClassType type, ContourUtils contourManager, boolean isInModel) {
		ContourID id = contourManager.createStaticContourID(type.name());
		return new ContourCreationRecord(ContourCreationRecordBuilder.create(id, type, true, isInModel));
	}
	
	private static ContourID determineSuperClass(ClassType type, ContourUtils contourManager) {
		ClassType superclass = type.superclass();
		if (superclass != null) {
			return contourManager.getStaticContourID(superclass.name());
		}
		else {
			return null;
		}
	}
	
	private LoadEventBuilder(ThreadID thread, ContourCreationRecord creationRecord, ContourID superclass, ContourUtils contourManager) {
		super(thread, contourManager);
		this.creationRecord = creationRecord;
		this.superclass = superclass;
	}
	
	public List<ContourCreationRecord> provideContourCreationRecords() {
		return Collections.singletonList(creationRecord);
	}

	public ContourID provideEnclosingContourID() {
		return superclass;
	}
	
	
}
