package edu.bsu.cs.jive.events;

/**
 * An event generated at the very beginning of a program's execution.
 * 
 * @author pvg
 */
public interface StartEvent extends Event {

	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
  public void export(Exporter exporter);

  /**
   * A builder for a start event.
   * @author pvg
   */
  public interface Importer extends Event.Importer {}

  /**
   * A reverse-builder for a start event.
   * @author pvg
   */
  public interface Exporter extends Event.Exporter {}
}
