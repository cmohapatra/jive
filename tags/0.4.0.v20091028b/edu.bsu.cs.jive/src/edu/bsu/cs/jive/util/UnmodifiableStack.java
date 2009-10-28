package edu.bsu.cs.jive.util;

import java.util.Collection;
import java.util.Stack;

/**
 * An unmodifiable view of a stack.
 *
 * @author pvg
 * @param <T> data element for the stack
 * @deprecated
 */
public class UnmodifiableStack<T> extends Stack<T> {

	/**
	 * generated serial version uid
	 */
	private static final long serialVersionUID = -5476225360268085719L;

	/**
	 * Create a new empty unmodifiable stack.
	 */
	public UnmodifiableStack() {
		super();
	}
	
	/**
	 * Copy constructor
	 * @param source
	 */
	public UnmodifiableStack(Stack<T> source) {
		this();
		super.addAll(source);
	}
	
	@Override
	public synchronized T pop() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T push(T item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean add(T o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void addElement(T obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void insertElementAt(T obj, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void removeAllElements() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean removeElement(Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void removeElementAt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected synchronized void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setElementAt(T obj, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setSize(int newSize) {
		throw new UnsupportedOperationException();
	}

}
