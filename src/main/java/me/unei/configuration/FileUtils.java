package me.unei.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import me.unei.configuration.plugin.UneiConfiguration;

public final class FileUtils
{
	private static final int BUFFER_SIZE = 12;
	
	private static int copy0(InputStream is, OutputStream os) throws IOException, NullPointerException
	{
		int totalCopyedBytes = 0;
		byte buffer[] = new byte[FileUtils.BUFFER_SIZE];
		int readed;
		while ((readed = is.read(buffer, 0, FileUtils.BUFFER_SIZE)) > 0)
		{
			os.write(buffer, 0, readed);
			totalCopyedBytes += readed;
		}
		os.flush();
		return totalCopyedBytes;
	}
	
	public static int copy(InputStream is, OutputStream os) throws IOException, NullPointerException
	{
		int tmp;
		UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying a stream into another");
		tmp = FileUtils.copy0(is, os);
		UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(tmp) + " byte(s).");
		return tmp;
	}
	
	public static int copy(InputStream is, File out) throws IOException, NullPointerException, FileNotFoundException, SecurityException
	{
		int tmp;
		UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying a stream into file " + out.getName());
		OutputStream os = new FileOutputStream(out);
		tmp = FileUtils.copy0(is, os);
		os.close();
		UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(tmp) + " byte(s).");
		return tmp;
	}
	
	public static int copy(File in, OutputStream os) throws IOException, NullPointerException, FileNotFoundException, SecurityException
	{
		int tmp;
		UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying file " + in.getName() + " into a stream");
		InputStream is = new FileInputStream(in);
		tmp = FileUtils.copy0(is, os);
		is.close();
		UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(tmp) + " byte(s).");
		return tmp;
	}
	
	public static int copy(File in, File out) throws IOException, NullPointerException, FileNotFoundException, SecurityException
	{
		int tmp;
		UneiConfiguration.getInstance().getLogger().log(Level.FINEST, "Copying file " + in.getName() + " into file " + out.getName());
		InputStream is = new FileInputStream(in);
		OutputStream os = new FileOutputStream(out);
		tmp = FileUtils.copy0(is, os);
		is.close();
		os.close();
		UneiConfiguration.getInstance().getLogger().log(Level.FINE, "Successfully copied " + Integer.toString(tmp) + " byte(s).");
		return tmp;
	}
}