package edu.buffalo.cse.jive.internal.core.builders;

import com.sun.jdi.Field;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ModificationWatchpointEvent;

import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.events.AssignEvent;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.runtime.builders.VariableIDFactory;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;
import edu.buffalo.cse.jive.internal.core.StackManager;

public class AssignEventBuilder extends AbstractEventBuilder implements AssignEvent.Importer {
	
	private ContourID context;
	
	private VariableID variable;
	
	private Value value;
	
	public static AssignEventBuilder create(ModificationWatchpointEvent event, ContourUtils contourManager, StackManager stackManager) {
		ThreadReference thread = event.thread();
		ThreadID threadID = stackManager.threadID(thread);
		ContourID context = determineContext(event, contourManager);
		VariableID variable = determineVariable(event);
		Value value = determineValue(event, contourManager);
		
		return new AssignEventBuilder(threadID, context, variable, value, contourManager);
	}
	
	private static ContourID determineContext(ModificationWatchpointEvent event, ContourUtils contourManager) {
		Field field = event.field();
		String declaringType = event.field().declaringType().name();

		if (field.isStatic()) {
			return contourManager.getStaticContourID(declaringType);
		}
		else {
			return contourManager.getInstanceContourID(event.object(), declaringType);
		}
	}
	
	private static VariableID determineVariable(ModificationWatchpointEvent event) {
		return VariableIDFactory.instance().create(event.field());
	}
	
	private static Value determineValue(ModificationWatchpointEvent event, ContourUtils contourManager) {
		return ValueFactory.instance().createValue(event.valueToBe(), contourManager);
	}
	
	private AssignEventBuilder(ThreadID thread, ContourID context, VariableID variable, Value value, ContourUtils contourManager) {
		super(thread, contourManager);
		this.context = context;
		this.variable = variable;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Importer#provideContourID()
	 */
	public ContourID provideContourID() {
		return context;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Importer#provideNewValue()
	 */
	public Value provideNewValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see edu.bsu.cs.jive.events.AssignEvent.Importer#provideVariableID()
	 */
	public VariableID provideVariableID() {
		return variable;
	}
}
