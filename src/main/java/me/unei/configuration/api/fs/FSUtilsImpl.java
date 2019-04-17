package me.unei.configuration.api.fs;

import me.unei.configuration.api.fs.IPathComponent.IPathComponentsList;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;

public class FSUtilsImpl extends FSUtils
{
	@Override
	protected IPathComponentsList internal_parsePath(String path, PathSymbolsType type)
	{
		return PathNavigator.parsePath(path, type);
	}

	@Override
	protected IPathComponentsList internal_cleanPath(IPathComponentsList path)
	{
		return PathNavigator.cleanPath(path);
	}

	@Override
	protected String internal_escapeComponent(String component, PathSymbolsType symType)
	{
		return PathComponent.escapeComponent(component, symType);
	}

	@Override
	protected IPathComponent internal_createComponent(PathComponentType type, String value)
	{
		return new PathComponent(type, value);
	}

	@Override
	protected IPathComponent internal_createComponent(PathComponentType type, int index)
	{
		return new PathComponent(type, index);
	}

	@Override
	protected IPathComponentsList internal_createList(PathSymbolsType symType)
	{
		return new PathComponent.PathComponentsList(symType);
	}

	@Override
	protected IPathComponentsList internal_createList(IPathComponentsList list)
	{
		return new PathComponent.PathComponentsList((PathComponent.PathComponentsList) list);
	}
	
	private FSUtilsImpl() {
		setInstance();
	}
	
	static {
		new FSUtilsImpl();
	}
	
	public static void init() { /* Call static {} */ }
}