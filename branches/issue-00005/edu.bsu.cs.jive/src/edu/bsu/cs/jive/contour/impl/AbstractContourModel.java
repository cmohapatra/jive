package edu.bsu.cs.jive.contour.impl;

import java.util.List;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.util.Publisher;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Abstract implementation of a contour model.
 * This class provides implementations for some basic shared functionality.
 * 
 * @author pvg
 */
public abstract class AbstractContourModel implements ContourModel{


  /** Publisher of event notifications*/
  private Publisher<ContourModel.Listener> publisher
    = new Publisher<ContourModel.Listener>();
  
  public void addListener(Listener l) { publisher.subscribe(l); }
  
  public void removeListener(Listener l) { publisher.unsubscribe(l); }

  public synchronized void export(final Exporter exporter) {
    // Use a depth first visitation to export this structure.
    visitDepthFirst(new Visitor() {
      public void visit(Contour contour) {
        exporter.addContour(contour, getParent(contour));
      }
    });
    exporter.exportFinished();
  }
  
  public synchronized void visitBreadthFirst(Visitor visitor) {
    List<Contour> toVisit = getRoots();
    while (toVisit.size()>0) {
    	// TODO Plug-in change
    	// Contour c = toVisit.get(0);
    	Contour c = toVisit.remove(0);
      visitor.visit(c);
      toVisit.addAll(getChildren(c));
    }
  }

  public synchronized void visitDepthFirst(Visitor visitor) {
    List<Contour> roots = getRoots();
    for (Contour contour : roots) {
      visitDepthFirst(contour,visitor);
    }
  }
  
  private void visitDepthFirst(Contour contour, Visitor visitor) {
    assert contour!=null;
    visitor.visit(contour);
    for (Contour child : getChildren(contour))
      visitDepthFirst(child,visitor);
  }
  
  // TODO Plug-in change.
//  /**
//   * Get the roots of the contour model.
//   * The returned list should not be a link to an internal representation.
//   * @return list of roots.  
//   */
//  public abstract List<Contour> getRoots();

  // TODO Plug-in change.
  protected void fireContourAdded(final Contour contour, final Contour parent) {
    publisher.publish(new Publisher.Distributor<Listener>() {
      public void deliverTo(Listener l) {
        l.contourAdded(AbstractContourModel.this, contour, parent);
      }
    });
  }
  
  protected void fireContourRemoved(final Contour contour, final Contour oldParent) {
    publisher.publish(new Publisher.Distributor<Listener>() {
      public void deliverTo(Listener l) {
        l.contourRemoved(AbstractContourModel.this, contour, oldParent);
      }
    });
  }
  
  protected void fireValueChanged(final Contour contour, final VariableID variableID, final Value newValue, final Value oldValue) {
    publisher.publish(new Publisher.Distributor<Listener>() {
      public void deliverTo(Listener l) {
        l.valueChanged(AbstractContourModel.this, contour, variableID, newValue, oldValue);
      }
    });
  }
  
}
