package me.unei.configuration.api.fs;

import java.util.ArrayList;
import java.util.ListIterator;

import me.unei.configuration.api.exceptions.NoExcept;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.formats.Storage.Key;

/**
 * Represent a component of an abstract path.
 * 
 * <p>could be Root, Parent, Child (named) or Index (indexed).</p>
 */
public final class PathComponent implements IPathComponent {
	
	private final PathComponentType type;
	private final String value;
	private final int index;
	
	/**
	 * Create a new component of the given type with a name.
	 * 
	 * @param type The type of the component.
	 * @param value The name of the component.
	 */
	@NoExcept
	public PathComponent(PathComponentType type, String value) {
		this.type = type;
		this.value = value;
		int idx;
		try {
			idx = Integer.valueOf(value);
		} catch (NumberFormatException ignored) {
			idx = -1;
		}
		this.index = idx;
	}
	
	/**
	 * Create a new component of the given type with a name.
	 * 
	 * @param type The type of the component.
	 * @param index The index of the component.
	 */
	@NoExcept
	public PathComponent(PathComponentType type, int index) {
		this.type = type;
		this.value = Integer.toString(index);
		this.index = index;
	}

	public PathComponentType getType() {
		return this.type;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public Key getKey(StorageType indice) {
		if (getType() == PathComponentType.INDEX) {
			if (indice == StorageType.MAP) {
				return new Key(getValue());
			}
			return new Key(getIndex());
		}
		if (getType() == PathComponentType.CHILD) {
			if (indice == StorageType.LIST) {
				return new Key(getIndex());
			}
			return new Key(getValue());
		}
		return null;
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
	public static class PathComponentsList extends ArrayList<IPathComponent> implements IPathComponentsList {
		
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
		
		@Override
		public boolean add(IPathComponent element) {
			if (element == null) {
				throw new NullPointerException("element must not be null.");
			}
			return super.add((PathComponent) element);
		}
		
		public PathSymbolsType getSymbolsType() {
			return this.symType;
		}
		
		public boolean appendComponent(PathComponentType type, String value) {
			return this.add(new PathComponent(type, value));
		}
		
		public boolean appendChild(String name) {
			return this.appendComponent(PathComponentType.CHILD, name);
		}
		
		public boolean appendIndex(int index) {
			return this.appendComponent(PathComponentType.INDEX, Integer.toString(index));
		}
		
		public boolean appendRoot() {
			return this.appendComponent(PathComponentType.ROOT, String.valueOf(symType.root));
		}

		public boolean appendParent() {
			return this.appendComponent(PathComponentType.PARENT, symType.parent);
		}
		
		public IPathComponent last() {
			if (this.isEmpty()) {
				return null;
			}
			return this.get(this.size() - 1);
		}
		
		public String lastChild() {
			IPathComponent last = this.last();
			if (last != null) {
				return last.getValue();
			}
			return null;
		}
		
		public int lastIndex() {
			IPathComponent last = this.last();
			if (last != null) {
				return last.getIndex();
			}
			return -1;
		}
		
		public IPathComponent removeLast() {
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
			ListIterator<IPathComponent> it = this.listIterator();
			int lr = this.lastIndexOf(new PathComponent(PathComponentType.ROOT, ""));
			for (int i = 0; i < lr && it.hasNext(); i++) {
				it.next();
				it.remove();
			}
			it = this.listIterator();
			while (it.hasNext()) {
				IPathComponent curr = it.next();
				if (curr.getType().equals(PathComponentType.PARENT)) {
					if (it.hasPrevious()) {
						IPathComponent prev = it.previous();
						if (prev.getType().equals(PathComponentType.CHILD)) {
							it.remove();
							it.next();
							it.remove();
						}
					}
				}
			}
		}
		
		@Override
		public String toString() {
			StringBuilder pathBuilder = new StringBuilder();
			PathComponentType lastType = null;
			for (IPathComponent component : this) {
				
				if (lastType != null && lastType != PathComponentType.ROOT) {
					if (lastType == PathComponentType.PARENT || component.getType() == PathComponentType.PARENT) {
						if (this.symType.wrapParent) {
							pathBuilder.append(this.symType.separator);
						}
					} else if (component.getType().valuable) {
						pathBuilder.append(this.symType.separator);
					}
				}
				
				if (component.getType() == PathComponentType.INDEX) {
					pathBuilder.append(this.symType.indexerPrefix);
				}
				
				if (component.getType().valuable) {
					pathBuilder.append(PathComponent.escapeComponent(component.getValue(), this.symType));
				} else {
					pathBuilder.append(component.getValue());
				}
				
				if (component.getType() == PathComponentType.INDEX) {
					pathBuilder.append(this.symType.indexerSuffix);
				}
				lastType = component.getType();
			}
			return pathBuilder.toString();
		}
		
		@Override
		public PathComponentsList clone() {
			PathComponentsList copy = (PathComponentsList) super.clone();
			copy.symType = this.symType;
			return copy;
		}
	}
}