package me.unei.configuration.api.fs;

public final class OrganizedFs
{
	private static final char PATH_SEPARATOR_DEF = '@';
	private static final char REPLACE_CHAR = '_';
	public static final char ESCAPE_CHAR = '\\';
	public static final char PATH_SEP_CHAR = '.';
	public static final char ROOT_CHAR = '.';
	public static final String PARENT_DEST = "..";
	
	private String currentPath;
	private UneiFsFile currentNode;
	
	public OrganizedFs(UneiFsFile rootFs)
	{
		this.currentNode = rootFs;
		this.currentPath = String.valueOf(OrganizedFs.ROOT_CHAR);
	}
	
	public String getPath()
	{
		return this.currentPath.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.PATH_SEP_CHAR);
	}
	
	public boolean navigate(String path)
	{
		if (path == null || path.isEmpty())
			return false;
		path = path.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.REPLACE_CHAR);
		for (int i = 0; i < path.length(); i++)
		{
			if (OrganizedFs.hasSemethingAt(path, i))
			{
				switch (OrganizedFs.getTypeAt(path, i))
				{
					case Root:
						this.gotoRoot();
						break;
					case Parent:
						this.gotoParent();
						++i;
						break;
					case Separator:
						this.gotoChild(OrganizedFs.getNodeName(path, i + 1));
						break;
						
					default:
						break;
				}
			}
		}
		return true;
	}
	
	public void gotoRoot()
	{
		this.currentPath = String.valueOf(OrganizedFs.ROOT_CHAR);
		this.currentNode = this.currentNode.getRoot();
	}
	
	public void gotoParent()
	{
		this.currentPath = OrganizedFs.getParentPath(currentPath);
		if (this.currentPath.contentEquals(String.valueOf(OrganizedFs.ROOT_CHAR)))
		{
			this.currentNode = this.currentNode.getRoot();
		}
		else
		{
			this.currentNode = this.currentNode.getParent();
		}
	}
	
	public void gotoChild(String name)
	{
		if (name == null || name.isEmpty())
		{
			return;
		}
		name = name.replace(OrganizedFs.PATH_SEPARATOR_DEF, OrganizedFs.REPLACE_CHAR);
		this.currentPath = OrganizedFs.getChildPath(currentPath, name);
		this.currentNode = this.currentNode.getChild(name);
	}
	
	private static String getParentPath(String current)
	{
		int lastIO = current.lastIndexOf(OrganizedFs.PATH_SEPARATOR_DEF);
		if (lastIO < 0)
		{
			return String.valueOf(OrganizedFs.ROOT_CHAR);
		}
		return current.substring(0, lastIO);
	}
	
	private static String getNodeName(String name, int start)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < name.length(); i++)
		{
			if (OrganizedFs.areParentStr(name, i) || name.charAt(i) == OrganizedFs.PATH_SEP_CHAR)
			{
				break;
			}
			sb.append(name.charAt(i));
		}
		return sb.toString();
	}
	
	private static boolean areParentStr(String str, int index)
	{
		return str.startsWith(OrganizedFs.PARENT_DEST, index);
	}
	
	private static String getChildPath(String current, String child)
	{
		if (current.contentEquals(String.valueOf(OrganizedFs.ROOT_CHAR)))
		{
			return child;
		}
		return current + OrganizedFs.PATH_SEPARATOR_DEF + child;
	}
	
	private static SeparatorType getTypeAt(String str, int idx)
	{
		if ((idx > 0 ? str.charAt(idx - 1) == OrganizedFs.ESCAPE_CHAR : false))
		{
			return SeparatorType.None;
		}
		if (OrganizedFs.areParentStr(str, idx))
		{
			return SeparatorType.Parent;
		}
		if (idx == 0 && str.charAt(idx) == OrganizedFs.ROOT_CHAR)
		{
			return SeparatorType.Root;
		}
		if (str.charAt(idx) == OrganizedFs.PATH_SEP_CHAR)
		{
			return SeparatorType.Separator;
		}
		return SeparatorType.None;
	}
	
	private static boolean hasSemethingAt(String str, int idx)
	{
		return OrganizedFs.getTypeAt(str, idx) != SeparatorType.None;
	}
	
	protected static enum SeparatorType
	{
		Parent,
		Root,
		Separator,
		None;
	}
}