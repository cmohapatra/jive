package edu.bsu.cs.jive.runtime.builders;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import edu.bsu.cs.jive.contour.Value.Visitor;
import edu.bsu.cs.jive.runtime.ContourUtils;
import edu.bsu.cs.jive.util.ContourID;

/**
 * Factory class that produces values for JDI data.
 * 
 * @author pvg
 */
public class ValueFactory {

	public static ValueFactory instance() {
		return SINGLETON;
	}

	private static final ValueFactory SINGLETON = new ValueFactory();

	private ValueFactory() {}

	public edu.bsu.cs.jive.contour.Value createValue(com.sun.jdi.Value v,
			ContourUtils utils) {
		
		// TODO plug-in change
		if (v == null) {
			return new NullImpl();
		}
		
		Type t = v.type();

		if (t instanceof PrimitiveType)
			return createValue((PrimitiveType) t, v);
		else
			return createValue((ReferenceType) t, v, utils);
	}
	
	// TODO Plug-in change
	public edu.bsu.cs.jive.contour.Value createValue(ContourID id) {
		return new ContourReferenceImpl(id);
	}
	
	// TODO Plug-in change
	public edu.bsu.cs.jive.contour.Value createValue(String value) {
		return new EncodedValueImpl(value);
	}

	private edu.bsu.cs.jive.contour.Value createValue(PrimitiveType t, Value v) {
		return new EncodedValueImpl(v.toString());
	}

	private edu.bsu.cs.jive.contour.Value createValue(ReferenceType t, Value v,
			ContourUtils utils) {
		assert v instanceof ObjectReference;

		ObjectReference objectRef = (ObjectReference) v;

		// If the value has a contour, then return the contour ID as a value.
		if (utils.instanceContourExistsFor(objectRef)) {
			return new ContourReferenceImpl(
						utils.getInstanceContourID(objectRef, t.name()));
		}
		// If the value doesn't have a contour, it must be out of the model;
		// otherwise we would have the contour in the model... right?
		else 
			return new EncodedValueImpl(v.toString());
	}

	private class EncodedValueImpl implements
			edu.bsu.cs.jive.contour.Value.Encoded {

		/** Stringified form of the value */
		private final String value;

		/**
		 * Create an encoded value from its string representation.
		 * 
		 * @param value
		 */
		public EncodedValueImpl(String value) {
			this.value = value;
		}

		public final Object accept(Visitor v, Object arg) {
			return v.visit(this, arg);
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private class ContourReferenceImpl implements
			edu.bsu.cs.jive.contour.Value.ContourReference {

		private final ContourID contourID;

		public ContourReferenceImpl(ContourID contourID) {
			this.contourID = contourID;
		}

		public ContourID getContourID() {
			assert contourID != null;
			return contourID;
		}

		public final Object accept(Visitor v, Object arg) {
			return v.visit(this, arg);
		}
    
    public void export(Exporter e) {
      e.addContourID(contourID);
    }
		
		@Override
		public String toString() {
			return contourID.toString();
		}
	}
	
	// TODO plug-in change
	private class NullImpl implements edu.bsu.cs.jive.contour.Value.Null {
		
		public final Object accept(Visitor v, Object arg) {
			return v.visit(this, arg);
		}
		
		public String toString() {
			return "null";
		}
	}
	
}
