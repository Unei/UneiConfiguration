package me.unei.configuration.api;

import java.io.File;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.impl.BinaryConfig;
import me.unei.configuration.api.impl.CSVConfig;
import me.unei.configuration.api.impl.FlatMySQLConfig;
import me.unei.configuration.api.impl.FlatSQLiteConfig;
import me.unei.configuration.api.impl.JSONConfig;
import me.unei.configuration.api.impl.MySQLConfig;
import me.unei.configuration.api.impl.NBTConfig;
import me.unei.configuration.api.impl.PropertiesConfig;
import me.unei.configuration.api.impl.SQLiteConfig;
import me.unei.configuration.api.impl.YAMLConfig;

public final class ConfigurationsImpl extends Configurations {
	public static ConfigurationType getTypeOf(IFlatConfiguration cfg) {
		try {
			return cfg.getConfigurationType();
		} catch (NoSuchMethodError ignored) { // Backwards compatibility

			if (cfg instanceof FlatConfiguration) {
				return ((FlatConfiguration<?>) cfg).getConfigurationType();
			}

			if (cfg instanceof Configuration) {
				return ((Configuration<?>) cfg).getConfigurationType();
			}
		}
		return null;
	}

	public static final class FlatConfigurationsImpl extends FlatConfigurations {
		protected IFlatPropertiesConfiguration internal_newPropertiesConfig(SavedFile file) {
			return new PropertiesConfig(file);
		}

		protected IFlatPropertiesConfiguration internal_newPropertiesConfig(File folder, String fileName) {
			return new PropertiesConfig(folder, fileName);
		}

		protected IFlatCSVConfiguration internal_newCSVConfig(SavedFile file) {
			return new CSVConfig(file);
		}

		protected IFlatCSVConfiguration internal_newCSVConfig(File folder, String fileName) {
			return new CSVConfig(folder, fileName);
		}

		protected IFlatSQLiteConfiguration internal_newFlatSQLiteConfig(SavedFile file, String tableName) {
			return new FlatSQLiteConfig(file, tableName);
		}

		protected IFlatSQLiteConfiguration internal_newFlatSQLiteConfig(File folder, String fileName,
				String tableName) {
			return new FlatSQLiteConfig(folder, fileName, tableName);
		}

		protected IFlatMySQLConfiguration internal_newFlatMySQLConfig(String host, int port, String base, String user,
				String pass, String tableName) {
			return new FlatMySQLConfig(host, port, base, user, pass, tableName);
		}

		private FlatConfigurationsImpl() {
			setInstance();
		}

		static {
			new FlatConfigurationsImpl();
			ConfigurationsImpl.init();
		}

		public static void init() {
			/* Call static {} */ }
	}

	protected IJSONConfiguration internal_newJSONConfig(SavedFile file) {
		return new JSONConfig(file, DefaultPathSymbolsType);
	}

	protected IJSONConfiguration internal_newJSONConfig(File folder, String fileName) {
		return new JSONConfig(folder, fileName, DefaultPathSymbolsType);
	}

	protected IJSONConfiguration internal_newJSONConfigFromRawData(String data) {
		return new JSONConfig(data, DefaultPathSymbolsType);
	}

	protected IJSONConfiguration internal_newJSONConfig(File folder, String fileName, String path) {
		return JSONConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}

	protected IJSONConfiguration internal_newJSONConfig(SavedFile file, String path) {
		return JSONConfig.getForPath(new JSONConfig(file, DefaultPathSymbolsType), path);
	}

	protected IJSONConfiguration internal_newJSONConfig(IConfiguration root, String path) {
		if (!(root instanceof JSONConfig)) {
			throw new IllegalArgumentException("root must be an instance of JSONConfig");
		}
		return JSONConfig.getForPath((JSONConfig) root, path);
	}

	protected INBTConfiguration internal_newNBTConfig(SavedFile file) {
		return new NBTConfig(file, DefaultPathSymbolsType);
	}

	protected INBTConfiguration internal_newNBTConfig(File folder, String fileName) {
		return new NBTConfig(folder, fileName, DefaultPathSymbolsType);
	}

	protected INBTConfiguration internal_newNBTConfig(File folder, String fileName, String path) {
		return NBTConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}

	protected INBTConfiguration internal_newNBTConfig(SavedFile file, String path) {
		return NBTConfig.getForPath(new NBTConfig(file, DefaultPathSymbolsType), path);
	}

