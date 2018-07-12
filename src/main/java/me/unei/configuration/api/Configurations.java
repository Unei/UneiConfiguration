package me.unei.configuration.api;

import java.io.File;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
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

public final class Configurations
{
	/**
	 * The default {@link PathSymbolsType} used for all generator methods of the {@link Configurations} class.
	 * 
	 * <p>Default value: <b>{@link PathSymbolsType#BUKKIT}</b></p>
	 */
	public static PathSymbolsType DefaultPathSymbolsType = PathSymbolsType.BUKKIT;
	
	/**
	 * Enumeration of the different Configuration types available.
	 * 
	 * @see Configurations#newConfig(ConfigurationType, SavedFile, String)
	 * @see Configurations#newConfig(ConfigurationType, File, String, String)
	 * @see Configurations#newSubConfig(ConfigurationType, IConfiguration, String)
	 */
	public static enum ConfigurationType
	{
		/**
		 * A Binary Configuration type.
		 */
		Binary("Binary", "bin"),
		/**
		 * A "Comma Separated Values" (CSV) Configuration type.
		 */
		CSV("(Flat) Comma Separated Values", "comma separated values", "csv"),
		/**
		 * A MySQL Configuration type but with 'flat' values allocations.
		 * 
		 * @see ConfigurationType#MySQL
		 */
		FlatMySQL("(Flat) MySQL", "FlatMySQL"),
		/**
		 * A SQLite Configuration type but with 'flat' values allocations.
		 * 
		 * @see ConfigurationType#SQLite
		 */
		FlatSQLite("(Flat) SQLite", "FlatSQLite"),
		/**
		 * A JSON Configuration type.
		 */
		JSON("JSON"),
		/**
		 * A MySQL Configuration type but with 'non-flat' values allocations.
		 * 
		 * @see ConfigurationType#FlatMySQL
		 */
		MySQL("MySQL"),
		/**
		 * A Mojang NBT Tags Configuration type. (Type used for Minecraft data files).
		 */
		NBT("NBT"),
		/**
		 * A Java Properties Configuration type. (Often used for languages configuration files).
		 */
		Properties("(Flat) Properties", "Properties", "lang"),
		/**
		 * A SQLite Configuration type but with 'non-flat' values allocations.
		 * 
		 * @see ConfigurationType#FlatSQLite
		 */
		SQLite("SQLite"),
		/**
		 * A YAML Configuration type. (Type used for CraftBukki/Spigot/Forge/... configuration files).
		 */
		YAML("YAML");
		
		private final String displayName;
		private final String[] aliases;
		
		private ConfigurationType(String name, String...aliases)
		{
			this.displayName = name;
			this.aliases = (aliases != null) ? aliases : new String[0];
		}
		
		/**
		 * Gets a short description (or a long name) of this configuration type.
		 * 
		 * @return Returns a human-readable full name.
		 */
		public String getDescription()
		{
			return this.displayName;
		}
		
		/**
		 * Gets the names used for this configuration type.
		 * 
		 * @return Returns a list of names.
		 */
		public String[] getNames()
		{
			return this.aliases;
		}
		
		public static ConfigurationType getByName(String name) {
			for (ConfigurationType type : ConfigurationType.values()) {
				if (type.getDescription().equalsIgnoreCase(name) || type.name().equalsIgnoreCase(name)) {
					return type;
				}
			}
			for (ConfigurationType type : ConfigurationType.values()) {
				for (String alias : type.getNames()) {
					if (alias.equalsIgnoreCase(name)) return type;
				}
			}
			return null;
		}
		
		public static ConfigurationType getByOrdinal(int ord)
		{
			if (ord < 0 || ord >= ConfigurationType.values().length)
			{
				return null;
			}
			return ConfigurationType.values()[ord];
		}
	}
	
	public static final class FlatConfigurations
	{
		public static IFlatPropertiesConfiguration newPropertiesConfig(SavedFile file)
		{
			return new PropertiesConfig(file);
		}

		public static IFlatPropertiesConfiguration newPropertiesConfig(File folder, String fileName)
		{
			return new PropertiesConfig(folder, fileName);
		}

		public static IFlatCSVConfiguration newCSVConfig(SavedFile file)
		{
			return new CSVConfig(file);
		}

		public static IFlatCSVConfiguration newCSVConfig(File folder, String fileName)
		{
			return new CSVConfig(folder, fileName);
		}

		public static IFlatSQLiteConfiguration newFlatSQLiteConfig(SavedFile file, String tableName)
		{
			return new FlatSQLiteConfig(file, tableName);
		}

