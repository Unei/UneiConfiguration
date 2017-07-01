package me.unei.configuration.api;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;

public abstract class FlatConfiguration<T extends FlatConfiguration<T>> implements IFlatConfiguration {

    protected SavedFile file;

    protected String nodeName;

    protected FlatConfiguration(SavedFile p_file) {
        if (p_file == null) {
            throw new IllegalArgumentException("SavedFile should not be null");
        }
        this.file = p_file;
        this.nodeName = "";
    }

    protected final void init() {
        this.file.init();
        this.reload();
    }

    public final SavedFile getFile() {
        return this.file;
    }

    public final String getFileName() {
        return this.file.getFileName();
    }

    public String getName() {
        return this.nodeName;
    }

    public final boolean canAccess() {
        return this.file.canAccess();
    }

    public final void lock() {
        this.file.lock();
    }
}