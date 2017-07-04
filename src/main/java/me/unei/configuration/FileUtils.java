package me.unei.configuration;

import me.unei.configuration.plugin.UneiConfiguration;

import java.io.*;
import java.util.logging.Level;

public final class FileUtils {

    private static final int BUFFER_SIZE = 12;

    public static int copy(InputStream is, OutputStream os) throws IOException, NullPointerException {
        UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying a stream into another");
        int totalCopyedBytes = 0;
        byte buffer[] = new byte[FileUtils.BUFFER_SIZE];
        int read;
        while ((read = is.read(buffer, 0, FileUtils.BUFFER_SIZE)) > 0) {
            os.write(buffer, 0, read);
            totalCopyedBytes += read;
        }
        os.flush();
        UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(totalCopyedBytes) + " bytes.");
        return totalCopyedBytes;
    }
    
    public static int copy(Reader in, Writer out) throws IOException {
        UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying a set of characters into another");
        int totalCopyedBytes = 0;
        char buffer[] = new char[FileUtils.BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer, 0, FileUtils.BUFFER_SIZE)) > 0) {
            out.write(buffer, 0, read);
            totalCopyedBytes += read;
        }
        out.flush();
        UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(totalCopyedBytes) + " characters.");
        return totalCopyedBytes;
    }

    public static int copy(InputStream is, File out) throws IOException, NullPointerException, FileNotFoundException, SecurityException {
        int tmp;
        UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying a stream into file " + out.getName());
        OutputStream os = new FileOutputStream(out);
        tmp = FileUtils.copy(is, os);
        os.close();
        return tmp;
    }

    public static int copy(File in, OutputStream os) throws IOException, NullPointerException, FileNotFoundException, SecurityException {
        int tmp;
        UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying file " + in.getName() + " into a stream");
        InputStream is = new FileInputStream(in);
        tmp = FileUtils.copy(is, os);
        is.close();
        return tmp;
    }

    public static int copy(File in, File out) throws IOException, NullPointerException, FileNotFoundException, SecurityException {
        int tmp;
        UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying file " + in.getName() + " into file " + out.getName());
        InputStream is = new FileInputStream(in);
        OutputStream os = new FileOutputStream(out);
        tmp = FileUtils.copy(is, os);
        is.close();
        os.close();
        return tmp;
    }

    public static boolean createFile(File file) throws SecurityException, IOException {
        if (!file.getParentFile().exists()) {
            UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Creating directory tree for file " + file.getName() + " : " + file.getParentFile().getPath());
            if (!file.getParentFile().mkdirs()) {
                UneiConfiguration.getInstance().getLogger().warning("Error while creating directory tree : " + file.getParentFile().getAbsolutePath());
                return false;
            }
            UneiConfiguration.getInstance().getLogger().fine("Successfully created directory tree.");
        }
        if (!file.exists()) {
            UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Creating file " + file.getName() + " : " + file.getPath());
            if (!file.createNewFile()) {
                UneiConfiguration.getInstance().getLogger().warning("Error while creating file : " + file.getAbsolutePath());
                return false;
            }
            UneiConfiguration.getInstance().getLogger().fine("Successfully created file.");
        }
        return true;
    }
}