		public static IFlatSQLiteConfiguration newFlatSQLiteConfig(File folder, String fileName, String tableName)
		{
			return new FlatSQLiteConfig(folder, fileName, tableName);
		}

		public static IFlatMySQLConfiguration newFlatMySQLConfig(String host, int port, String base, String user, String pass, String tableName)
		{
			return new FlatMySQLConfig(host, port, base, user, pass, tableName);
		}
	}
	
	public static IJSONConfiguration newJSONConfig(SavedFile file)
	{
		return new JSONConfig(file, DefaultPathSymbolsType);
	}
	
	public static IJSONConfiguration newJSONConfig(File folder, String fileName)
	{
		return new JSONConfig(folder, fileName, DefaultPathSymbolsType);
	}
	
	public static IJSONConfiguration newJSONConfigFromRawData(String data)
	{
		return new JSONConfig(data, DefaultPathSymbolsType);
	}
	
	public static IJSONConfiguration newJSONConfig(File folder, String fileName, String path)
	{
		return JSONConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}
	
	public static IJSONConfiguration newJSONConfig(SavedFile file, String path)
	{
		return JSONConfig.getForPath(new JSONConfig(file, DefaultPathSymbolsType), path);
	}
	
	public static IJSONConfiguration newJSONConfig(IConfiguration root, String path)
	{
		if (!(root instanceof JSONConfig))
		{
			throw new IllegalArgumentException("root must be an instance of JSONConfig");
		}
		return JSONConfig.getForPath((JSONConfig) root, path);
	}

	public static INBTConfiguration newNBTConfig(SavedFile file)
	{
		return new NBTConfig(file, DefaultPathSymbolsType);
	}
	
	public static INBTConfiguration newNBTConfig(File folder, String fileName)
	{
		return new NBTConfig(folder, fileName, DefaultPathSymbolsType);
	}
	
	public static INBTConfiguration newNBTConfig(File folder, String fileName, String path)
	{
		return NBTConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}
	
	public static INBTConfiguration newNBTConfig(SavedFile file, String path)
	{
		return NBTConfig.getForPath(new NBTConfig(file, DefaultPathSymbolsType), path);
	}
	
	public static INBTConfiguration newNBTConfig(IConfiguration root, String path)
	{
		if (!(root instanceof NBTConfig))
		{
			throw new IllegalArgumentException("root must be an instance of NBTConfig");
		}
		return NBTConfig.getForPath((NBTConfig) root, path);
	}

	public static IYAMLConfiguration newYAMLConfig(SavedFile file)
	{
		return new YAMLConfig(file, DefaultPathSymbolsType);
	}
	
	public static IYAMLConfiguration newYAMLConfig(File folder, String fileName)
	{
		return new YAMLConfig(folder, fileName, DefaultPathSymbolsType);
	}
	
	public static IYAMLConfiguration newYAMLConfigFromRawData(String data)
	{
		return new YAMLConfig(data, DefaultPathSymbolsType);
	}
	
	public static IYAMLConfiguration newYAMLConfig(File folder, String fileName, String path)
	{
		return YAMLConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}
	
	public static IYAMLConfiguration newYAMLConfig(SavedFile file, String path)
	{
		return YAMLConfig.getForPath(new YAMLConfig(file, DefaultPathSymbolsType), path);
	}
	
	public static IYAMLConfiguration newYAMLConfig(IConfiguration root, String path)
	{
		if (!(root instanceof YAMLConfig))
		{
			throw new IllegalArgumentException("root must be an instance of YAMLConfig");
		}
		return YAMLConfig.getForPath((YAMLConfig) root, path);
	}

	public static IConfiguration newBinaryConfig(SavedFile file)
	{
		return new BinaryConfig(file, DefaultPathSymbolsType);
	}
	
	public static IConfiguration newBinaryConfig(File folder, String fileName)
	{
		return new BinaryConfig(folder, fileName, DefaultPathSymbolsType);
	}
	
	public static IConfiguration newBinaryConfig(File folder, String fileName, String path)
	{
		return BinaryConfig.getForPath(folder, fileName, path, DefaultPathSymbolsType);
	}
	
	public static IConfiguration newBinaryConfig(SavedFile file, String path)
	{
		return BinaryConfig.getForPath(new BinaryConfig(file, DefaultPathSymbolsType), path);
	}
	
	public static IConfiguration newBinaryConfig(IConfiguration root, String path)
	{
		if (!(root instanceof BinaryConfig))
		{
			throw new IllegalArgumentException("root must be an instance of BinaryConfig");
		}
		return BinaryConfig.getForPath((BinaryConfig) root, path);
	}

