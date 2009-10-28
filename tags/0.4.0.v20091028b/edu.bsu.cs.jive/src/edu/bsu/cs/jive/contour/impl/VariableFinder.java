package edu.bsu.cs.jive.contour.impl;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourMember.InnerClass;
import edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration;
import edu.bsu.cs.jive.contour.ContourMember.Variable;
import edu.bsu.cs.jive.util.ContourID;
import edu.bsu.cs.jive.util.VariableID;

/**
 * Finds variables in a contour by their variable identifiers.
 * 
 * @author pvg
 */
class VariableFinder {
  
  /**
   * Find a specific variable within a contour. 
   * @param id the ID of the variable being sought
   * @param contour the contour being searched
   * @return the variable or null if it is not found
   */
  public synchronized static ContourMember.Variable find(VariableID id, Contour contour) {
    seeker.reset(id);
    contour.export(seeker);
    if (seeker.v!=null) return seeker.v;
    else throw new IllegalArgumentException("Variable with id " + id + 
        " not found in contour " + contour);
  }
  
  private static Seeker seeker = new Seeker();

  private static class Seeker implements Contour.Exporter {

    Variable v;
    private VariableID id;
    
    public void reset(VariableID id) {
      this.id=id;
      this.v=null;
    }
    
    public void addID(ContourID id) {}

    public void addMember(ContourMember member) {
      // If we already found the variable, we short-circuit here.
      if (v!=null) return;
      
      // If we have found th
      Object result = member.accept(visitor, null);
      if (result!=null)
        this.v = (Variable)result;
    }

    public void exportFinished() {
    }
    
    private ContourMember.Visitor visitor = new ContourMember.Visitor() {

      public Object visit(InnerClass c, Object arg) {
        return null;
      }

      public Object visit(MethodDeclaration m, Object arg) {
        return null;
      }

      public Object visit(Variable v, Object arg) {
        if (v.id().equals(Seeker.this.id))
          return v;
        else
          return null;
      }
      
    };
    
  }
}
