package edu.bsu.cs.jive.events;

/**
 * An event corresponding to the exit of the VM.
 *
 * @author pvg
 */
public interface ExitEvent extends Event {

	/**
   * Exports the event.
   * @param exporter the reverse-builder
   */
	public void export(Exporter exporter);
  
  /**
   * A builder for an exit event.
   * @author pvg
   */
  public interface Importer extends Event.Importer {}
  
  /**
   * A reverse-builder for an exit event.
   * @author pvg
   */
  public interface Exporter extends Event.Exporter {}
}
