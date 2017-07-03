package me.unei.configuration.api.exceptions;

import java.io.File;

public class FileFormatException extends Exception
{
	private static final long serialVersionUID = 3594972910118569236L;
	
	public FileFormatException(String requestedFormat, File file, String message)
	{
		super("The file " + file.getAbsolutePath() + " is not in format " + requestedFormat + " : " + message);
	}
	
	public FileFormatException(String requestedFormat, File file, String message, Throwable t)
	{
		super("The file " + file.getAbsolutePath() + " is not in format " + requestedFormat + " : " + message, t);
	}
}