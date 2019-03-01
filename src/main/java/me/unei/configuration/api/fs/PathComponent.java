package me.unei.configuration.api.fs;

import java.util.ArrayList;
import java.util.ListIterator;

import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;

/**
 * Represent a component of an abstract path.
 * 
 * <p>could be Root, Parent, Child (named) or Index (indexed).</p>
 */
public final class PathComponent {
	
	private final PathComponentType type;
	private final String value;
	private final int index;
	
	/**
	 * Create a new component of the given type with a name.
	 * 
	 * @param type The type of the component.
	 * @param value The name of the component.
	 */
	public PathComponent(PathComponentType type, String value) {
		this.type = type;
		this.value = value;
		if (type == PathComponentType.INDEX) {
			this.index = Integer.valueOf(value);
		} else {
			this.index = -1;
		}
	}
	
	/**
	 * Create a new component of the given type with a name.
	 * 
	 * @param type The type of the component.
	 * @param index The index of the component.
	 */
	public PathComponent(PathComponentType type, int index) {
		this.type = type;
		this.value = Integer.toString(index);
		this.index = index;
	}
	
	/**
	 * Gets the component type.
	 * 
	 * @return Returns the type of this component.
	 */
	public PathComponentType getType() {
		return this.type;
	}
	
	/**
	 * Gets the component name.
	 * 
	 * @return Returns the component name.
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Gets the component index (if type of {@link PathComponentType#INDEX}).
	 * 
	 * @return Returns the component index.
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Escape a string path using the given symbols type.
	 * 
	 * @param component The text to escape.
	 * @param symType The type of the symbols used in the path.
	 * @return Returns the escaped text.
	 */
	public static String escapeComponent(String component, PathSymbolsType symType) {
		if (component == null || component.isEmpty()) {
			return component;
		}
		
		boolean absolute = component.startsWith(String.valueOf(symType.root));
		if (absolute) {
			component = component.substring(1);
		}
		
		component = component.replace(String.valueOf(symType.escape),
				String.valueOf(new char[] { symType.escape, symType.escape }));
		component = component.replace(String.valueOf(symType.separator),
				String.valueOf(new char[] { symType.escape, symType.separator }));
		component = component.replace(String.valueOf(symType.indexerPrefix),
				String.valueOf(new char[] { symType.escape, symType.indexerPrefix }));
		component = component.replace(String.valueOf(symType.indexerSuffix),
				String.valueOf(new char[] { symType.escape, symType.indexerSuffix }));
		
		if (absolute) {
			component = symType.escape + symType.root + component;
		}
		return component;
	}
	
	/**
	 * Indicates whether some other object is "equal to" this path component.
	 * 
	 * <p>This component's name is checked only for {@linkplain PathComponentType#CHILD child} type.</p>
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PathComponent)) {
			return false;
		}
		PathComponent pc = (PathComponent) other;
		if (pc.getType().equals(this.getType())) {
			if (this.getType().equals(PathComponentType.CHILD)) {
				return this.getValue().contentEquals(pc.getValue());
			}
			if (this.getType().equals(PathComponentType.INDEX)) {
				return this.getIndex() == pc.getIndex();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Array of {@link PathComponent} used to construct a full path.
	 */
	public static class PathComponentsList extends ArrayList<PathComponent> {
		
		private static final long serialVersionUID = 7055238860386957873L;
		
		private PathSymbolsType symType;
		
		/**
		 * Constructs an empty path with an initial capacity of ten components.
		 * 
		 * @param type The type of the symbols used in string paths.
		 */
		public PathComponentsList(PathSymbolsType type) {
			super();
			this.symType = type;
		}
		
		/**
		 * Constructs a list containing the elements of the specified path,
		 * in the order they are returned by the collection's iterator.
		 * 
		 * @param orig The collection whose elements are to be placed into this list.
		 * 
		 * @throws NullPointerException If the specified collection is null.
		 */
		public PathComponentsList(PathComponentsList orig) {
			super(orig);
			this.symType = orig.symType;
		}
		
		/**
		 * Appends the path component at the end of the existing path.
		 * 
		 * @throws NullPointerException If element is null.
		 */
		@Override
		public boolean add(PathComponent element) {
			if (element == null) {
				throw new NullPointerException("element must not be null.");
			}
			return super.add(element);
		}
		
		/**
		 * Gets the type of the symbols used in string paths.
		 * 
		 * @return Returns the string path symbols type.
		 */
		public PathSymbolsType getSymbolsType() {
			return this.symType;
		}
		
