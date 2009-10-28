package edu.bsu.cs.jive.contour.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of transactions.
 * This is a wrapper for a Collections list, with added support for logging.
 * 
 * @author pvg
 */
class TransactionList implements List<Transaction> {

  private List<Transaction> list = new java.util.ArrayList<Transaction>(); 
  
  public void add(int index, Transaction element) {
    list.add(index,element);
  }

  public boolean add(Transaction o) {
    return list.add(o);
  }

  public boolean addAll(Collection<? extends Transaction> c) {
    return list.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends Transaction> c) {
    return list.addAll(index,c);
  }

  public void clear() {
    list.clear();
  }

  public boolean contains(Object o) {
    return list.contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }

  public Transaction get(int index) {
    return list.get(index);
  }

  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Iterator<Transaction> iterator() {
    return list.iterator();
  }

  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  public ListIterator<Transaction> listIterator() {
    return list.listIterator();
  }

  public ListIterator<Transaction> listIterator(int index) {
    return list.listIterator(index);
  }

  public Transaction remove(int index) {
    return list.remove(index);
  }

  public boolean remove(Object o) {
    return list.remove(o);
  }

  public boolean removeAll(Collection<?> c) {
    return list.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return list.retainAll(c);
  }

  public Transaction set(int index, Transaction element) {
    return list.set(index,element);
  }

  public int size() {
    return list.size();
  }

  public List<Transaction> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex,toIndex);
  }

  public Object[] toArray() {
    return list.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }
  
}
