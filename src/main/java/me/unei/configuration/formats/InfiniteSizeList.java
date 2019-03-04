package me.unei.configuration.formats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class InfiniteSizeList<E> extends ArrayList<E> implements List<E>, Cloneable, Serializable, Storage<E>
{
	private static final long serialVersionUID = 3928206292547481992L;

	private static final int DEFAULT_CAPACITY = 10;

	private static final int[] EMPTY_ELEMENTKEYS = {};
	private static final Object[] EMPTY_ELEMENTVALUES = {};

	private static final int[] DEFAULTCAPACITY_EMPTY_ELEMENTKEYS = {};
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTVALUES = {};

	private transient int[] elementKeys;
	private transient Object[] elementValues;

	private int allocated;
	private int size;

	private int getIndexForKey(int key) {
		return getIndexForKey(elementKeys, key);
	}

	@Override
	public StorageType getStorageType() {
		return StorageType.LIST;
	}

	@Override
	public E get(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return this.get(key.getKeyInt());
		}
		return null;
	}

	@Override
	public void set(Key key, E value) {
		if (key != null && key.getType() == this.getStorageType()) {
			this.set(key.getKeyInt(), value);
		}
	}

	@Override
	public void remove(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			this.remove(key.getKeyInt());
		}
	}

	@Override
	public boolean has(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return (this.getIndexForKey(key.getKeyInt()) >= 0);
		}
		return false;
	}

	@Override
	public Set<String> keySet() {
		Set<String> r = new HashSet<String>(allocated);
		for (int i = 0; i < elementKeys.length; ++i) {
			if (elementValues[i] != null) {
				r.add(Integer.toString(elementKeys[i]));
			}
		}
		return r;
	}

	public static int getIndexForKey(int[] keys, int key)
	{
		return Arrays.binarySearch(keys, key);
	}

	private void freeInsert(int position)
	{
		if (elementValues[position] != null)
		{
			System.arraycopy(elementKeys, position, elementKeys, position + 1, elementKeys.length - position - 1);
			System.arraycopy(elementValues, position, elementValues, position + 1, elementValues.length - position - 1);
		}
	}

	private void newKey(int key, int insert_point, E obj)
	{
		if ((insert_point + 1) >= elementKeys.length)
		{
			grow(insert_point + 2);
		}
		freeInsert(insert_point);
		elementKeys[insert_point] = key;
		elementValues[insert_point] = obj;
	}

	private Object insertAt(int key, E obj) {
		int  index = getIndexForKey(key);
		if (index < 0)
		{
			int insert_point = -(index + 1);
			newKey(key, insert_point, obj);
			size = Math.max(size, key + 1);
			++allocated;
			return null;
		}
		else
		{
			Object old = elementValues[index];
			elementValues[index] = obj;
			size = Math.max(size, key + 1);
			return old;
		}
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param  initialCapacity  the initial capacity of the list
	 * @throws IllegalArgumentException if the specified initial capacity
	 *         is negative
	 */
	public InfiniteSizeList(int initialCapacity) {
		super(0);
		if (initialCapacity > 0) {
			this.elementKeys = new int[initialCapacity];
			this.elementValues = new Object[initialCapacity];
		} else if (initialCapacity == 0) {
			this.elementKeys = EMPTY_ELEMENTKEYS;
			this.elementValues = EMPTY_ELEMENTVALUES;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: "+
					initialCapacity);
		}
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public InfiniteSizeList() {
		super(0);
		this.elementKeys = DEFAULTCAPACITY_EMPTY_ELEMENTKEYS;
		this.elementValues = DEFAULTCAPACITY_EMPTY_ELEMENTVALUES;
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {
		int minExpand = (elementValues != DEFAULTCAPACITY_EMPTY_ELEMENTVALUES)
				// any size if not default element table
				? 0
						// larger than default for default empty table. It's already
						// supposed to be at default size.
						: DEFAULT_CAPACITY;

		if (minCapacity > minExpand) {
			ensureExplicitCapacity(minCapacity);
		}
	}

	private static int calculateCapacity(Object[] elementValues, int minCapacity) {
		if (elementValues == DEFAULTCAPACITY_EMPTY_ELEMENTVALUES) {
			return Math.max(DEFAULT_CAPACITY, minCapacity);
		}
		return minCapacity;
	}

	private void ensureCapacityInternal(int minCapacity) {
		ensureExplicitCapacity(calculateCapacity(elementValues, minCapacity));
	}

	private void ensureExplicitCapacity(int minCapacity) {
		modCount++;

		// overflow-conscious code
		if (minCapacity - elementValues.length > 0)
			grow(minCapacity);
	}

	/**
	 * The maximum size of array to allocate.
	 * Some VMs reserve some header words in an array.
	 * Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * Increases the capacity to ensure that it can hold at least the
	 * number of elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = Math.max(elementValues.length, elementKeys.length);
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		elementValues = Arrays.copyOf(elementValues, newCapacity);
		elementKeys = Arrays.copyOf(elementKeys, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ?
				Integer.MAX_VALUE :
					MAX_ARRAY_SIZE;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++)
				if (elementValues[i]==null)
					return elementKeys[i];
		} else {
			for (int i = 0; i < size; i++)
				if (o.equals(elementValues[i]))
					return elementKeys[i];
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size-1; i >= 0; i--)
				if (elementValues[i]==null)
					return elementKeys[i];
		} else {
			for (int i = size-1; i >= 0; i--)
				if (o.equals(elementValues[i]))
					return elementKeys[i];
		}
		return -1;
	}

	/**
	 * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
	 * elements themselves are not copied.)
	 *
	 * @return a clone of this <tt>ArrayList</tt> instance
	 */
	public Object clone() {
		InfiniteSizeList<?> v = (InfiniteSizeList<?>) super.clone();
		v.elementValues = Arrays.copyOf(elementValues, elementValues.length);
		v.elementKeys = Arrays.copyOf(elementKeys, elementKeys.length);
		v.modCount = 0;
		return v;
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in proper sequence (from first to last element).
	 *
	 * <p>The returned array will be "safe" in that no references to it are
	 * maintained by this list.  (In other words, this method must allocate
	 * a new array).  The caller is thus free to modify the returned array.
	 *
	 * <p>This method acts as bridge between array-based and collection-based
	 * APIs.
	 *
	 * @return an array containing all of the elements in this list in
	 *         proper sequence
	 */
	public Object[] toArray() {
		Object[] result = new Object[size];
		Arrays.fill(result, null);
		for (int i = 0; i < elementKeys.length; ++i) {
			if (elementValues[i] != null) {
				result[elementKeys[i]] = elementValues[i];
			}
		}
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element); the runtime type of the returned
	 * array is that of the specified array.  If the list fits in the
	 * specified array, it is returned therein.  Otherwise, a new array is
	 * allocated with the runtime type of the specified array and the size of
	 * this list.
	 *
	 * <p>If the list fits in the specified array with room to spare
	 * (i.e., the array has more elements than the list), the element in
	 * the array immediately following the end of the collection is set to
	 * <tt>null</tt>.  (This is useful in determining the length of the
	 * list <i>only</i> if the caller knows that the list does not contain
	 * any null elements.)
	 *
	 * @param a the array into which the elements of the list are to
	 *          be stored, if it is big enough; otherwise, a new array of the
	 *          same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException if the runtime type of the specified array
	 *         is not a supertype of the runtime type of every element in
	 *         this list
	 * @throws NullPointerException if the specified array is null
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		Object[] elementData = this.toArray();
		if (a.length < size)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size)
			Arrays.fill(a, size, a.length, null);
		return a;
	}

	// Positional Access Operations

	E elementData1(int index) {
		int rel = getIndexForKey(index);
		return elementData0(rel);
	}

	@SuppressWarnings("unchecked")
	E elementData0(int index) {
		if (index >= elementValues.length) {
			throw new ConcurrentModificationException();
		}
		if (index < 0) {
			return null;
		}
		return (E) elementValues[index];
	}

	public static <A> A elementData(int[] keys, Object[] values, int key) {
		int rel = getIndexForKey(keys, key);
		return elementData0(values, rel);
	}

	public static <A> A elementData0(Object[] values, int index) {
		if (index >= values.length) {
			throw new ConcurrentModificationException();
		}
		if (index < 0) {
			return null;
		}
		return autoCast(values[index]);
	}

	@SuppressWarnings("unchecked")
	private static <A> A autoCast(Object in)
	{
		return (A) in;
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public E get(int index) {
		rangeCheck(index);

		return elementData1(index);
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 *
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 */
	public E set(int index, E element) {
		if (element == null) {
			return remove(index);
		}

		return autoCast(insertAt(index, element));
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param e element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 */
	public boolean add(E e) {
		if (e == null) {
			return false;
		}
		insertAt(size, e);
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public void add(int index, E element) {
		if (element == null) {
			remove(index);
			return;
		}
		rangeCheckForAdd(index);
		insertAt(index, element);
	}

	private void deleteAt(int index)
	{
		if (elementValues[index] != null) {
			allocated -= 1;
			if (allocated <= 0) {
				allocated = 0;
				size = 0;
			}
		}
		elementKeys[index] = -1;
		elementValues[index] = null;
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 *
	 * @param index the index of the element to be removed
	 * @return the element that was removed from the list
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public E remove(int index) {
		rangeCheck(index);

		modCount++;
		int idx = getIndexForKey(index);
		E oldValue = elementData0(idx);
		if (idx >= 0) {
			deleteAt(idx);

			if ((index + 1) == size) {
				if (idx > 0) {
					int prevIdx = elementKeys[idx - 1];
					size = (prevIdx + 1);
				} else {
					size = 0;
				}
			}
		}

		return oldValue;
	}

	/**
	 * Removes the first occurrence of the specified element from this list,
	 * if it is present.  If the list does not contain the element, it is
	 * unchanged.  More formally, removes the element with the lowest index
	 * <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
	 * (if such an element exists).  Returns <tt>true</tt> if this list
	 * contained the specified element (or equivalently, if this list
	 * changed as a result of the call).
	 *
	 * @param o element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(Object o) {
		if (o != null) {
			for (int index = 0; index < size; index++)
				if (o.equals(elementValues[index])) {
					fastRemove(index);
					return true;
				}
		}
		return false;
	}

	/*
	 * Private remove method that skips bounds checking and does not
	 * return the value removed.
	 */
	private void fastRemove(int index) {

		modCount++;
		int idx = getIndexForKey(index);
		if (idx >= 0) {
			deleteAt(idx);

			if ((index + 1) == size) {
				if (idx > 0) {
					int prevIdx = elementKeys[idx - 1];
					size = (prevIdx + 1);
				} else {
					size = 0;
				}
			}
		}
	}

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns.
	 */
	public void clear() {
		modCount++;

		for (int i = 0; i < elementKeys.length; i++) {
			deleteAt(i);
		}

		size = 0;
	}

	private static int absolute(int in)
	{
		if (in < 0) {
			return -(in + 1);
		}
		return in;
	}

	private int getLastIndex()
	{
		for (int i = (elementValues.length - 1); i >= 0; --i) {
			if (elementValues[i] != null) {
				return i;
			}
		}
		return -1;
	}

	private void autoAdjustSize()
	{
		int last = getLastIndex();

		if (last >= 0) {
			size = (elementKeys[last] + 1);
		} else {
			size = 0;
		}
	}

	/**
	 * Removes from this list all of the elements whose index is between
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
	 * Shifts any succeeding elements to the left (reduces their index).
	 * This call shortens the list by {@code (toIndex - fromIndex)} elements.
	 * (If {@code toIndex==fromIndex}, this operation has no effect.)
	 *
	 * @throws IndexOutOfBoundsException if {@code fromIndex} or
	 *         {@code toIndex} is out of range
	 *         ({@code fromIndex < 0 ||
	 *          fromIndex >= size() ||
	 *          toIndex > size() ||
	 *          toIndex < fromIndex})
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex < 0|| fromIndex >= size
				|| toIndex > size || toIndex < fromIndex)
		{
			throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ",toIndex: " + toIndex + ",size: " + size);
		}
		modCount++;
		int fromIdx = absolute(getIndexForKey(fromIndex));
		int toIdx = absolute(getIndexForKey(toIndex));
		for (int i = fromIdx; i < toIdx; ++i) {
			deleteAt(i);
		}
		autoAdjustSize();
	}

	/**
	 * Checks if the given index is in range.  If not, throws an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void rangeCheck(int index) {
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * A version of rangeCheck used by add and addAll.
	 */
	private void rangeCheckForAdd(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message.
	 * Of the many possible refactorings of the error handling code,
	 * this "outlining" performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: "+index+", Size: "+size;
	}

	/**
	 * Save the state of the <tt>ArrayList</tt> instance to a stream (that
	 * is, serialize it).
	 *
	 * @serialData The length of the array backing the <tt>ArrayList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException{
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		// Write out size as capacity for behavioural compatibility with clone()
		s.writeInt(size);
		s.writeInt(allocated);

		// Write out all elements in the proper order.
		for (int i=0; i<elementValues.length; i++) {
			if (elementValues[i] != null) {
				s.writeInt(elementKeys[i]);
				s.writeObject(elementValues[i]);
			}
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		elementKeys = EMPTY_ELEMENTKEYS;
		elementValues = EMPTY_ELEMENTVALUES;

		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in capacity
		s.readInt(); // ignored
		s.readInt(); // ignored

		if (size > 0) {
			ensureCapacityInternal(allocated);

			int[] b = elementKeys;
			Object[] a = elementValues;
			// Read in all elements in the proper order.
			for (int i=0; i<allocated; i++) {
				b[i] = s.readInt();
				a[i] = s.readObject();
			}
		}
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 *
	 * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @see #listIterator(int)
	 */
	public ListIterator<E> listIterator() {
		return new ListItr(0);
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @return an iterator over the elements in this list in proper sequence
	 */
	public Iterator<E> iterator() {
		return new Itr();
	}

	/**
	 * An optimized version of AbstractList.Itr
	 */
	private class Itr implements Iterator<E> {
		int cursor;       // index of next element to return
		int lastRet = -1; // index of last element returned; -1 if no such
		int expectedModCount = modCount;

		Itr() {}

		public boolean hasNext() {
			return cursor != size;
		}

		public E next() {
			checkForComodification();
			int i = cursor;
			if (i >= size)
				throw new NoSuchElementException();
			cursor = i + 1;
			return InfiniteSizeList.this.elementData1(lastRet = i);
		}

		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				InfiniteSizeList.this.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super E> consumer) {
			Objects.requireNonNull(consumer);
			final int size = InfiniteSizeList.this.size;
			int i = cursor;
			if (i >= size) {
				return;
			}
			while (i != size && modCount == expectedModCount) {
				consumer.accept(InfiniteSizeList.this.elementData1(i++));
			}
			// update once at end of iteration to reduce heap write traffic
			cursor = i;
			lastRet = i - 1;
			checkForComodification();
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	/**
	 * An optimized version of AbstractList.ListItr
	 */
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			super();
			cursor = index;
		}

		public boolean hasPrevious() {
			return cursor != 0;
		}

		public int nextIndex() {
			return cursor;
		}

		public int previousIndex() {
			return cursor - 1;
		}

		public E previous() {
			checkForComodification();
			int i = cursor - 1;
			if (i < 0)
				throw new NoSuchElementException();
			cursor = i;
			return InfiniteSizeList.this.elementData1(lastRet = i);
		}

		public void set(E e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				InfiniteSizeList.this.set(lastRet, e);
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		public void add(E e) {
			checkForComodification();

			try {
				int i = cursor;
				InfiniteSizeList.this.add(i, e);
				cursor = i + 1;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		final int expectedModCount = modCount;
		@SuppressWarnings("unchecked")
		final E[] elementData = (E[]) this.elementValues;
		final int size = elementData.length;
		for (int i=0; modCount == expectedModCount && i < size; i++) {
			if (elementData[i] != null) {
				action.accept(elementData[i]);
			}
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this
	 * list.
	 *
	 * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
	 * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
	 * Overriding implementations should document the reporting of additional
	 * characteristic values.
	 *
	 * @return a {@code Spliterator} over the elements in this list
	 * @since 1.8
	 */
	@Override
	public Spliterator<E> spliterator() {
		return new InfiniteSizeListSpliterator<>(this, 0, -1, 0);
	}

	/** Index-based split-by-two, lazily initialized Spliterator */
	static final class InfiniteSizeListSpliterator<E> implements Spliterator<E> {

		/*
		 * If ArrayLists were immutable, or structurally immutable (no
		 * adds, removes, etc), we could implement their spliterators
		 * with Arrays.spliterator. Instead we detect as much
		 * interference during traversal as practical without
		 * sacrificing much performance. We rely primarily on
		 * modCounts. These are not guaranteed to detect concurrency
		 * violations, and are sometimes overly conservative about
		 * within-thread interference, but detect enough problems to
		 * be worthwhile in practice. To carry this out, we (1) lazily
		 * initialize fence and expectedModCount until the latest
		 * point that we need to commit to the state we are checking
		 * against; thus improving precision.  (This doesn't apply to
		 * SubLists, that create spliterators with current non-lazy
		 * values).  (2) We perform only a single
		 * ConcurrentModificationException check at the end of forEach
		 * (the most performance-sensitive method). When using forEach
		 * (as opposed to iterators), we can normally only detect
		 * interference after actions, not before. Further
		 * CME-triggering checks apply to all other possible
		 * violations of assumptions for example null or too-small
		 * elementData array given its size(), that could only have
		 * occurred due to interference.  This allows the inner loop
		 * of forEach to run without any further checks, and
		 * simplifies lambda-resolution. While this does entail a
		 * number of checks, note that in the common case of
		 * list.stream().forEach(a), no checks or other computation
		 * occur anywhere other than inside forEach itself.  The other
		 * less-often-used methods cannot take advantage of most of
		 * these streamlinings.
		 */

		private final InfiniteSizeList<E> list;
		private int index; // current index, modified on advance/split
		private int fence; // -1 until used; then one past last index
		private int expectedModCount; // initialized when fence set

		/** Create new spliterator covering the given  range */
		InfiniteSizeListSpliterator(InfiniteSizeList<E> list, int origin, int fence,
				int expectedModCount) {
			this.list = list; // OK if null unless traversed
			this.index = origin;
			this.fence = fence;
			this.expectedModCount = expectedModCount;
		}

		private int getFence() { // initialize fence to size on first use
			int hi; // (a specialized variant appears in method forEach)
			InfiniteSizeList<E> lst;
			if ((hi = fence) < 0) {
				if ((lst = list) == null)
					hi = fence = 0;
				else {
					expectedModCount = lst.modCount;
					hi = fence = lst.size;
				}
			}
			return hi;
		}

		public InfiniteSizeListSpliterator<E> trySplit() {
			int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
		return (lo >= mid) ? null : // divide range in half unless too small
			new InfiniteSizeListSpliterator<E>(list, lo, index = mid,
			expectedModCount);
		}

		public boolean tryAdvance(Consumer<? super E> action) {
			if (action == null)
				throw new NullPointerException();
			int hi = getFence(), i = index;
			if (i < hi) {
				index = i + 1;
				E e = list.elementData1(i);
				action.accept(e);
				if (list.modCount != expectedModCount)
					throw new ConcurrentModificationException();
				return true;
			}
			return false;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			int i, hi, mc; // hoist accesses and checks from loop
			InfiniteSizeList<E> lst; Object[] a; int[] b;
			if (action == null)
				throw new NullPointerException();
			if ((lst = list) != null && (a = lst.elementValues) != null) {
				b = lst.elementKeys;
				if ((hi = fence) < 0) {
					mc = lst.modCount;
					hi = lst.size;
				}
				else
					mc = expectedModCount;
				if ((i = index) >= 0 && (index = hi) <= a.length) {
					for (; i < hi; ++i) {
						E e = elementData(b, a, i);
						action.accept(e);
					}
					if (lst.modCount == mc)
						return;
				}
			}
			throw new ConcurrentModificationException();
		}

		public long estimateSize() {
			return (long) (getFence() - index);
		}

		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
		}
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		// figure out which elements are to be removed
		// any exception thrown from the filter predicate at this stage
		// will leave the collection unmodified
		int removeCount = 0;
		final BitSet removeSet = new BitSet(size);
		final int expectedModCount = modCount;
		final int size = this.elementValues.length;
		for (int i=0; modCount == expectedModCount && i < size; i++) {
			@SuppressWarnings("unchecked")
			final E element = (E) elementValues[i];
			if (element != null && filter.test(element)) {
				removeSet.set(i);
				removeCount++;
			}
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}

		// shift surviving elements left over the spaces left by removed elements
		final boolean anyToRemove = removeCount > 0;
		if (anyToRemove) {
			for (int i=0; i < size; ++i) {
				i = removeSet.nextSetBit(i);
				elementValues[i] = null;
			}
			this.autoAdjustSize();
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			modCount++;
		}

		return anyToRemove;
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		final int expectedModCount = modCount;
		final int size = this.size;
		for (int i=0; modCount == expectedModCount && i < size; i++) {
			insertAt(i, operator.apply(elementData1(i)));
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
		modCount++;
	}


	//  String conversion

	/**
	 * Returns a string representation of this collection.  The string
	 * representation consists of a list of the collection's elements in the
	 * order they are returned by its iterator, enclosed in square brackets
	 * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
	 * <tt>", "</tt> (comma and space).  Elements are converted to strings as
	 * by {@link String#valueOf(Object)}.
	 *
	 * @return a string representation of this collection
	 */
	public String toString() {
		if (isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < elementValues.length; ++i) {
			int idx = elementKeys[i];
			E e = autoCast(elementValues[i]);
			if (e != null) {
				sb.append(idx).append('=');
				sb.append(e == this ? "(this Collection)" : e);
				if (i < elementValues.length)
					sb.append(',').append(' ');
			}
		}
		return sb.append(']').toString();
	}
}