package me.unei.configuration;

import java.io.File;

import me.unei.configuration.plugin.UneiConfiguration;

/**
 * A representation of a file.
 * 
 * @version 1.3.0
 * @since 0.0.0
 */
public final class SavedFile {

    private File folder;
    private String fileName = "ERROR_IN_THIS_FILE";

    private String extension = ".null";

    private boolean dummyFile = false;
    private boolean canAccess;
    private File datFile;

    private boolean initialized;

    public SavedFile() {
        this(null, null, null);
        this.dummyFile = true;
    }

    public SavedFile(File folder, String fileName, String extension) {
        this.folder = (folder == null ? new File(".") : folder);
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
    
    public SavedFile(File file) {
    	if (file == null) {
    		throw new NullPointerException("file cannot be null");
    	}
    	this.folder = file.getParentFile();
    	this.canAccess = false;
    	this.initialized = false;
    	this.datFile = file;
    	String name = file.getName();
    	if (!name.contains(".")) {
    		this.fileName = name;
    		this.extension = "";
    	} else {
    		this.fileName = name.substring(0, name.lastIndexOf('.'));
    		this.extension = name.substring(name.lastIndexOf('.'));
    	}
    }

    public boolean init() {
    	if (this.isInitialized()) {
    		return true;
    	}
        if (this.folder == null || this.fileName == null || this.fileName.isEmpty()) {
            return false;
        }
        if (this.dummyFile) {
            this.canAccess = true;
            this.initialized = true;
            return true;
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
        if (this.datFile == null) {
        	this.datFile = new File(this.folder, this.fileName + this.extension);
        }
        this.canAccess = true;
        this.initialized = true;
        return true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Returns <tt>true</tt> if it is currently possible to access and
     * modify this file.
     *
     * @see SavedFile#lock()
     *
     * @return <tt>true</tt> if this file is accessible
     */
    public boolean canAccess() {
        return this.canAccess;
    }

    public void lock() {
        this.canAccess = false;
    }

    public File getFile() {
        if (!this.canAccess() || this.dummyFile) {
            return null;
        }
        return this.datFile;
    }

    public File getFolder() {
        return this.folder;
    }

    /**
     * Returns the name of the {@link File file} that
     * represents this SavedFile without extension.
     *
     * @return the name of this file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Returns the name of the {@link File file} that
     * represents this SavedFile with extension.
     *
     * @return the name of this file
     */
    public String getFullName() {
    	return this.fileName + this.extension;
    }
}