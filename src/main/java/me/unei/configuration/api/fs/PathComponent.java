package me.unei.configuration.api.fs;

import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.commons.lang.NullArgumentException;

import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;

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

    public static String escapeComponent(String component, PathSymbolsType symType) {
        if (component == null || component.isEmpty()) {
            return component;
        }

        boolean absolute = component.startsWith(String.valueOf(symType.root));
        if (absolute) {
            component = component.substring(1);
        }

        component = component.replace(String.valueOf(symType.escape), String.valueOf(new char[]{symType.escape, symType.escape}));
        component = component.replace(String.valueOf(symType.separator), String.valueOf(new char[]{symType.escape, symType.separator}));

        if (absolute) {
            component = symType.escape + symType.root + component;
        }
        return component;
    }

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
            return true;
        }
        return false;
    }

    public static class PathComponentsList extends ArrayList<PathComponent> {

        private static final long serialVersionUID = 7055238860386957873L;

        private PathSymbolsType symType;

        public PathComponentsList(PathSymbolsType type) {
            super();
            this.symType = type;
        }

        public PathComponentsList(PathComponentsList orig) {
            super(orig);
            this.symType = orig.symType;
        }

        @Override
        public boolean add(PathComponent element) {
            if (element == null) {
                throw new NullArgumentException("element");
            }
            return super.add(element);
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

        public boolean appendRoot() {
            return this.appendComponent(PathComponentType.ROOT, String.valueOf(symType.root));
        }

        public boolean appendParent() {
            return this.appendComponent(PathComponentType.PARENT, symType.parent);
        }

        public PathComponent last() {
            if (this.isEmpty()) {
                return null;
            }
            return this.get(this.size() - 1);
        }

        public String lastChild() {
            PathComponent last = this.last();
            if (last != null) {
                return last.getValue();
            }
            return null;
        }

        public PathComponent removeLast() {
            if (this.isEmpty()) {
                return null;
            }
            return this.remove(this.size() - 1);
        }

        /**
         * @deprecated use {@link PathNavigator#cleanPath(PathComponentsList)} instead
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

        @Override
        public String toString() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent component : this) {
                pathBuilder.append(PathComponent.escapeComponent(component.getValue(), this.symType));
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

    public static enum PathComponentType {
        ROOT, PARENT, CHILD;
    }
}