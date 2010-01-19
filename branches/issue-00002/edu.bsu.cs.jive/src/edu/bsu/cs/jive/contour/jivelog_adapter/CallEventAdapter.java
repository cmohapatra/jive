package edu.bsu.cs.jive.contour.jivelog_adapter;

import java.util.LinkedList;
import java.util.List;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.MethodContourCreationRecord;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.ContourMember.InnerClass;
import edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration;
import edu.bsu.cs.jive.contour.ContourMember.Variable;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.impl.JavaInteractiveContourModel;
import edu.bsu.cs.jive.events.CallEvent;
import edu.bsu.cs.jive.events.CallEvent.Caller;
import edu.bsu.cs.jive.events.CallEvent.Target;
import edu.bsu.cs.jive.runtime.builders.ValueFactory;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.ThreadID;
import edu.bsu.cs.jive.util.VariableID;

class CallEventAdapter implements EventAdapter, CallEvent.Exporter {

	// TODO Plug-in change
	private long eventNumber;
  private MethodContourCreationRecord ccr;
  private ContourID enclosingContourID;
  // TODO Plug-in change
  private List<Value> actualParameterValues;
  private List<VariableID> variables;
  private Value returnPoint;
  
  public void addActualParams(List<Value> actuals) {
    // TODO Plug-in change
  	actualParameterValues = actuals;
  	variables = new LinkedList<VariableID>();
  }

  public void addCaller(Caller caller) {
    // TODO Plug-in change
  	caller.accept(callerVisitor, null);
  }
  

  public void addTarget(Target target) {
    // Delegate processing to the target visitor.
    target.accept(targetVisitor,null);
  }

  // TODO Plug-in change
  public void addNumber(long n) {
  	eventNumber = n;
  }

  public void addThreadID(ThreadID thread) {}

  public void apply(JavaInteractiveContourModel cm) {
    // If the ccr is not null, then we must be dealing with an in-model 
    // call.
    if (ccr!=null) {
      // If the ccr is not null, then we'd better know where the contour goes
      assert enclosingContourID!=null;
      // TODO Plug-in change
      assert actualParameterValues != null;
      assert variables != null;
      assert returnPoint != null;
      
      cm.startTransactionRecording();
      
      // TODO Plug-in change
      MethodContour contour = cm.addMethodContour(ccr, enclosingContourID);
      contour.export(contourExporter);
      assert variables.size() > actualParameterValues.size();
      ContourID contourID = contour.id();
      for (Value value : actualParameterValues) {
      	VariableID variableID = variables.remove(0);
      	cm.setValue(contourID, variableID, value);
      }
      VariableID rpdlID = variables.get(variables.size() - 1);
      cm.setValue(contourID, rpdlID, returnPoint);
      // TODO Plug-in change
      cm.endTransactionRecording(eventNumber);
    }
    
    // cleanup
    ccr=null;
    enclosingContourID=null;
    // TODO Plug-in change
    actualParameterValues = null;
    variables = null;
    returnPoint = null;
  }
  
  // TODO Plug-in change
  private final Caller.Visitor callerVisitor = new Caller.Visitor() {

		public Object visit(Caller.InModel caller, Object obj) {
			ValueFactory factory = ValueFactory.instance();
			returnPoint = factory.createValue(caller.contour());
			return null;
		}

		public Object visit(Caller.OutOfModel caller, Object obj) {
			ValueFactory factory = ValueFactory.instance();
			returnPoint = factory.createValue(caller.description());
			return null;
		}

		public Object visit(Caller.System caller, Object obj) {
			ValueFactory factory = ValueFactory.instance();
			returnPoint = factory.createValue(caller.toString());
			return null;
		}
  	
  };
  
  private final Target.Visitor targetVisitor = new Target.Visitor() {

    public Object visit(Target.InModel target, Object obj) {
      enclosingContourID = target.enclosing();
      target.export(inModelTargetExporter);
      return null;
    }

    public Object visit(Target.OutOfModel target, Object obj) {
      // Nothing special to do here.
      assert enclosingContourID == null;
      return null;
    }

    private final Target.InModel.Exporter inModelTargetExporter =
      new Target.InModel.Exporter() {
        public void addContourCreationRecord(MethodContourCreationRecord ccr) {
          CallEventAdapter.this.ccr = ccr;
        }
    };
    
  };
  
  // TODO Plug-in change
  private final Contour.Exporter contourExporter = new Contour.Exporter() {

		public void addID(ContourID id) {
			// do nothing
		}

		public void addMember(ContourMember member) {
			member.accept(contourMemberVisitor, null);
		}

		public void exportFinished() {
			// do nothing
		}
  	
  };
  
  // TODO Plug-in change
  private final ContourMember.Visitor contourMemberVisitor = new ContourMember.Visitor() {

		public Object visit(MethodDeclaration m, Object arg) {
			// TODO Add support for MethodDeclarations
			return null;
		}

		public Object visit(Variable v, Object arg) {
			variables.add(v.id());
			return null;
		}

		public Object visit(InnerClass c, Object arg) {
			// TODO Add support for InnerClasses
			return null;
		}
  	
  };
}
