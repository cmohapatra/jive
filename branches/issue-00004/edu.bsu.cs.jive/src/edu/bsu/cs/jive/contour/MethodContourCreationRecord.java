package edu.bsu.cs.jive.contour;

import edu.bsu.cs.jive.util.ThreadID;

/**
 * Combines a normal contour creation record with a method's thread
 * of execution.
 * 
 * @author pvg
 */
public class MethodContourCreationRecord extends ContourCreationRecord {

  private final ThreadID thread;
  
  public MethodContourCreationRecord(Importer builder) {
    super(builder);
    this.thread = builder.provideThread();
  }
  
  /**
   * A builder for method contour creation records.
   * 
   * @author pvg
   */
  public interface Importer extends ContourCreationRecord.Importer {
    public ThreadID provideThread();
  }
  
  /**
   * A reverse-builder for method contour creation records.
   * 
   * @author pvg
   */
  public interface Exporter extends ContourCreationRecord.Exporter {
    public void addThread(ThreadID thread);
  }
  
  public void export(MethodContourCreationRecord.Exporter e) {
    super.export(e);
    e.addThread(thread);
  }
  
  /** 
   * Get the thread on which the method was called.
   * @return thread
   */
  public ThreadID thread() { return thread; }
}
