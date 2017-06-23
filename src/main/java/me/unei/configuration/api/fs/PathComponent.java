package me.unei.configuration.api.fs;

import java.util.ArrayList;

public final class PathComponent {

    private final PathComponentType type;
    private final String value;

    public PathComponent(PathComponentType type, String value) {
        this.type = type;
        this.value = value;
    }

    public PathComponentType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public static String escapeComponent(String component) {
        if (component == null || component.isEmpty()) {
            return component;
        }
        // TODO: Escape depending on PathNavigator's constants
        return component.replace("\\", "\\\\").replace(".", "\\.");
    }

    public static class PathComponentsList extends ArrayList<PathComponent> {

        private static final long serialVersionUID = 7055238860386957873L;

        public PathComponentsList() {
            super();
        }

        public boolean appendComponent(PathComponentType type, String value) {
            return this.add(new PathComponent(type, value));
        }

        public String toString() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent component : this) {
                pathBuilder.append(PathComponent.escapeComponent(component.getValue()));
            }
            return pathBuilder.toString();
        }
    }

    public static enum PathComponentType {
        ROOT, PARENT, CHILD;
    }
}