	protected INBTConfiguration internal_newNBTConfig(IConfiguration root, String path) {
		if (!(root instanceof NBTConfig)) {
			throw new IllegalArgumentException("root must be an instance of NBTConfig");
		}
		return NBTConfig.getForPath((NBTConfig) root, path);
	}

	protected IYAMLConfiguration internal_newYAMLConfig(SavedFile file) {
		return new YAMLConfig(file, DefaultPathSymbolsType);
	}

	protected IYAMLConfiguration internal_newYAMLConfig(File folder, String fileName) {
		return new YAMLConfig(folder, fileName, DefaultPathSymbolsType);
	}

	protected IYAMLConfiguration internal_newYAMLConfigFromRawData(String data) {
		return new YAMLConfig(data, DefaultPathSymbolsType);
	}

	protected IYAMLConfiguration internal_newYAMLConfig(File folder, String fileName, String path) {
		return YAMLConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}

	protected IYAMLConfiguration internal_newYAMLConfig(SavedFile file, String path) {
		return YAMLConfig.getForPath(new YAMLConfig(file, DefaultPathSymbolsType), path);
	}

	protected IYAMLConfiguration internal_newYAMLConfig(IConfiguration root, String path) {
		if (!(root instanceof YAMLConfig)) {
			throw new IllegalArgumentException("root must be an instance of YAMLConfig");
		}
		return YAMLConfig.getForPath((YAMLConfig) root, path);
	}

	protected IConfiguration internal_newBinaryConfig(SavedFile file) {
		return new BinaryConfig(file, DefaultPathSymbolsType);
	}

	protected IConfiguration internal_newBinaryConfig(File folder, String fileName) {
		return new BinaryConfig(folder, fileName, DefaultPathSymbolsType);
	}

	protected IConfiguration internal_newBinaryConfig(File folder, String fileName, String path) {
		return BinaryConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}

	protected IConfiguration internal_newBinaryConfig(SavedFile file, String path) {
		return BinaryConfig.getForPath(new BinaryConfig(file, DefaultPathSymbolsType), path);
	}

	protected IConfiguration internal_newBinaryConfig(IConfiguration root, String path) {
		if (!(root instanceof BinaryConfig)) {
			throw new IllegalArgumentException("root must be an instance of BinaryConfig");
		}
		return BinaryConfig.getForPath((BinaryConfig) root, path);
	}

	protected ISQLiteConfiguration internal_newSQLiteConfig(SavedFile file, String tableName) {
		return new SQLiteConfig(file, tableName, DefaultPathSymbolsType);
	}

	protected ISQLiteConfiguration internal_newSQLiteConfig(File folder, String fileName, String tableName) {
		return new SQLiteConfig(folder, fileName, tableName, DefaultPathSymbolsType);
	}

	protected ISQLiteConfiguration internal_newSQLiteConfig(File folder, String fileName, String tableName,
			String path) {
		return SQLiteConfig.getForPath(folder, fileName, path, tableName, DefaultPathSymbolsType);
	}

	protected ISQLiteConfiguration internal_newSQLiteConfig(SavedFile file, String tableName, String path) {
		return SQLiteConfig.getForPath(new SQLiteConfig(file, tableName, DefaultPathSymbolsType), path);
	}

	protected ISQLiteConfiguration internal_newSQLiteConfig(IConfiguration root, String path) {
		if (!(root instanceof SQLiteConfig)) {
			throw new IllegalArgumentException("root must be an instance of SQLiteConfig");
		}
		return SQLiteConfig.getForPath((SQLiteConfig) root, path);
	}

	protected IMySQLConfiguration internal_newMySQLConfig(String host, int port, String base, String user, String pass,
			String tableName) {
		return new MySQLConfig(host, port, base, user, pass, tableName, DefaultPathSymbolsType);
	}

	protected IMySQLConfiguration internal_newMySQLConfig(String host, int port, String base, String user, String pass,
			String tableName, String path) {
		return MySQLConfig.getForPath(host, port, base, user, pass, tableName, path, DefaultPathSymbolsType);
	}

	protected IMySQLConfiguration internal_newMySQLConfig(IConfiguration root, String path) {
		if (!(root instanceof MySQLConfig)) {
			throw new IllegalArgumentException("root must be an instance of MySQLConfig");
		}
		return MySQLConfig.getForPath((MySQLConfig) root, path);
	}

	private ConfigurationsImpl() {
		setInstance();
	}

	static {
		new ConfigurationsImpl();
		FlatConfigurationsImpl.init();
	}

	public static void init() {
		/* Call static {} */ }
}