	public static ISQLiteConfiguration newSQLiteConfig(SavedFile file, String tableName)
	{
		return new SQLiteConfig(file, tableName, DefaultPathSymbolsType);
	}
	
	public static ISQLiteConfiguration newSQLiteConfig(File folder, String fileName, String tableName)
	{
		return new SQLiteConfig(folder, fileName, tableName, DefaultPathSymbolsType);
	}
	
	public static ISQLiteConfiguration newSQLiteConfig(File folder, String fileName, String tableName, String path)
	{
		return SQLiteConfig.getForPath(folder, fileName, path, tableName, DefaultPathSymbolsType);
	}
	
	public static ISQLiteConfiguration newSQLiteConfig(SavedFile file, String tableName, String path)
	{
		return SQLiteConfig.getForPath(new SQLiteConfig(file, tableName, DefaultPathSymbolsType), path);
	}
	
	public static ISQLiteConfiguration newSQLiteConfig(IConfiguration root, String path)
	{
		if (!(root instanceof SQLiteConfig))
		{
			throw new IllegalArgumentException("root must be an instance of SQLiteConfig");
		}
		return SQLiteConfig.getForPath((SQLiteConfig) root, path);
	}
	
	public static IMySQLConfiguration newMySQLConfig(String host, int port, String base, String user, String pass, String tableName)
	{
		return new MySQLConfig(host, port, base, user, pass, tableName, DefaultPathSymbolsType);
	}
	
	public static IMySQLConfiguration newMySQLConfig(String host, int port, String base, String user, String pass, String tableName, String path)
	{
		return MySQLConfig.getForPath(host, port, base, user, pass, tableName, path, DefaultPathSymbolsType);
	}
	
	public static IMySQLConfiguration newMySQLConfig(IConfiguration root, String path)
	{
		if (!(root instanceof MySQLConfig))
		{
			throw new IllegalArgumentException("root must be an instance of MySQLConfig");
		}
		return MySQLConfig.getForPath((MySQLConfig) root, path);
	}
	
	public static IFlatConfiguration newConfig(ConfigurationType type, SavedFile file, String tableName)
	{
		switch (type)
		{
			case NBT:
				return Configurations.newNBTConfig(file);
			case JSON:
				return Configurations.newJSONConfig(file);
			case SQLite:
				return Configurations.newSQLiteConfig(file, tableName);
			case Binary:
				return Configurations.newBinaryConfig(file);
			case YAML:
				return Configurations.newYAMLConfig(file);
			case CSV:
				return FlatConfigurations.newCSVConfig(file);
			case FlatSQLite:
				return FlatConfigurations.newFlatSQLiteConfig(file, tableName);
			case Properties:
				return FlatConfigurations.newPropertiesConfig(file);
				
			case MySQL:
			case FlatMySQL:
				throw new IllegalArgumentException("Could not create a MySQL configuration type with those arguments");
				
			default:
				return null;
		}
	}
	
	public static IFlatConfiguration newConfig(ConfigurationType type, File folder, String fileName, String tableName)
	{
		switch (type)
		{
			case NBT:
				return Configurations.newNBTConfig(folder, fileName);
			case JSON:
				return Configurations.newJSONConfig(folder, fileName);
			case SQLite:
				return Configurations.newSQLiteConfig(folder, fileName, tableName);
			case Binary:
				return Configurations.newBinaryConfig(folder, fileName);
			case YAML:
				return Configurations.newYAMLConfig(folder, fileName);
			case CSV:
				return FlatConfigurations.newCSVConfig(folder, fileName);
			case FlatSQLite:
				return FlatConfigurations.newFlatSQLiteConfig(folder, fileName, tableName);
			case Properties:
				return FlatConfigurations.newPropertiesConfig(folder, fileName);
				
			case MySQL:
			case FlatMySQL:
				throw new IllegalArgumentException("Could not create a MySQL configuration type with those arguments");
				
			default:
				return null;
		}
	}
	
	public static IConfiguration newSubConfig(ConfigurationType type, IConfiguration root, String path)
	{
		switch (type)
		{
			case NBT:
				return Configurations.newNBTConfig(root, path);
			case JSON:
				return Configurations.newJSONConfig(root, path);
			case SQLite:
				return Configurations.newSQLiteConfig(root, path);
			case Binary:
				return Configurations.newBinaryConfig(root, path);
			case YAML:
				return Configurations.newYAMLConfig(root, path);

			case CSV:
			case FlatSQLite:
			case Properties:
				throw new IllegalArgumentException("Could not use a path on a flat configuration");
				
			case MySQL:
			case FlatMySQL:
				throw new IllegalArgumentException("Could not create a MySQL configuration type with those arguments");
				
			default:
				return null;
		}
	}
}
