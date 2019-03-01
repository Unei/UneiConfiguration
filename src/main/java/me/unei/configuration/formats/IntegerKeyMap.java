package me.unei.configuration.formats;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class IntegerKeyMap<V> extends AbstractMap<Integer, V> implements Map<Integer, V>, Cloneable, Serializable
{
	private static final long serialVersionUID = 8881193177423773836L;

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

	/**
	 * The maximum capacity, used if a higher value is implicitly specified
	 * by either of the constructors with arguments.
	 * MUST be a power of two <= 1<<30.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load factor used when none specified in constructor.
	 */
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * Basic hash bin node, used for most entries.  (See below for
	 * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
	 */
	private static class Node<V> implements Map.Entry<Integer, V> {
		final int key;
		V value;
		Node<V> next;

		Node(int key, V value, Node<V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public final Integer getKey()        { return key; }
		public final V getValue()      { return value; }
		public final String toString() { return key + "=" + value; }

		public final int hashCode() {
			return Integer.hashCode(key) ^ Objects.hashCode(value);
		}

		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof Map.Entry) {
				Map.Entry<?,?> e = (Map.Entry<?,?>)o;
				if (getKey().equals(e.getKey()) &&
						Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}
	}

	/**
	 * Returns a power of two size for the given target capacity.
	 */
	private static final int tableSizeFor(int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	/* ---------------- Fields -------------- */

	/**
	 * The table, initialized on first use, and resized as
	 * necessary. When allocated, length is always a power of two.
	 * (We also tolerate length zero in some operations to allow
	 * bootstrapping mechanics that are currently not needed.)
	 */
	private transient Node<V>[] table;

	/**
	 * Holds cached entrySet(). Note that AbstractMap fields are used
	 * for keySet() and values().
	 */
	private transient Set<Map.Entry<Integer, V>> entrySet;


	// Views

	/**
	 * Each of these fields are initialized to contain an instance of the
	 * appropriate view the first time this view is requested.  The views are
	 * stateless, so there's no reason to create more than one of each.
	 *
	 * <p>Since there is no synchronization performed while accessing these fields,
	 * it is expected that java.util.Map view classes using these fields have
	 * no non-final fields (or any fields at all except for outer-this). Adhering
	 * to this rule would make the races on these fields benign.
	 *
	 * <p>It is also imperative that implementations read the field only once,
	 * as in:
	 *
	 * <pre> {@code
	 * public Set<K> keySet() {
	 *   Set<K> ks = keySet;  // single racy read
	 *   if (ks == null) {
	 *     ks = new KeySet();
	 *     keySet = ks;
	 *   }
	 *   return ks;
	 * }
	 *}</pre>
	 */
	private transient Set<Integer> keySet;
	private transient Collection<V> values;

	/**
	 * The number of key-value mappings contained in this map.
	 */
	private transient int size;

	/**
	 * The number of times this HashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMap or otherwise modify its internal structure (e.g.,
	 * rehash).  This field is used to make iterators on Collection-views of
	 * the HashMap fail-fast.  (See ConcurrentModificationException).
	 */
	private transient int modCount;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 *
	 * @serial
	 */
	// (The javadoc description is true upon serialization.
	// Additionally, if the table array has not been allocated, this
	// field holds the initial array capacity, or zero signifying
	// DEFAULT_INITIAL_CAPACITY.)
	private int threshold;

	/**
	 * The load factor for the hash table.
	 *
	 * @serial
	 */
	private final float loadFactor;

	/* ---------------- Public operations -------------- */

	/**
	 * Constructs an empty <tt>IntegerKeyMap</tt> with the specified initial
	 * capacity and load factor.
	 *
	 * @param  initialCapacity the initial capacity
	 * @param  loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative
	 *         or the load factor is nonpositive
	 */
	public IntegerKeyMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: " +
					initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " +
					loadFactor);
		this.loadFactor = loadFactor;
		this.threshold = tableSizeFor(initialCapacity);
	}

	/**
	 * Constructs an empty <tt>IntegerKeyMap</tt> with the specified initial
	 * capacity and the default load factor (0.75).
	 *
	 * @param  initialCapacity the initial capacity.
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public IntegerKeyMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>IntegerKeyMap</tt> with the default initial capacity
	 * (16) and the default load factor (0.75).
	 */
	public IntegerKeyMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
	}

	/**
	 * Constructs a new <tt>IntegerKeyMap</tt> with the same mappings as the
	 * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
	 * default load factor (0.75) and an initial capacity sufficient to
	 * hold the mappings in the specified <tt>Map</tt>.
	 *
	 * @param   m the map whose mappings are to be placed in this map
	 * @throws  NullPointerException if the specified map is null
	 */
	public IntegerKeyMap(Map<? extends Integer, ? extends V> m) {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		putMapEntries(m, false);
	}

	/**
	 * Implements Map.putAll and Map constructor.
	 *
	 * @param m the map
	 * @param evict false when initially constructing this map, else
	 * true (relayed to method afterNodeInsertion).
	 */
	private final void putMapEntries(Map<? extends Integer, ? extends V> m, boolean evict) {
		int s = m.size();
		if (s > 0) {
			if (table == null) {
				float ft = ((float)s / loadFactor) + 1.0F;
				int t = ((ft < (float)MAXIMUM_CAPACITY) ?
						(int)ft : MAXIMUM_CAPACITY);
				if (t > threshold)
					threshold = tableSizeFor(t);
			}
			else if (s > threshold)
				resize();
			for (Map.Entry<? extends Integer, ? extends V> e : m.entrySet()) {
				int key = e.getKey();
				V value = e.getValue();
				putVal(key, value, false, evict);
			}
		}
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 *
	 * <p>More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise
	 * it returns {@code null}.  (There can be at most one such mapping.)
	 *
	 * <p>A return value of {@code null} does not <i>necessarily</i>
	 * indicate that the map contains no mapping for the key; it's also
	 * possible that the map explicitly maps the key to {@code null}.
	 * The {@link #containsKey containsKey} operation may be used to
	 * distinguish these two cases.
	 *
	 * @see #put(Object, Object)
	 */
	public V get(int key) {
		Node<V> e;
		return (e = getNode(key)) == null ? null : e.value;
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 *
	 * <p>More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise
	 * it returns {@code null}.  (There can be at most one such mapping.)
	 *
	 * <p>A return value of {@code null} does not <i>necessarily</i>
	 * indicate that the map contains no mapping for the key; it's also
	 * possible that the map explicitly maps the key to {@code null}.
	 * The {@link #containsKey containsKey} operation may be used to
	 * distinguish these two cases.
	 *
	 * @see #put(Object, Object)
	 * @deprecated Use {@link #get(int)} instead.
	 */
	@Deprecated
	public V get(Object key) {
		Node<V> e;
		return (e = getNode(key)) == null ? null : e.value;
	}

	final Node<V> getNode(Object key) {
		if (key instanceof Number) {
			return getNode(((Number) key).intValue());
		}
		return null;
	}

	/**
	 * Implements Map.get and related methods.
	 *
	 * @param hash hash for key
	 * @param key the key
	 * @return the node, or null if none
	 */
	private final Node<V> getNode(int key) {
		Node<V>[] tab;
		Node<V> first, e;
		int n;
		if ((tab = table) != null
				&& (n = tab.length) > 0
				&& (first = tab[(n - 1) & key]) != null)
		{
			if (first.key == key)
			{
				return first;
			}
			if ((e = first.next) != null)
			{
				do
				{
					if (e.key == key)
						return e;
				} while ((e = e.next) != null);
			}
		}
		return null;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the
	 * specified key.
	 *
	 * @param   key   The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 */
	public boolean containsKey(int key) {
		return getNode(key) != null;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the
	 * specified key.
	 *
	 * @param   key   The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 * @deprecated Use {@link #containsKey(int)} instead.
	 */
	@Deprecated
	public boolean containsKey(Object key) {
		return getNode(key) != null;
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 *         (A <tt>null</tt> return can also indicate that the map
	 *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	public V put(int key, V value) {
		return putVal(key, value, false, true);
	}

	/**
	 * Implements Map.put and related methods.
	 *
	 * @param key the key
	 * @param value the value to put
	 * @param onlyIfAbsent if true, don't change existing value
	 * @param evict if false, the table is in creation mode.
	 * @return previous value, or null if none
	 */
	private final V putVal(int key, V value, boolean onlyIfAbsent, boolean evict) {
		Node<V>[] tab;
		Node<V> p;
		int n, i;
		if ((tab = table) == null || (n = tab.length) == 0)
			n = (tab = resize()).length;
		if ((p = tab[i = (n - 1) & key]) == null)
			tab[i] = newNode(key, value, null);
		else {
			Node<V> e;
			if (p.key == key)
				e = p;
			else {
				while (true) {
					if ((e = p.next) == null) {
						p.next = newNode(key, value, null);
						break;
					}
					if (e.key == key)
						break;
					p = e;
				}
			}
			if (e != null) { // existing mapping for key
				V oldValue = e.value;
				if (!onlyIfAbsent || oldValue == null)
					e.value = value;
				return oldValue;
			}
		}
		++modCount;
		if (++size > threshold)
			resize();
		return null;
	}

	/**
	 * Initializes or doubles table size.  If null, allocates in
	 * accord with initial capacity target held in field threshold.
	 * Otherwise, because we are using power-of-two expansion, the
	 * elements from each bin must either stay at same index, or move
	 * with a power of two offset in the new table.
	 *
	 * @return the table
	 */
	private final Node<V>[] resize() {
		Node<V>[] oldTab = table;
		int oldCap = (oldTab == null) ? 0 : oldTab.length;
		int oldThr = threshold;
		int newCap, newThr = 0;
		if (oldCap > 0) {
			if (oldCap >= MAXIMUM_CAPACITY) {
				threshold = Integer.MAX_VALUE;
				return oldTab;
			}
			else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
					oldCap >= DEFAULT_INITIAL_CAPACITY)
				newThr = oldThr << 1; // double threshold
		}
		else if (oldThr > 0) // initial capacity was placed in threshold
			newCap = oldThr;
		else {               // zero initial threshold signifies using defaults
			newCap = DEFAULT_INITIAL_CAPACITY;
			newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
		}
		if (newThr == 0) {
			float ft = (float)newCap * loadFactor;
			newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
					(int)ft : Integer.MAX_VALUE);
		}
		threshold = newThr;
		@SuppressWarnings({"unchecked"})
		Node<V>[] newTab = (Node<V>[])new Node[newCap];
		table = newTab;
		if (oldTab != null) {
			for (int j = 0; j < oldCap; ++j) {
				Node<V> e;
				if ((e = oldTab[j]) != null) {
					oldTab[j] = null;
					if (e.next == null)
						newTab[e.key & (newCap - 1)] = e;
					else { // preserve order
						Node<V> loHead = null, loTail = null;
						Node<V> hiHead = null, hiTail = null;
						Node<V> next;
						do {
							next = e.next;
							if ((e.key & oldCap) == 0) {
								if (loTail == null)
									loHead = e;
								else
									loTail.next = e;
								loTail = e;
							}
							else {
								if (hiTail == null)
									hiHead = e;
								else
									hiTail.next = e;
								hiTail = e;
							}
						} while ((e = next) != null);
						if (loTail != null) {
							loTail.next = null;
							newTab[j] = loHead;
						}
						if (hiTail != null) {
							hiTail.next = null;
							newTab[j + oldCap] = hiHead;
						}
					}
				}
			}
		}
		return newTab;
	}

	/**
	 * Copies all of the mappings from the specified map to this map.
	 * These mappings will replace any mappings that this map had for
	 * any of the keys currently in the specified map.
	 *
	 * @param m mappings to be stored in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public void putAll(Map<? extends Integer, ? extends V> m) {
		putMapEntries(m, true);
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param  key key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 *         (A <tt>null</tt> return can also indicate that the map
	 *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	public V remove(int key) {
		Node<V> e;
		return (e = removeNode(key, null, false, true)) == null ?
				null : e.value;
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param  key key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 *         (A <tt>null</tt> return can also indicate that the map
	 *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 * @deprecated Use {@link #remove(int)} instead.
	 */
	@Deprecated
	public V remove(Object key) {
		Node<V> e;
		return (e = removeNode(key, null, false, true)) == null ?
				null : e.value;
	}

	/**
	 * Implements Map.remove and related methods.
	 *
	 * @param hash hash for key
	 * @param key the key
	 * @param value the value to match if matchValue, else ignored
	 * @param matchValue if true only remove if value is equal
	 * @param movable if false do not move other nodes while removing
	 * @return the node, or null if none
	 */
	private final Node<V> removeNode(Object key, Object value, boolean matchValue, boolean movable) 
	{
		if (key instanceof Number) {
			return removeNode(((Number) key).intValue(), value, matchValue, movable);
		}
		return null;
	}

	/**
	 * Implements Map.remove and related methods.
	 *
	 * @param hash hash for key
	 * @param key the key
	 * @param value the value to match if matchValue, else ignored
	 * @param matchValue if true only remove if value is equal
	 * @param movable if false do not move other nodes while removing
	 * @return the node, or null if none
	 */
	private final Node<V> removeNode(int key, Object value, boolean matchValue, boolean movable)
	{
		Node<V>[] tab;
		Node<V> p;
		int n, index;
		if ((tab = table) != null
				&& (n = tab.length) > 0
				&& (p = tab[index = (n - 1) & key]) != null)
		{
			Node<V> node = null, e; V v;
			if (p.key == key)
				node = p;
			else if ((e = p.next) != null) {
				do {
					if (e.key == key) {
						node = e;
						break;
					}
					p = e;
				} while ((e = e.next) != null);
			}
			if (node != null && (!matchValue || (v = node.value) == value ||
					(value != null && value.equals(v)))) {
				if (node == p)
					tab[index] = node.next;
				else
					p.next = node.next;
				++modCount;
				--size;
				return node;
			}
		}
		return null;
	}

	/**
	 * Removes all of the mappings from this map.
	 * The map will be empty after this call returns.
	 */
	public void clear() {
		Node<V>[] tab;
		modCount++;
		if ((tab = table) != null && size > 0) {
			size = 0;
			for (int i = 0; i < tab.length; ++i)
				tab[i] = null;
		}
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the
	 * specified value.
	 *
	 * @param value value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value
	 */
	public boolean containsValue(Object value) {
		Node<V>[] tab; V v;
		if ((tab = table) != null && size > 0) {
			for (int i = 0; i < tab.length; ++i) {
				for (Node<V> e = tab[i]; e != null; e = e.next) {
					if ((v = e.value) == value ||
							(value != null && value.equals(v)))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a {@link Set} view of the keys contained in this map.
	 * The set is backed by the map, so changes to the map are
	 * reflected in the set, and vice-versa.  If the map is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation), the results of
	 * the iteration are undefined.  The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	public Set<Integer> keySet() {
		Set<Integer> ks = keySet;
		if (ks == null) {
			ks = new KeySet();
			keySet = ks;
		}
		return ks;
	}

	private final class KeySet extends AbstractSet<Integer> {
		public final int size()                 { return size; }
		public final void clear()               { IntegerKeyMap.this.clear(); }
		public final Iterator<Integer> iterator()     { return new KeyIterator(); }
		public final boolean contains(Object o) { return containsKey(o); }
		public final boolean remove(Object key) {
			return removeNode(key, null, false, true) != null;
		}
		public final Spliterator<Integer> spliterator() {
			return new KeySpliterator<>(IntegerKeyMap.this, 0, -1, 0, 0);
		}
		public final void forEach(Consumer<? super Integer> action) {
			Node<V>[] tab;
			if (action == null)
				throw new NullPointerException();
			if (size > 0 && (tab = table) != null) {
				int mc = modCount;
				for (int i = 0; i < tab.length; ++i) {
					for (Node<V> e = tab[i]; e != null; e = e.next)
						action.accept(e.key);
				}
				if (modCount != mc)
					throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * Returns a {@link Collection} view of the values contained in this map.
	 * The collection is backed by the map, so changes to the map are
	 * reflected in the collection, and vice-versa.  If the map is
	 * modified while an iteration over the collection is in progress
	 * (except through the iterator's own <tt>remove</tt> operation),
	 * the results of the iteration are undefined.  The collection
	 * supports element removal, which removes the corresponding
	 * mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
	 * support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a view of the values contained in this map
	 */
	public Collection<V> values() {
		Collection<V> vs = values;
		if (vs == null) {
			vs = new Values();
			values = vs;
		}
		return vs;
	}

	private final class Values extends AbstractCollection<V> {
		public final int size()                 { return size; }
		public final void clear()               { IntegerKeyMap.this.clear(); }
		public final Iterator<V> iterator()     { return new ValueIterator(); }
		public final boolean contains(Object o) { return containsValue(o); }
		public final Spliterator<V> spliterator() {
			return new ValueSpliterator<>(IntegerKeyMap.this, 0, -1, 0, 0);
		}
		public final void forEach(Consumer<? super V> action) {
			Node<V>[] tab;
			if (action == null)
				throw new NullPointerException();
			if (size > 0 && (tab = table) != null) {
				int mc = modCount;
				for (int i = 0; i < tab.length; ++i) {
					for (Node<V> e = tab[i]; e != null; e = e.next)
						action.accept(e.value);
				}
				if (modCount != mc)
					throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map.
	 * The set is backed by the map, so changes to the map are
	 * reflected in the set, and vice-versa.  If the map is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation, or through the
	 * <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined.  The set
	 * supports element removal, which removes the corresponding
	 * mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
	 * <tt>clear</tt> operations.  It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	public Set<Map.Entry<Integer,V>> entrySet() {
		Set<Map.Entry<Integer,V>> es;
		return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
	}

	private final class EntrySet extends AbstractSet<Map.Entry<Integer,V>> {
		public final int size()                 { return size; }
		public final void clear()               { IntegerKeyMap.this.clear(); }
		public final Iterator<Map.Entry<Integer,V>> iterator() {
			return new EntryIterator();
		}
		public final boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry<?,?> e = (Map.Entry<?,?>) o;
			Object key = e.getKey();
			Node<V> candidate = getNode(key);
			return candidate != null && candidate.equals(e);
		}
		public final boolean remove(Object o) {
			if (o instanceof Map.Entry) {
				Map.Entry<?,?> e = (Map.Entry<?,?>) o;
				Object key = e.getKey();
				Object value = e.getValue();
				return removeNode(key, value, true, true) != null;
			}
			return false;
		}
		public final Spliterator<Map.Entry<Integer,V>> spliterator() {
			return new EntrySpliterator<>(IntegerKeyMap.this, 0, -1, 0, 0);
		}
		public final void forEach(Consumer<? super Map.Entry<Integer,V>> action) {
			Node<V>[] tab;
			if (action == null)
				throw new NullPointerException();
			if (size > 0 && (tab = table) != null) {
				int mc = modCount;
				for (int i = 0; i < tab.length; ++i) {
					for (Node<V> e = tab[i]; e != null; e = e.next)
						action.accept(e);
				}
				if (modCount != mc)
					throw new ConcurrentModificationException();
			}
		}
	}

	// Overrides of JDK8 Map extension methods
	
	public V getOrDefault(int key, V defaultValue) {
		Node<V> e;
		return (e = getNode(key)) == null ? defaultValue : e.value;
	}

	@Override
	@Deprecated
	public V getOrDefault(Object key, V defaultValue) {
		Node<V> e;
		return (e = getNode(key)) == null ? defaultValue : e.value;
	}

	public V putIfAbsent(int key, V value) {
		return putVal(key, value, true, true);
	}

	@Override
	public V putIfAbsent(Integer key, V value) {
		return putVal(key, value, true, true);
	}

	public boolean remove(int key, Object value) {
		return removeNode(key, value, true, true) != null;
	}

	@Override
	@Deprecated
	public boolean remove(Object key, Object value) {
		return removeNode(key, value, true, true) != null;
	}

	public boolean replace(int key, V oldValue, V newValue) {
		Node<V> e; V v;
		if ((e = getNode(key)) != null &&
				((v = e.value) == oldValue || (v != null && v.equals(oldValue)))) {
			e.value = newValue;
			return true;
		}
		return false;
	}

	@Override
	public boolean replace(Integer key, V oldValue, V newValue) {
		return replace(key.intValue(), oldValue, newValue);
	}

	public V replace(int key, V value) {
		Node<V> e;
		if ((e = getNode(key)) != null) {
			V oldValue = e.value;
			e.value = value;
			return oldValue;
		}
		return null;
	}
	
	@Override
	public V replace(Integer key, V value) {
		return replace(key.intValue(), value);
	}

	@Override
	public V computeIfAbsent(Integer p_key, Function<? super Integer, ? extends V> mappingFunction) {
		if (mappingFunction == null)
			throw new NullPointerException();
		Node<V>[] tab; Node<V> first; int n, i;
		int key = p_key.intValue();
		Node<V> old = null;
		if (size > threshold || (tab = table) == null ||
				(n = tab.length) == 0)
			n = (tab = resize()).length;
		if ((first = tab[i = (n - 1) & key]) != null) {
			Node<V> e = first;
			do {
				if (e.key == key) {
					old = e;
					break;
				}
			} while ((e = e.next) != null);
			V oldValue;
			if (old != null && (oldValue = old.value) != null) {
				return oldValue;
			}
		}
		V v = mappingFunction.apply(key);
		if (v == null) {
			return null;
		} else if (old != null) {
			old.value = v;
			return v;
		}
		else {
			tab[i] = newNode(key, v, first);
		}
		++modCount;
		++size;
		return v;
	}

	public V computeIfPresent(Integer p_key,
			BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		if (remappingFunction == null)
			throw new NullPointerException();
		Node<V> e; V oldValue;
		int key = p_key.intValue();
		if ((e = getNode(key)) != null &&
				(oldValue = e.value) != null) {
			V v = remappingFunction.apply(p_key, oldValue);
			if (v != null) {
				e.value = v;
				return v;
			}
			else
				removeNode(key, null, false, true);
		}
		return null;
	}

	@Override
	public V compute(Integer p_key,
			BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		if (remappingFunction == null)
			throw new NullPointerException();
		int key = p_key.intValue();
		Node<V>[] tab; Node<V> first; int n, i;
		Node<V> old = null;
		if (size > threshold || (tab = table) == null ||
				(n = tab.length) == 0)
			n = (tab = resize()).length;
		if ((first = tab[i = (n - 1) & key]) != null) {
			Node<V> e = first;
			do {
				if (e.key == key) {
					old = e;
					break;
				}
			} while ((e = e.next) != null);
		}
		V oldValue = (old == null) ? null : old.value;
		V v = remappingFunction.apply(p_key, oldValue);
		if (old != null) {
			if (v != null) {
				old.value = v;
			}
			else
				removeNode(key, null, false, true);
		}
		else if (v != null) {
			tab[i] = newNode(key, v, first);
			++modCount;
			++size;
		}
		return v;
	}

	@Override
	public V merge(Integer p_key, V value,
			BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		if (value == null)
			throw new NullPointerException();
		if (remappingFunction == null)
			throw new NullPointerException();
		int key = p_key.intValue();
		Node<V>[] tab; Node<V> first; int n, i;
		Node<V> old = null;
		if (size > threshold || (tab = table) == null ||
				(n = tab.length) == 0)
			n = (tab = resize()).length;
		if ((first = tab[i = (n - 1) & key]) != null) {
			Node<V> e = first;
			do {
				if (e.key == key) {
					old = e;
					break;
				}
			} while ((e = e.next) != null);
		}
		if (old != null) {
			V v;
			if (old.value != null)
				v = remappingFunction.apply(old.value, value);
			else
				v = value;
			if (v != null) {
				old.value = v;
			}
			else
				removeNode(key, null, false, true);
			return v;
		}
		if (value != null) {
			tab[i] = newNode(key, value, first);
			++modCount;
			++size;
		}
		return value;
	}

	@Override
	public void forEach(BiConsumer<? super Integer, ? super V> action) {
		Node<V>[] tab;
		if (action == null)
			throw new NullPointerException();
		if (size > 0 && (tab = table) != null) {
			int mc = modCount;
			for (int i = 0; i < tab.length; ++i) {
				for (Node<V> e = tab[i]; e != null; e = e.next)
					action.accept(e.key, e.value);
			}
			if (modCount != mc)
				throw new ConcurrentModificationException();
		}
	}

	@Override
	public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> function) {
		Node<V>[] tab;
		if (function == null)
			throw new NullPointerException();
		if (size > 0 && (tab = table) != null) {
			int mc = modCount;
			for (int i = 0; i < tab.length; ++i) {
				for (Node<V> e = tab[i]; e != null; e = e.next) {
					e.value = function.apply(e.key, e.value);
				}
			}
			if (modCount != mc)
				throw new ConcurrentModificationException();
		}
	}

	/* ------------------------------------------------------------ */
	// Cloning and serialization

	/**
	 * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
	 * values themselves are not cloned.
	 *
	 * @return a shallow copy of this map
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		IntegerKeyMap<V> result;
		try {
			result = (IntegerKeyMap<V>)super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError(e);
		}
		result.reinitialize();
		result.putMapEntries(this, false);
		return result;
	}

	public final float loadFactor() { return loadFactor; }
	public final int capacity() {
		return (table != null) ? table.length :
			(threshold > 0) ? threshold :
				DEFAULT_INITIAL_CAPACITY;
	}

	/**
	 * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
	 * serialize it).
	 *
	 * @serialData The <i>capacity</i> of the HashMap (the length of the
	 *             bucket array) is emitted (int), followed by the
	 *             <i>size</i> (an int, the number of key-value
	 *             mappings), followed by the key (Object) and value (Object)
	 *             for each key-value mapping.  The key-value mappings are
	 *             emitted in no particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws IOException {
		int buckets = capacity();
		// Write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();
		s.writeInt(buckets);
		s.writeInt(size);
		internalWriteEntries(s);
	}

	/**
	 * Reconstitutes this map from a stream (that is, deserializes it).
	 * @param s the stream
	 * @throws ClassNotFoundException if the class of a serialized object
	 *         could not be found
	 * @throws IOException if an I/O error occurs
	 */
	@SuppressWarnings("restriction")
	private void readObject(java.io.ObjectInputStream s)
			throws IOException, ClassNotFoundException {
		// Read in the threshold (ignored), loadfactor, and any hidden stuff
		s.defaultReadObject();
		reinitialize();
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new InvalidObjectException("Illegal load factor: " +
					loadFactor);
		s.readInt();                // Read and ignore number of buckets
		int mappings = s.readInt(); // Read number of mappings (size)
		if (mappings < 0)
			throw new InvalidObjectException("Illegal mappings count: " +
					mappings);
		else if (mappings > 0) { // (if zero, use defaults)
			// Size the table using given load factor only if within
			// range of 0.25...4.0
			float lf = Math.min(Math.max(0.25f, loadFactor), 4.0f);
			float fc = (float)mappings / lf + 1.0f;
			int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ?
					DEFAULT_INITIAL_CAPACITY :
						(fc >= MAXIMUM_CAPACITY) ?
								MAXIMUM_CAPACITY :
									tableSizeFor((int)fc));
			float ft = (float)cap * lf;
			threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ?
					(int)ft : Integer.MAX_VALUE);

			// Check Map.Entry[].class since it's the nearest public type to
			// what we're actually creating.
			sun.misc.SharedSecrets.getJavaOISAccess().checkArray(s, Map.Entry[].class, cap);
			@SuppressWarnings({"unchecked"})
			Node<V>[] tab = (Node<V>[])new Node[cap];
			table = tab;

			// Read the keys and values, and put the mappings in the HashMap
			for (int i = 0; i < mappings; i++) {
				int key = s.readInt();
				@SuppressWarnings("unchecked")
				V value = (V) s.readObject();
				putVal(key, value, false, false);
			}
		}
	}

	/* ------------------------------------------------------------ */
	// iterators

	private abstract class IntegerIterator {
		Node<V> next;        // next entry to return
		Node<V> current;     // current entry
		int expectedModCount;  // for fast-fail
		int index;             // current slot

		IntegerIterator() {
			expectedModCount = modCount;
			Node<V>[] t = table;
			current = next = null;
			index = 0;
			if (t != null && size > 0) { // advance to first entry
				do {} while (index < t.length && (next = t[index++]) == null);
			}
		}

		public final boolean hasNext() {
			return next != null;
		}

		final Node<V> nextNode() {
			Node<V>[] t;
			Node<V> e = next;
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (e == null)
				throw new NoSuchElementException();
			if ((next = (current = e).next) == null && (t = table) != null) {
				do {} while (index < t.length && (next = t[index++]) == null);
			}
			return e;
		}

		public final void remove() {
			Node<V> p = current;
			if (p == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			current = null;
			int key = p.key;
			removeNode(key, null, false, false);
			expectedModCount = modCount;
		}
	}

	private final class KeyIterator extends IntegerIterator implements Iterator<Integer> {
		public final Integer next() { return nextNode().key; }
	}

	private final class ValueIterator extends IntegerIterator implements Iterator<V> {
		public final V next() { return nextNode().value; }
	}

	private final class EntryIterator extends IntegerIterator implements Iterator<Map.Entry<Integer,V>> {
		public final Map.Entry<Integer,V> next() { return nextNode(); }
	}

	/* ------------------------------------------------------------ */
	// spliterators

	private static class IntegerKeyMapSpliterator<V> {
		final IntegerKeyMap<V> map;
		Node<V> current;          // current node
		int index;                  // current index, modified on advance/split
		int fence;                  // one past last index
		int est;                    // size estimate
		int expectedModCount;       // for comodification checks

		public IntegerKeyMapSpliterator(IntegerKeyMap<V> m, int origin,
				int fence, int est,
				int expectedModCount) {
			this.map = m;
			this.index = origin;
			this.fence = fence;
			this.est = est;
			this.expectedModCount = expectedModCount;
		}

		public final int getFence() { // initialize fence and size on first use
			int hi;
			if ((hi = fence) < 0) {
				IntegerKeyMap<V> m = map;
				est = m.size;
				expectedModCount = m.modCount;
				Node<V>[] tab = m.table;
				hi = fence = (tab == null) ? 0 : tab.length;
			}
			return hi;
		}

		public final long estimateSize() {
			getFence(); // force init
			return (long) est;
		}
	}

	private static final class KeySpliterator<V> extends IntegerKeyMapSpliterator<V> implements Spliterator<Integer> {
		public KeySpliterator(IntegerKeyMap<V> m, int origin, int fence, int est,
				int expectedModCount) {
			super(m, origin, fence, est, expectedModCount);
		}

		public KeySpliterator<V> trySplit() {
			int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
				return (lo >= mid || current != null) ? null :
					new KeySpliterator<>(map, lo, index = mid, est >>>= 1,
					expectedModCount);
		}

		public void forEachRemaining(Consumer<? super Integer> action) {
			int i, hi, mc;
			if (action == null)
				throw new NullPointerException();
			IntegerKeyMap<V> m = map;
			Node<V>[] tab = m.table;
			if ((hi = fence) < 0) {
				mc = expectedModCount = m.modCount;
				hi = fence = (tab == null) ? 0 : tab.length;
			}
			else
				mc = expectedModCount;
			if (tab != null && tab.length >= hi &&
					(i = index) >= 0 && (i < (index = hi) || current != null)) {
				Node<V> p = current;
				current = null;
				do {
					if (p == null)
						p = tab[i++];
					else {
						action.accept(p.key);
						p = p.next;
					}
				} while (p != null || i < hi);
				if (m.modCount != mc)
					throw new ConcurrentModificationException();
			}
		}

		public boolean tryAdvance(Consumer<? super Integer> action) {
			int hi;
			if (action == null)
				throw new NullPointerException();
			Node<V>[] tab = map.table;
			if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
				while (current != null || index < hi) {
					if (current == null)
						current = tab[index++];
					else {
						int k = current.key;
						current = current.next;
						action.accept(Integer.valueOf(k));
						if (map.modCount != expectedModCount)
							throw new ConcurrentModificationException();
						return true;
					}
				}
			}
			return false;
		}

		public int characteristics() {
			return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
					Spliterator.DISTINCT;
		}
	}

	private static final class ValueSpliterator<V> extends IntegerKeyMapSpliterator<V> implements Spliterator<V> {
		public ValueSpliterator(IntegerKeyMap<V> m, int origin, int fence, int est,
				int expectedModCount) {
			super(m, origin, fence, est, expectedModCount);
		}

		public ValueSpliterator<V> trySplit() {
			int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
				return (lo >= mid || current != null) ? null :
					new ValueSpliterator<>(map, lo, index = mid, est >>>= 1,
					expectedModCount);
		}

		public void forEachRemaining(Consumer<? super V> action) {
			int i, hi, mc;
			if (action == null)
				throw new NullPointerException();
			IntegerKeyMap<V> m = map;
			Node<V>[] tab = m.table;
			if ((hi = fence) < 0) {
				mc = expectedModCount = m.modCount;
				hi = fence = (tab == null) ? 0 : tab.length;
			}
			else
				mc = expectedModCount;
			if (tab != null && tab.length >= hi &&
					(i = index) >= 0 && (i < (index = hi) || current != null)) {
				Node<V> p = current;
				current = null;
				do {
					if (p == null)
						p = tab[i++];
					else {
						action.accept(p.value);
						p = p.next;
					}
				} while (p != null || i < hi);
				if (m.modCount != mc)
					throw new ConcurrentModificationException();
			}
		}

		public boolean tryAdvance(Consumer<? super V> action) {
			int hi;
			if (action == null)
				throw new NullPointerException();
			Node<V>[] tab = map.table;
			if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
				while (current != null || index < hi) {
					if (current == null)
						current = tab[index++];
					else {
						V v = current.value;
						current = current.next;
						action.accept(v);
						if (map.modCount != expectedModCount)
							throw new ConcurrentModificationException();
						return true;
					}
				}
			}
			return false;
		}

		public int characteristics() {
			return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
		}
	}

	private static final class EntrySpliterator<V> extends IntegerKeyMapSpliterator<V> implements Spliterator<Map.Entry<Integer, V>> {
		public EntrySpliterator(IntegerKeyMap<V> m, int origin, int fence, int est,
				int expectedModCount) {
			super(m, origin, fence, est, expectedModCount);
		}

		public EntrySpliterator<V> trySplit() {
			int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
				return (lo >= mid || current != null) ? null :
					new EntrySpliterator<>(map, lo, index = mid, est >>>= 1,
					expectedModCount);
		}

		public void forEachRemaining(Consumer<? super Map.Entry<Integer,V>> action) {
			int i, hi, mc;
			if (action == null)
				throw new NullPointerException();
			IntegerKeyMap<V> m = map;
			Node<V>[] tab = m.table;
			if ((hi = fence) < 0) {
				mc = expectedModCount = m.modCount;
				hi = fence = (tab == null) ? 0 : tab.length;
			}
			else
				mc = expectedModCount;
			if (tab != null && tab.length >= hi &&
					(i = index) >= 0 && (i < (index = hi) || current != null)) {
				Node<V> p = current;
				current = null;
				do {
					if (p == null)
						p = tab[i++];
					else {
						action.accept(p);
						p = p.next;
					}
				} while (p != null || i < hi);
				if (m.modCount != mc)
					throw new ConcurrentModificationException();
			}
		}

		public boolean tryAdvance(Consumer<? super Map.Entry<Integer,V>> action) {
			int hi;
			if (action == null)
				throw new NullPointerException();
			Node<V>[] tab = map.table;
			if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
				while (current != null || index < hi) {
					if (current == null)
						current = tab[index++];
					else {
						Node<V> e = current;
						current = current.next;
						action.accept(e);
						if (map.modCount != expectedModCount)
							throw new ConcurrentModificationException();
						return true;
					}
				}
			}
			return false;
		}

		public int characteristics() {
			return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
					Spliterator.DISTINCT;
		}
	}

	// Create a regular node
	private Node<V> newNode(int key, V value, Node<V> next) {
		return new Node<>(key, value, next);
	}


	/**
	 * Reset to initial default state.  Called by clone and readObject.
	 */
	void reinitialize() {
		table = null;
		entrySet = null;
		keySet = null;
		values = null;
		modCount = 0;
		threshold = 0;
		size = 0;
	}

	// Called only from writeObject, to ensure compatible ordering.
	void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
		Node<V>[] tab;
		if (size > 0 && (tab = table) != null) {
			for (int i = 0; i < tab.length; ++i) {
				for (Node<V> e = tab[i]; e != null; e = e.next) {
					s.writeInt(e.key);
					s.writeObject(e.value);
				}
			}
		}
	}
}