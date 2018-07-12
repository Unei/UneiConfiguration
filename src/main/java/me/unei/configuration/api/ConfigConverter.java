package me.unei.configuration.api;

import org.apache.commons.lang.NotImplementedException;

public final class ConfigConverter
{
	public static boolean copy(IFlatConfiguration from, IFlatConfiguration to)
	{
		if (!from.canAccess() || !to.canAccess())
		{
			return false;
		}
		boolean fromIsFlat = (from instanceof FlatConfiguration);
		boolean toIsFlat = (to instanceof FlatConfiguration);
		if (fromIsFlat && toIsFlat)
		{
			return ConfigConverter.copyFlat(from, to);
		}
		else if (!fromIsFlat && !toIsFlat)
		{
			throw new NotImplementedException("Could not yet execute copies from non-flat configurations");
			// return ConfigConverter.copyNonFlat((IConfiguration) from, (IConfiguration) to);
		}
		else if (fromIsFlat && !toIsFlat)
		{
			return ConfigConverter.copyFlatToNon(from, (IConfiguration) to);
		}
		else
		{
			return ConfigConverter.copyNonToFlat((IConfiguration) from, to);
		}
	}
	
	private static boolean copyFlat(IFlatConfiguration from, IFlatConfiguration to)
	{
		boolean useOnlyStr = (from instanceof UntypedFlatStorage);
		
		for (String key : from.getKeys())
		{
			if (useOnlyStr)
			{
				to.setString(key, from.getString(key));
			}
		}
		return true;
	}
	
	private static boolean copyFlatToNon(IFlatConfiguration from, IConfiguration to)
	{
		boolean useOnlyStr = (from instanceof UntypedFlatStorage);
		
		for (String key : from.getKeys())
		{
			if (useOnlyStr)
			{
				to.setString(key, from.getString(key));
			}
		}
		return true;
	}
	
	private static boolean copyNonToFlat(IConfiguration from, IFlatConfiguration to)
	{
		for (String key : from.getKeys())
		{
			to.setString(key, from.getString(key));
		}
		return false;
	}
	
	@SuppressWarnings({"unused"})
	private static boolean copyNonFlat(IConfiguration from, IConfiguration to)
	{
		
		for (String key : from.getKeys())
		{
			Object tmp = from.get(key);
			to.set(key, tmp); //FIXME: clone values !
		}
		return false;
	}
}
