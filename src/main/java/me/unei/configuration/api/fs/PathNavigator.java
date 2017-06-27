package me.unei.configuration.api.fs;

import java.util.regex.Pattern;

import me.unei.configuration.api.fs.PathComponent.PathComponentType;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;

public final class PathNavigator<T extends NavigableFile> {

    public static final char ESCAPE_CHAR = '\\';
    public static final char PATH_SEPARATOR = '.';
    public static final char ROOT_CHAR = '.';
    public static final String PARENT_CHAR = "..";

    public static final Pattern PATH_SEPARATOR_REGEXP = Pattern.compile(Pattern.quote(String.valueOf(PathNavigator.PATH_SEPARATOR)));

    private PathComponentsList currentPath;
    private T currentNode;

    public PathNavigator(T rootFile) {
        this.currentNode = rootFile;
        this.currentPath = PathNavigator.parsePath(rootFile.getCurrentPath());
    }
    
    @SuppressWarnings({"unchecked"})
    private T getChecked(NavigableFile file)
    {
    	try
    	{
    		return (T)file;
    	}
    	catch (ClassCastException e)
    	{
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
        this.currentPath.appendComponent(PathComponentType.CHILD, name);
        this.currentNode = getChecked(this.currentNode.getChild(name));
    }

    public String getCurrentPath() {
        return currentPath.toString();
    }

    public T getCurrentNode() {
        return this.currentNode;
    }

    public static PathComponentsList parsePath(String path) {
        if (path == null || path.isEmpty()) {
            return new PathComponentsList();
        }

        PathComponentsList components = new PathComponentsList();
        StringBuilder lastComponent = new StringBuilder();

        int i = 0;

        if (PathNavigator.isAbsolute(path)) {
            components.appendComponent(PathComponentType.ROOT, String.valueOf(PathNavigator.ROOT_CHAR));
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

            if (c == PathNavigator.ESCAPE_CHAR) {
                escaped = true;
            } else if (PathNavigator.hasParentChar(path, i)) {
                if (lastComponent.length() > 0) {
                    components.appendComponent(PathComponentType.CHILD, lastComponent.toString());
                    lastComponent.setLength(0);
                }
                components.appendComponent(PathComponentType.PARENT, PathNavigator.PARENT_CHAR);
                i++;
            } else if (PathNavigator.hasSeperatorChar(path, i)) {
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
        PathComponentsList cleanPath = new PathComponentsList();
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

    public boolean navigate(String path) {
        return followPath(PathNavigator.parsePath(path));
    }

    private static boolean isAbsolute(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (path.charAt(0) != PathNavigator.ROOT_CHAR) {
            return false;
        }
        if (path.length() > 1) {
            if (PathNavigator.hasParentChar(path, 0)) {
                return false;
            }
            if (path.charAt(1) != PathNavigator.ROOT_CHAR) {
                return true;
            }
            return false;
        }
        return true;
    }

    private static boolean hasParentChar(String path, int index) {
        return path.startsWith(PathNavigator.PARENT_CHAR, index);
    }

    private static boolean hasSeperatorChar(String path, int index) {
        if (index < 0 || index >= path.length()) {
            return false;
        }
        return path.charAt(index) == PathNavigator.PATH_SEPARATOR;
    }
}