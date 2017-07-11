package me.unei.configuration.api.exceptions;

public class UnexpectedClassException extends RuntimeException
{
	private static final long serialVersionUID = -8302500070768143554L;
	
	public UnexpectedClassException()
	{
		super();
	}
	
	public UnexpectedClassException(String message)
	{
		super(message);
	}
	
	public UnexpectedClassException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public UnexpectedClassException(Class<?> obtained)
	{
		super("Class of type " + obtained.getTypeName() + " was not expected here !");
	}
	
	public UnexpectedClassException(Class<?> obtained, Throwable cause)
	{
		super("Class of type " + obtained.getTypeName() + " was not expected here !", cause);
	}
	
	public UnexpectedClassException(Class<?> expected, Class<?> obtained)
	{
		super("Expected class of type " + expected.getTypeName() + " but got class of type " + obtained.getTypeName() + " !");
	}
	
	public UnexpectedClassException(Class<?> expected, Class<?> obtained, Throwable cause)
	{
		super("Expected class of type " + expected.getTypeName() + " but got class of type " + obtained.getTypeName() + " !", cause);
	}
}