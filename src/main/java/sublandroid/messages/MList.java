package sublandroid.messages;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;


public class MList<M extends Message> extends Message implements java.util.List<M> {

	transient protected LinkedList<M> elements = new LinkedList<>();

	public MList() {

	}

	public MList(M... elements) {
		for (M element : elements)
			this.elements.add(element);
	}

	public boolean add(M e) {
		return elements.add(e);
	}

	public void add(int index, M element) {
		elements.add(index, element);
	}

	public boolean addAll(Collection<? extends M> c) {
		return elements.addAll(c);
	}

	public void addAll(M... elements) {
		for (M e : elements)
			this.elements.add(e);
	}

	public boolean addAll(int index, Collection<? extends M> c) {
		return elements.addAll(index, c);
	}

	public void clear() {
		elements.clear();
	}

	public boolean contains(Object o)  {
		return elements.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public Iterator<M> iterator() {
		return elements.iterator();
	}

	public M get(int index) {
		return elements.get(index);
	}

	public int hashCode() {
		return elements.hashCode();
	}

	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	public ListIterator<M> listIterator() {
		return elements.listIterator();
	}

	public ListIterator<M> listIterator(int index) {
		return elements.listIterator(index);
	}

	public M remove(int index) {
		return elements.remove(index);
	}

	public boolean remove(Object o) {
		return elements.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	public M set(int index, M element) {
		return elements.set(index, element);
	}

	public int size() {
		return elements.size();
	}

	@Override
	public java.util.List<M> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return elements.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

}