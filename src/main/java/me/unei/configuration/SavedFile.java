package me.unei.configuration;

import java.io.File;

import me.unei.configuration.plugin.UneiConfiguration;

public final class SavedFile {

    private File folder;
    private String fileName = "ERROR_IN_THIS_FILE";

    private String extension = ".null";

    private boolean canAccess;
    private File datFile;

    private boolean initialized;

    public SavedFile(File folder, String fileName, String extension) {
        this.folder = (folder == null? new File(".") : folder);
        if (fileName != null) {
            this.fileName = fileName;
        }
        this.canAccess = false;
        this.datFile = null;
        this.initialized = false;
        if (extension != null) {
            this.extension = extension;
        }
    }

    public boolean init() {
        if (this.folder == null || this.fileName == null || this.fileName.isEmpty()) {
            return false;
        }
        if (!this.folder.exists()) {
            UneiConfiguration.getInstance().getLogger().finest("Creating Configuration tree... (" + this.folder.getPath() + ")");
            if (this.folder.mkdirs()) {
                UneiConfiguration.getInstance().getLogger().finest("Successfully created configuration tree. (" + this.folder.getPath() + ")");
            } else {
                UneiConfiguration.getInstance().getLogger().warning("Unable to create the configuration tree : " + this.folder.getAbsolutePath());
                return false;
            }
        }
        this.datFile = new File(this.folder, this.fileName + this.extension);
        this.canAccess = true;
        this.initialized = true;
        return true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean canAccess() {
        return this.canAccess;
    }

    public void lock() {
        this.canAccess = false;
    }

    public File getFile() {
        if (!this.canAccess()) {
            return null;
        }
        return this.datFile;
    }

    public File getFolder() {
        return this.folder;
    }

    public String getFileName() {
        return this.fileName;
    }
}