package me.unei.configuration.api.fs;

import java.util.Iterator;

import me.unei.configuration.api.fs.DestinationDescription.DestinationList;
import me.unei.configuration.api.fs.DestinationDescription.DestinationType;

public final class OrganizedFs {

    //@formatter:off
    private static final char PATH_SEPARATOR_DEF = '@';
    private static final char REPLACE_CHAR = '_';
    public static final char ESCAPE_CHAR = '\\';
    public static final char PATH_SEP_CHAR = '.';
    public static final char ROOT_CHAR = '.';
    public static final String PARENT_DEST = "..";
    //@formatter:on

    private String currentPath;
    private UneiFsFile currentNode;

    public OrganizedFs(UneiFsFile rootFs) {
        this.currentNode = rootFs;
        this.currentPath = String.valueOf(OrganizedFs.ROOT_CHAR);
    }

    public String getPath() {
        return this.currentPath.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.PATH_SEP_CHAR);
    }

    public boolean navigate(String path) {
        if (path == null || path.isEmpty()) return false;
        path = path.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.REPLACE_CHAR);
        return true;
    }

    public boolean followPath(DestinationList path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        Iterator<DestinationDescription> it = path.iterator();
        while (it.hasNext()) {
            DestinationDescription part = it.next();
            if (part == null) {
                return false;
            }
            if (part.getType().equals(DestinationType.Root)) {
                this.gotoRoot();
            } else if (part.getType().equals(DestinationType.Parent)) {
                this.gotoParent();
            } else {
                this.gotoChild(part.getValue());
            }
        }
        return true;
    }

    public void gotoRoot() {
        this.currentPath = String.valueOf(OrganizedFs.ROOT_CHAR);
        this.currentNode = this.currentNode.getRoot();
    }

    public void gotoParent() {
        this.currentPath = OrganizedFs.getParentPath(currentPath);
        if (this.currentPath.contentEquals(String.valueOf(OrganizedFs.ROOT_CHAR))) {
            this.currentNode = this.currentNode.getRoot();
        } else {
            this.currentNode = this.currentNode.getParent();
        }
    }

    public void gotoChild(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }
        name = name.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.REPLACE_CHAR);
        this.currentPath = OrganizedFs.getChildPath(currentPath, name);
        this.currentNode = this.currentNode.getChild(name);
    }

    private static String getParentPath(String current) {
        int lastIO = current.lastIndexOf(OrganizedFs.PATH_SEPARATOR_DEF);
        if (lastIO < 0) {
            return String.valueOf(OrganizedFs.ROOT_CHAR);
        }
        return current.substring(0, lastIO);
    }

    private static boolean areParentStr(String str, int index) {
        return str.startsWith(OrganizedFs.PARENT_DEST, index);
    }

    private static String getChildPath(String current, String child) {
        if (current.contentEquals(String.valueOf(OrganizedFs.ROOT_CHAR))) {
            return child;
        }
        return current + OrganizedFs.PATH_SEPARATOR_DEF + child;
    }

    private static boolean isRoot(String str, int idx) {
        if (idx > 0) {
            return false;
        }
        if (str.charAt(idx) != OrganizedFs.ROOT_CHAR) {
            return false;
        }
        if (str.length() > (idx + 1)) {
            if (str.charAt(idx + 1) != OrganizedFs.ROOT_CHAR) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static DestinationList parsePath(String path) {
        if (path == null || path.isEmpty()) {
            return new DestinationList();
        }
        path = path.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.REPLACE_CHAR);
        DestinationList components = new DestinationList();
        StringBuilder component = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < path.length(); ++i) {
            char sel = path.charAt(i);

            if (escaped) {
                component.append(sel);
                escaped = false;
                continue;
            }
            if (OrganizedFs.isRoot(path, i)) {
                components.add(DestinationType.Root, String.valueOf(OrganizedFs.ROOT_CHAR));
            } else if (sel == OrganizedFs.ESCAPE_CHAR) {
                escaped = true;
            } else if (OrganizedFs.areParentStr(path, i)) {
                if (component.length() > 0) {
                    components.add(DestinationType.Child, component.toString());
                    component.setLength(0);
                }
                components.add(DestinationType.Parent, OrganizedFs.PARENT_DEST);
                ++i;
            } else if (sel == OrganizedFs.PATH_SEP_CHAR) {
                if (component.length() > 0) {
                    components.add(DestinationType.Child, component.toString());
                    component.setLength(0);
                }
            } else {
                component.append(sel);
            }
        }
        if (component.length() > 0) {
            components.add(DestinationType.Child, component.toString());
        }
        return components;
    }

    protected static enum SeparatorType {
        Parent, Root, Separator, None;
    }
}