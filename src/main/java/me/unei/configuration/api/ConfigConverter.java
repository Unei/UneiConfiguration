package me.unei.configuration.api;

import me.unei.configuration.api.exceptions.NotImplementedException;

import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.Configurations.ConfigurationType.ConfigurationTypeCls;
import me.unei.configuration.api.impl.FlatMySQLConfig;
import me.unei.configuration.api.impl.FlatSQLiteConfig;
import me.unei.configuration.api.impl.MySQLConfig;
import me.unei.configuration.api.impl.SQLiteConfig;

public final class ConfigConverter {
	public static IFlatConfiguration convert(IFlatConfiguration input, ConfigurationType type) {
		if (type == null || input == null || ConfigurationsImpl.getTypeOf(input) == type) {
			return input;
		}

		String tableName = input.getName();

		try {

			if (input instanceof ISQLConfiguration) {
				tableName = ((ISQLConfiguration) input).getTableName();
			}
		} catch (NoSuchMethodError ignored) // TODO: Remove backwards compatibility.
		{

			if (input instanceof SQLiteConfig) {
				tableName = ((SQLiteConfig) input).getTableName();
			} else if (input instanceof FlatSQLiteConfig) {
				tableName = ((FlatSQLiteConfig) input).getTableName();
			} else if (input instanceof MySQLConfig) {
				tableName = ((MySQLConfig) input).getTableName();
			} else if (input instanceof FlatMySQLConfig) {
				tableName = ((FlatMySQLConfig) input).getTableName();
			}
		}

		IFlatConfiguration newInstance = Configurations.newConfig(type, input.getFile(), tableName);

		if (newInstance == null) {
			return null;
		}

		if ((newInstance instanceof IInternalStorageUse) && (input instanceof IInternalStorageUse)) {
			((IInternalStorageUse) newInstance).setStorageObject(((IInternalStorageUse) input).getStorageObject());
		}

		input.lock();

		return newInstance;
	}

	public static <T extends IFlatConfiguration> T changeType(IFlatConfiguration input, ConfigurationTypeCls<T> dest) {
		return dest.safeCast(convert(input, dest.getType()));
	}

	public static boolean copy(IFlatConfiguration from, IFlatConfiguration to) {
		if (!from.canAccess() || !to.canAccess()) {
			return false;
		}
		boolean fromIsFlat = (from instanceof FlatConfiguration);
		boolean toIsFlat = (to instanceof FlatConfiguration);

		if (fromIsFlat && toIsFlat) {
			return ConfigConverter.copyFlat(from, to);
		} else if (!fromIsFlat && !toIsFlat) {
			throw new NotImplementedException("Could not yet execute copies from non-flat configurations");
			// return ConfigConverter.copyNonFlat((IConfiguration) from, (IConfiguration)
			// to);
		} else if (fromIsFlat && !toIsFlat) {
			return ConfigConverter.copyFlatToNon(from, (IConfiguration) to);
		} else {
			return ConfigConverter.copyNonToFlat((IConfiguration) from, to);
		}
	}

	private static boolean copyFlat(IFlatConfiguration from, IFlatConfiguration to) {
		boolean useOnlyStr = (from instanceof UntypedFlatStorage);

		for (String key : from.getKeys()) {

			if (useOnlyStr) {
				to.setString(key, from.getString(key));
			}
		}
		return true;
	}

	private static boolean copyFlatToNon(IFlatConfiguration from, IConfiguration to) {
		boolean useOnlyStr = (from instanceof UntypedFlatStorage);

		for (String key : from.getKeys()) {

			if (useOnlyStr) {
				to.setString(key, from.getString(key));
			}
		}
		return true;
	}

	private static boolean copyNonToFlat(IConfiguration from, IFlatConfiguration to) {
		for (String key : from.getKeys()) {
			to.setString(key, from.getString(key));
		}
		return false;
	}

	@SuppressWarnings({
		"unused"
	})
	private static boolean copyNonFlat(IConfiguration from, IConfiguration to) {
		for (String key : from.getKeys()) {
			Object tmp = from.get(key);
			to.set(key, tmp); // FIXME: clone values !
		}
		return false;
	}
}