		/**
		 * Appends a component of the specified type and with the given name.
		 * 
		 * @see PathComponent#PathComponent(PathComponentType, String)
		 * 
		 * @param type The type of the component.
		 * @param value The name of the component.
		 * @return Returns `true`.
		 */
		public boolean appendComponent(PathComponentType type, String value) {
			return this.add(new PathComponent(type, value));
		}
		
		/**
		 * Appends a child component to this path.
		 * 
		 * @param name The child name.
		 * @return Returns `true`.
		 */
		public boolean appendChild(String name) {
			return this.appendComponent(PathComponentType.CHILD, name);
		}
		
		/**
		 * Appends a table element component to this path.
		 * 
		 * @param index The element index.
		 * @return Returns `true`.
		 */
		public boolean appendIndex(int index) {
			return this.appendComponent(PathComponentType.INDEX, Integer.toString(index));
		}
		
		/**
		 * Appends a root component to this path.
		 * 
		 * @return Returns `true`.
		 */
		public boolean appendRoot() {
			return this.appendComponent(PathComponentType.ROOT, String.valueOf(symType.root));
		}

		/**
		 * Appends a parent component to this path.
		 * 
		 * @return Returns `true`.
		 */
		public boolean appendParent() {
			return this.appendComponent(PathComponentType.PARENT, symType.parent);
		}
		
		/**
		 * Gets the last component of the path.
		 * 
		 * @return Returns the last component.
		 */
		public PathComponent last() {
			if (this.isEmpty()) {
				return null;
			}
			return this.get(this.size() - 1);
		}
		
		/**
		 * Gets the last component of the path, if it is a 'child' one.
		 * 
		 * @return Returns the last component or null.
		 */
		public String lastChild() {
			PathComponent last = this.last();
			if (last != null) {
				return last.getValue();
			}
			return null;
		}
		
		/**
		 * Gets the last component of the path, if it is an 'index' one.
		 * 
		 * @return Returns the last component or -1.
		 */
		public int lastIndex() {
			PathComponent last = this.last();
			if (last != null) {
				return last.getIndex();
			}
			return -1;
		}
		
		/**
		 * Removes the last component of the path.
		 * 
		 * @return Returns the removed component.
		 */
		public PathComponent removeLast() {
			if (this.isEmpty()) {
				return null;
			}
			return this.remove(this.size() - 1);
		}
		
		/**
		 * Clean the path components. 
		 *
		 * <p>Removes the 'parent' and 'child' components where unneeded.</p>
		 *
		 * @deprecated use
		 *             {@link PathNavigator#cleanPath(PathComponent.PathComponentsList)}
		 *             instead
		 */
		@Deprecated
		public void cleanPath() {
			ListIterator<PathComponent> it = this.listIterator();
			int lr = this.lastIndexOf(new PathComponent(PathComponentType.ROOT, ""));
			for (int i = 0; i < lr && it.hasNext(); i++) {
				it.next();
				it.remove();
			}
			it = this.listIterator();
			while (it.hasNext()) {
				PathComponent curr = it.next();
				if (curr.getType().equals(PathComponentType.PARENT)) {
					if (it.hasPrevious()) {
						PathComponent prev = it.previous();
						if (prev.getType().equals(PathComponentType.CHILD)) {
							it.remove();
							it.next();
							it.remove();
						}
					}
				}
			}
		}
		
		/**
		 * Returns the string representation of this path.
		 */
		@Override
		public String toString() { // FIXME: Check if the path separator is added correctly.
			StringBuilder pathBuilder = new StringBuilder();
			for (PathComponent component : this) {
				if (component.type == PathComponentType.INDEX) {
					pathBuilder.append(this.symType.indexerPrefix);
				}
				pathBuilder.append(PathComponent.escapeComponent(component.getValue(), this.symType));
				if (component.type == PathComponentType.INDEX) {
					pathBuilder.append(this.symType.indexerSuffix);
				}
			}
			return pathBuilder.toString();
		}
		
		/**
		 * Clone this path components to another list.
		 */
		@Override
		public PathComponentsList clone() {
			PathComponentsList copy = (PathComponentsList) super.clone();
			copy.symType = this.symType;
			return copy;
		}
	}
	
	/**
	 * Types of path component.
	 */
	public static enum PathComponentType {
		/**
		 * A root component (the first '/' with Unix).
		 */
		ROOT,
		/**
		 * A parent component ('..' with Unix).
		 */
		PARENT,
		/**
		 * A child component ('/name/' with Unix).
		 * 
		 * <p>A child is always accompanied with a name.</p>
		 */
		CHILD,
		/**
		 * A table element component ('[index]').
		 * 
		 * <p>An index is always accompanied with an integer.</p>
		 */
		INDEX;
	}
}