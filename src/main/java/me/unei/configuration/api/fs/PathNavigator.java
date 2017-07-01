package me.unei.configuration.api.fs;

import me.unei.configuration.api.fs.PathComponent.PathComponentType;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;

public final class PathNavigator<T extends NavigableFile> {

    private PathComponentsList currentPath;
    private T currentNode;

    public PathNavigator(T rootFile) {
        this.currentNode = rootFile;
        this.currentPath = rootFile.getRealListPath();
        //this.currentPath = PathNavigator.parsePath(rootFile.getCurrentPath(), PathSymbolsType.BUKKIT);
    }
    
    @Deprecated
    public PathNavigator(T rootFile, PathSymbolsType type) {
        this.currentNode = rootFile;
        this.currentPath = PathNavigator.parsePath(rootFile.getCurrentPath(), type);
    }
    
    @SuppressWarnings({"unchecked"})
    private T getChecked(NavigableFile file) {
    	try {
    		return (T) file;
    	} catch (ClassCastException e) {
    		return null;
    	}
    }

    public void goToRoot() {
        this.currentPath.clear();
        this.currentNode = getChecked(this.currentNode.getRoot());
    }

    public void goToParent() {
        if (!this.currentPath.isEmpty()) {
            this.currentPath.remove(this.currentPath.size() - 1);
            this.currentNode = getChecked(this.currentNode.getParent());
        }
    }

    public void goToChild(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }
        this.currentPath.appendChild(name);
        this.currentNode = getChecked(this.currentNode.getChild(name));
    }

    public String getCurrentPath() {
        return currentPath.toString();
    }

    public T getCurrentNode() {
        return this.currentNode;
    }

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
            } else {
                lastComponent.append(c);
            }
        }

        if (lastComponent.length() > 0) {
            components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
        }

        return components;
    }

    public static PathComponentsList cleanPath(PathComponentsList path) {
        PathComponentsList cleanPath = new PathComponentsList(path.getSymbolsType());
        for (PathComponent component : path) {
            switch(component.getType()) {
                case ROOT:
                    cleanPath.clear();
                    cleanPath.add(component);
                    break;

                case PARENT:
                    if (!cleanPath.isEmpty() && cleanPath.get(cleanPath.size() - 1).getType().equals(PathComponentType.CHILD)) {
                        cleanPath.remove(cleanPath.size() - 1);
                    } else {
                        cleanPath.add(component);
                    }
                    break;

                case CHILD:
                    cleanPath.add(component);
                    break;
            }
        }
        return cleanPath;
    }

    public boolean followPath(PathComponentsList path) {
        if (path == null) {
            return false;
        }

        for (PathComponent component : path) {
            if (component == null) {
                return false;
            }

            switch(component.getType()) {
                case ROOT:
                    this.goToRoot();
                    break;

                case PARENT:
                    this.goToParent();
                    break;

                case CHILD:
                    this.goToChild(component.getValue());
                    break;
            }
        }
        return true;
    }

    public boolean navigate(String path, PathSymbolsType type) {
        return followPath(PathNavigator.parsePath(path, type));
    }

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

    private static boolean hasParentChar(String path, int index, PathSymbolsType type) {
        return path.startsWith(type.parent, index);
    }

    private static boolean hasSeperatorChar(String path, int index, PathSymbolsType type) {
        if (index < 0 || index >= path.length()) {
            return false;
        }
        return path.charAt(index) == type.separator;
    }
    
    public static enum PathSymbolsType
    {
    	BUKKIT('\\', '.', '.', ".."),
    	UNIX('\\', '/', '/', "..");
    	
    	public final char	escape;
    	public final char	separator;
    	public final char	root;
    	public final String	parent;
    	
    	private PathSymbolsType(char p_escape, char p_separator, char p_root, String p_parent)
    	{
    		this.escape = p_escape;
    		this.separator = p_separator;
    		this.root = p_root;
    		this.parent = p_parent;
    	}
    	
    	@Deprecated
    	public static PathSymbolsType tryDetectType(String apath)
    	{
    		int li = apath.lastIndexOf(UNIX.parent);
    		if (li >= 0)
    		{
    			if (li > 0 && apath.charAt(li - 1) == UNIX.separator)
    			{
    				return PathSymbolsType.UNIX;
    			}
    			else if ((li + 2) < apath.length() && apath.charAt(li + 2) == UNIX.separator)
    			{
    				return PathSymbolsType.UNIX;
    			}
    		}
    		return PathSymbolsType.BUKKIT;
    	}
    }
}