package me.unei.configuration.api.fs;

import java.util.function.Consumer;

import me.unei.configuration.api.fs.PathComponent.PathComponentType;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;

/**
 * Section navigator.
 *
 * @param <T> The section type.
 */
public final class PathNavigator<T extends NavigableFile> {
	
	private PathComponentsList currentPath;
	private T currentNode;
	
	/**
	 * Create a new navigator for the given section.
	 * 
	 * @param rootFile The section where we start.
	 */
	public PathNavigator(T rootFile) {
		this.currentNode = rootFile;
		this.currentPath = rootFile.getRealListPath();
		// this.currentPath = PathNavigator.parsePath(rootFile.getCurrentPath(),
		// PathSymbolsType.BUKKIT);
	}
	
	/**
	 * Create a new navigator for the given section.
	 * 
	 * @param rootFile The section where we start.
	 * @param type The type of the symbols in paths.
	 * 
	 * @deprecated The {@linkplain PathSymbolsType type of the symbols} are given as parameter every times.
	 */
	@Deprecated
	public PathNavigator(T rootFile, PathSymbolsType type) {
		this.currentNode = rootFile;
		this.currentPath = PathNavigator.parsePath(rootFile.getCurrentPath(), type);
	}
	
	@SuppressWarnings("unchecked")
	private T getChecked(NavigableFile file) {
		try {
			return (T) file;
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * Navigate to the root section.
	 */
	public void goToRoot() {
		this.currentPath.clear();
		this.currentNode = getChecked(this.currentNode.getRoot());
	}
	
	/**
	 * Navigate to the parent section.
	 */
	public void goToParent() {
		if (!this.currentPath.isEmpty()) {
			this.currentPath.remove(this.currentPath.size() - 1);
			this.currentNode = getChecked(this.currentNode.getParent());
		}
	}
	
	/**
	 * Navigate to the `name` child section.
	 * 
	 * @param name The name of the child section to go to.
	 */
	public void goToChild(String name) {
		if (name == null || name.isEmpty()) {
			return;
		}
		this.currentPath.appendChild(name);
		this.currentNode = getChecked(this.currentNode.getChild(name));
	}
	
	/**
	 * Navigate to the `index` element section.
	 * 
	 * @param index The index of the child section to go to.
	 */
	public void goToIndex(int index) {
		if (index < 0) {
			return;
		}
		this.currentPath.appendIndex(index);
		this.currentNode = getChecked(this.currentNode.getChild(Integer.toString(index)));
	}
	
	/**
	 * Gets the current path.
	 * 
	 * @return Returns the path as a string.
	 */
	public String getCurrentPath() {
		return currentPath.toString();
	}
	
	/**
	 * Gets the current section.
	 * 
	 * @return Returns the actual node.
	 */
	public T getCurrentNode() {
		return this.currentNode;
	}
	
	/**
	 * Navigate following the given path and execute an action on each nodes.
	 * 
	 * @param path The path of the destination.
	 * @param action An action to execute on each node of the path.
	 * 
	 * @throws NullPointerException If the `action` parameter is null.
	 */
	public void followAndApply(PathComponentsList path, Consumer<T> action) {
		if (action == null) {
			throw new NullPointerException("action must not be null.");
		}
		
		action.accept(this.currentNode);
		
		for (PathComponent component : path) {
			switch (component.getType()) {
				case ROOT:
					this.goToRoot();
					break;
				
				case PARENT:
					this.goToParent();
					break;
				
				case CHILD:
					this.goToChild(component.getValue());
					break;
					
				case INDEX:
					this.goToIndex(component.getIndex());
					break;
			}
			action.accept(this.currentNode);
		}
	}
	
	/**
	 * Parse a string, natural, path into multiples, easy to use, components.
	 * 
	 * @param path The path to parse.
	 * @param type The types of the symbols used in the path.
	 * @return Returns the parsed path.
	 */
	public static PathComponentsList parsePath(String path, PathSymbolsType type) {
		if (path == null || path.isEmpty()) {
			return new PathComponentsList(type);
		}
		
		PathComponentsList components = new PathComponentsList(type);
		StringBuilder lastComponent = new StringBuilder();
		
		int i = 0;
		
		if (PathNavigator.isAbsolute(path, type)) {
			components.appendRoot();
			i = 1;
		}
		
		boolean escaped = false;
		boolean index = false;
		for (; i < path.length(); i++) {
			char c = path.charAt(i);
			
			if (escaped) {
				lastComponent.append(c);
				escaped = false;
				continue;
			}
			
			if (c == type.escape) {
				escaped = true;
			} else if (PathNavigator.hasParentChar(path, i, type)) {
				if (lastComponent.length() > 0) {
					components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
					lastComponent.setLength(0);
				}
				components.appendParent();
				i++;
			} else if (PathNavigator.hasSeperatorChar(path, i, type)) {
				if (lastComponent.length() > 0) {
					components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
					lastComponent.setLength(0);
				}
			} else if (PathNavigator.hasIndexerChar(path, i, type)) {
				if (lastComponent.length() > 0) {
					components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
					lastComponent.setLength(0);
				}
				index = true;
			} else if (index && PathNavigator.hasIndexerEndChar(path, i, type)) {
				if (lastComponent.length() > 0) {
					components.appendComponent(PathComponentType.INDEX, lastComponent.toString());
					lastComponent.setLength(0);
				}
				index = false;
			} else {
				lastComponent.append(c);
			}
		}
		
		if (lastComponent.length() > 0) {
			components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
		}
		
		return components;
	}
	
	/**
	 * Clean the path components.
	 * 
	 * <p>Removes the 'parent' and 'child' components where unneeded.</p>
	 * 
	 * @param path The path to clean.
	 * @return Returns the new {@link PathComponentsList} after the cleaning.
	 */
	public static PathComponentsList cleanPath(PathComponentsList path) {
		PathComponentsList cleanPath = new PathComponentsList(path.getSymbolsType());
		for (PathComponent component : path) {
			switch (component.getType()) {
				case ROOT:
					cleanPath.clear();
					cleanPath.add(component);
					break;
				
				case PARENT:
					if (!cleanPath.isEmpty()
							&& (cleanPath.get(cleanPath.size() - 1).getType().equals(PathComponentType.CHILD)
									|| cleanPath.get(cleanPath.size() - 1).getType().equals(PathComponentType.INDEX))) {
						cleanPath.remove(cleanPath.size() - 1);
					} else {
						cleanPath.add(component);
					}
					break;
				
				case CHILD:
				case INDEX:
					cleanPath.add(component);
					break;
			}
		}
		return cleanPath;
	}
	
	/**
	 * Navigate following the given `path`.
	 * 
	 * @param path The path of the destination.
	 * @return Returns `true` if navigation was successful, `false` otherwise.
	 * 
	 * @see #navigate(String, PathSymbolsType)
	 */
	public boolean followPath(PathComponentsList path) {
		if (path == null) {
			return false;
		}
		
		for (PathComponent component : path) {
			if (component == null) {
				return false;
			}
			
			switch (component.getType()) {
				case ROOT:
					this.goToRoot();
					break;
				
				case PARENT:
					this.goToParent();
					break;
				
				case CHILD:
					this.goToChild(component.getValue());
					break;
					
				case INDEX:
					this.goToIndex(component.getIndex());
					break;
			}
		}
		return true;
	}
	
	/**
	 * Navigate following the given `path`.
	 * 
	 * @param path The path of the destination.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if navigation was successful, `false` otherwise.
	 * 
	 * @see #followPath(PathComponentsList)
	 */
	public boolean navigate(String path, PathSymbolsType type) {
		return followPath(PathNavigator.parsePath(path, type));
	}
	
	/**
	 * Checks whenever a path string starts with the 'root' node.
	 * 
	 * @param path The path to check.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if the given path is absolute, `false` otherwise.
	 */
	private static boolean isAbsolute(String path, PathSymbolsType type) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		if (path.charAt(0) != type.root) {
			return false;
		}
		if (path.length() > 1) {
			if (PathNavigator.hasParentChar(path, 0, type)) {
				return false;
			}
			if (path.charAt(1) != type.root) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whenever the next(s) characters are a 'parent' node.
	 * 
	 * @param path The path to check.
	 * @param index The index in the path string where the check should occur.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if the next(s) character(s) is/are the 'parent' one(s).
	 */
	private static boolean hasParentChar(String path, int index, PathSymbolsType type) {
		return path.startsWith(type.parent, index);
	}
	
	/**
	 * Checks whenever the next(s) characters are an elements separator(s).
	 * 
	 * @param path The path to check.
	 * @param index The index in the path string where the check should occur.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if the next(s) character(s) is/are the elements separator(s) one(s).
	 */
	private static boolean hasSeperatorChar(String path, int index, PathSymbolsType type) {
		if (index < 0 || index >= path.length()) {
			return false;
		}
		return path.charAt(index) == type.separator;
	}
	
	/**
	 * Checks whenever the next(s) characters are an element's index prefix.
	 * 
	 * @param path The path to check.
	 * @param index The index in the path string where the check should occur.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if the next(s) character(s) is/are the elements' index prefix one(s).
	 */
	private static boolean hasIndexerChar(String path, int index, PathSymbolsType type) {
		if (index < 0 || index >= path.length()) {
			return false;
		}
		return path.charAt(index) == type.indexerPrefix;
	}
	
	/**
	 * Checks whenever the next(s) characters are an element's index suffix.
	 * 
	 * @param path The path to check.
	 * @param index The index in the path string where the check should occur.
	 * @param type The types of the symbols used in the path.
	 * @return Returns `true` if the next(s) character(s) is/are the elements' index suffix one(s).
	 */
	private static boolean hasIndexerEndChar(String path, int index, PathSymbolsType type) {
		if (index < 0 || index >= path.length()) {
			return false;
		}
		return path.charAt(index) == type.indexerSuffix;
	}
	
	/**
	 * Represents the different types of symbols used in string paths.
	 */
	public static enum PathSymbolsType {
		/**
		 * The default Bukkit path symbols type.
		 * 
		 * <ul>
		 * <li>Escape char: &nbsp; &nbsp; &nbsp; 			<tt>'\'</tt></li>
		 * <li>Separator char:&nbsp;						<tt>'.'</tt></li>
		 * <li>Root char: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<tt>'.'</tt></li>
		 * <li>Parent string: &nbsp; &nbsp; 				<tt>'..'</tt></li>
		 * <li>Index prefix: &nbsp; &nbsp; &nbsp; 			<tt>'['</tt></li>
		 * <li>Index suffix: &nbsp; &nbsp; &nbsp;&nbsp; 	<tt>']'</tt></li>
		 * </ul>
		 */
		BUKKIT('\\', '.', '.', "..", '[', ']', false),
		/**
		 * The default Unix path symbols type.
		 * 
		 * <ul>
		 * <li>Escape char: &nbsp; &nbsp; &nbsp; 			<tt>'\'</tt></li>
		 * <li>Separator char:&nbsp;						<tt>'/'</tt></li>
		 * <li>Root char: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<tt>'/'</tt></li>
		 * <li>Parent string: &nbsp; &nbsp; 				<tt>'..'</tt></li>
		 * <li>Index prefix: &nbsp; &nbsp; &nbsp; 			<tt>'['</tt></li>
		 * <li>Index suffix: &nbsp; &nbsp; &nbsp;&nbsp; 	<tt>']'</tt></li>
		 * </ul>
		 */
		UNIX('\\', '/', '/', "..", '[', ']', true);
		
		public final char escape;
		public final char separator;
		public final char root;
		public final String parent;
		public final char indexerPrefix;
		public final char indexerSuffix;
		
		public final boolean wrapParent;
		
		private PathSymbolsType(char p_escape, char p_separator, char p_root, String p_parent, char idxPre, char idxSuf, boolean wParent) {
			this.escape = p_escape;
			this.separator = p_separator;
			this.root = p_root;
			this.parent = p_parent;
			this.indexerPrefix = idxPre;
			this.indexerSuffix = idxSuf;
			this.wrapParent = wParent;
		}
		
		/**
		 * Try to detect the type of symbols used in the given 'apath'.
		 * 
		 * @param apath A path to test.
		 * @return Returns a PathSymbolsType detected in the path.
		 * 
		 * @deprecated This won't detect the type for sure and can lead to data-reading errors.
		 */
		@Deprecated
		public static PathSymbolsType tryDetectType(String apath) {
			int li = apath.lastIndexOf(UNIX.parent);
			if (li >= 0) {
				if (li > 0 && apath.charAt(li - 1) == UNIX.separator) {
					return PathSymbolsType.UNIX;
				} else if ((li + 2) < apath.length() && apath.charAt(li + 2) == UNIX.separator) {
					return PathSymbolsType.UNIX;
				}
			}
			return PathSymbolsType.BUKKIT;
		}
	}
}