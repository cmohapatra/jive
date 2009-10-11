package edu.bsu.cs.jive.contour;

import java.util.NoSuchElementException;

/**
 * Identifies a contour model that supports forward and reverse program
 * execution.
 * <p>
 * An interactive contour model will throw an exception if the client
 * tries to modify the state of the contour model while stepping back
 * through previous states.
 * 
 * @author pvg
 */
public interface InteractiveContourModel extends ContourModel {
  
  /**
   * Count the number of transactions in this interactive contour model.
   * @return transaction count
   */
  public int countTransactions();
  
  /**
   * Play a step-forward transaction.
   * This only works if there is a transaction available to be committed.
   * <p>
   * This is a synchronous method: when it returns, the stepping backward
   * has completed.  (Do not confuse this with a <i>synchronized</i> method.)
   * 
   * @throws NoSuchElementException if there is no forward step to be
   *  executed.
   */
  public void stepForward() throws NoSuchElementException;
  
  /**
   * Play a step-backward transaction.
   * This is only possible if there is a transaction to roll back.
   * <p>
   * This is a synchronous method: when it returns, the stepping backward
   * has completed.  (Do not confuse this with a <i>synchronized</i> method.)
   * 
   * @throws NoSuchElementException if the step backwards cannot be 
   *  completed because that information is either not available (not
   *  cached).
   * @throws IndexOutOfBoundsException if the request was to move
   *  back from the initial state (state zero).
   */
  public void stepBackward() throws NoSuchElementException, IndexOutOfBoundsException;
  
  /**
   * Get the index of the <i>next</i> transaction, the transaction
   * that would be played if we call {@link #stepForward()}.
   * @return index of the next (forward) transaction
   * @throws NoSuchElementException if there is no recorded "next"
   *  transaction that can be committed.
   */
  public int getNextTransactionIndex();
  
  /**
   * Get the index of the <i>previous</i> transaction, the transaction
   * that would be played if we call {@link #stepBackward()}.
   * @return index of the previous (backward) transaction.
   * @throws NoSuchElementException if there is no recorded "previous 
   * transaction that can be rolled back.
   */
  public int getPrevTransactionIndex();
  
  /**
   * Indicates if this object is in a state that is ready to record
   * a transaction.
   * The model is ready to record if it is not playing back through
   * history, meaning that there are no future transactions recorded
   * and all previous transactions are committed.
   * @return true if this is ready to record
   */
  public boolean readyToRecord();
  
  /**
   * Returns true if this interactive contour model can step backwards.
   * @return true if there are recorded states and we can step backwards
   */
  public boolean canStepBackward();
  
  /**
   * Returns true if this interactive contour model can step forward
   *             <em>through pre-recorded states</em>.
   * @return true if there are pre-recorded states through which we can
   *  step forward
   * @see #readyToRecord()
   */
  public boolean canStepForwardThroughRecordedStates();
  
  // TODO Plug-in change
  /**
   * Returns the index of the transaction created as a result of the event with
   * the supplied event number.
   * 
   * @param eventNumber the event resulting in the transaction
   * @return the transaction index corresponding to the event number
   * @throws NoSuchElementException if there is no such transaction
   */
  public int getTransactionIndex(long eventNumber);
  
  // TODO Plug-in change
  /**
   * Returns the number of the event which initiated the transaction identified
   * by the supplied index.
   * 
   * @param transactionIndex the transaction index resulting from the event number
   * @return the event number corresponding to the transaction index
   */
  public long getEventNumber(int transactionIndex);
}