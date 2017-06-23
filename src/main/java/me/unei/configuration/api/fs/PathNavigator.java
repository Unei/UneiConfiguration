package me.unei.configuration.api.fs;

import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathComponent.PathComponentType;

import java.util.regex.Pattern;

public final class PathNavigator {

    public static final char ESCAPE_CHAR = '\\';
    public static final char PATH_SEPARATOR = '.';
    public static final char ROOT_CHAR = '.';
    public static final String PARENT_CHAR = "..";

    public static final Pattern PATH_SEPARATOR_REGEXP = Pattern.compile(Pattern.quote(String.valueOf(PathNavigator.PATH_SEPARATOR)));

    private PathComponentsList currentPath;
    private NavigableFile currentNode;

    public PathNavigator(NavigableFile rootFile) {
        this.currentNode = rootFile;
        this.currentPath = new PathComponent.PathComponentsList();
    }

    public void goToRoot() {
        this.currentPath.clear();
        this.currentNode = this.currentNode.getRoot();
    }

    public void goToParent() {
        if (!this.currentPath.isEmpty()) {
            this.currentPath.remove(this.currentPath.size() - 1);
            this.currentNode = this.currentNode.getParent();
        }
    }

    public void goToChild(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }
        this.currentPath.appendComponent(PathComponentType.CHILD, name);
        this.currentNode = this.currentNode.getChild(name);
    }

    public String getCurrentPath() {
        return currentPath.toString();
    }

    public NavigableFile getCurrentNode() {
        return this.currentNode;
    }

    public static PathComponentsList parsePath(String path) {
        if (path == null || path.isEmpty()) {
            return new PathComponentsList();
        }

        PathComponentsList components = new PathComponentsList();
        StringBuilder lastComponent = new StringBuilder();

        if (PathNavigator.isAbsolute(path)) {
            components.appendComponent(PathComponentType.ROOT, String.valueOf(PathNavigator.ROOT_CHAR));
        }

        boolean escaped = false;
        for (int i = 0; i < path.length(); i++) {
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
            } else if (PathNavigator.hasParentChar(path, i)) {
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
        return followPath(parsePath(path));
    }

    private static boolean isAbsolute(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (path.charAt(0) != PathNavigator.ROOT_CHAR) {
            return false;
        }
        if (path.length() > 1) {
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