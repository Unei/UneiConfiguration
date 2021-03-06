package me.unei.configuration.api.impl;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.IFlatSQLiteConfiguration;
import me.unei.configuration.api.UntypedFlatStorage;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.exceptions.NoFieldException;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.plugin.UneiConfiguration;

public final class FlatSQLiteConfig extends UntypedFlatStorage<FlatSQLiteConfig> implements IFlatSQLiteConfiguration {

	public static final String SQLITE_FILE_EXT = ".db";
	public static final String SQLITE_DRIVER = "org.sqlite.JDBC";

	private Map<String, String> data = new HashMap<String, String>();

	private Connection connection = null;
	private String tableName = "_";

	public FlatSQLiteConfig(SavedFile file, String tableName) {
		super(file);

		this.tableName = tableName;

		this.subinit();
	}

	public FlatSQLiteConfig(File folder, String fileName, String tableName) {
		this(new SavedFile(folder, fileName, FlatSQLiteConfig.SQLITE_FILE_EXT), tableName);
	}

	@Override
	public ConfigurationType getConfigurationType() {
		return ConfigurationType.FlatSQLite;
	}

	public String getTableName() {
		return this.tableName;
	}

	private void subinit() {
		try {
			Class.forName(FlatSQLiteConfig.SQLITE_DRIVER);
		} catch (ClassNotFoundException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("Could not load SQLite driver " + FlatSQLiteConfig.SQLITE_DRIVER + ":");
			e.printStackTrace();
			return;
		}
		this.file.init();
		this.reload();
	}

	private static String getHash(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return SerializerHelper.hexbinToString(digest.digest(text.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			UneiConfiguration.getInstance().getLogger().warning("Could not calculate MD5 hash of " + text + ":");
			e.printStackTrace();
		}
		String hex = SerializerHelper.hexbinToString(text.getBytes());

		if (hex.length() > 32) {
			hex = hex.substring(0, 32);
		}

		while (hex.length() < 32) {
			hex = "0" + hex;
		}
		return hex;
	}

	public boolean execute(String query, Map<Integer, Object> parameters) throws SQLException {
		if (this.connection == null) {
			return false;
		}

		PreparedStatement statement = null;

		try {
			statement = this.connection.prepareStatement(query);

			if (parameters != null) {

				for (Entry<Integer, Object> entry : parameters.entrySet()) {
					statement.setObject(entry.getKey(), entry.getValue());
				}
			}
			boolean result = statement.execute();
			statement.close();
			return result;
		} catch (SQLException e) {

			if (statement != null) {
				statement.close();
			}
			throw e;
		}
	}

	public ResultSet query(String query, Map<Integer, Object> parameters) throws SQLException {
		if (this.connection == null) {
			return null;
		}

		PreparedStatement statement = null;

		try {
			statement = this.connection.prepareStatement(query);

			if (parameters != null) {

				for (Entry<Integer, Object> entry : parameters.entrySet()) {
					statement.setObject(entry.getKey(), entry.getValue());
				}
			}
			return statement.executeQuery();
		} catch (SQLException e) {

			if (statement != null) {
				statement.close();
			}
			throw e;
		}
	}

	public int update(String query, Map<Integer, Object> parameters) throws SQLException {
		if (this.connection == null) {
			return -1;
		}

		PreparedStatement statement = null;

		try {
			statement = this.connection.prepareStatement(query);

			if (parameters != null) {

				for (Entry<Integer, Object> entry : parameters.entrySet()) {
					statement.setObject(entry.getKey(), entry.getValue());
				}
			}
			int result = statement.executeUpdate();
			statement.close();
			return result;
		} catch (SQLException e) {

			if (statement != null) {
				statement.close();
			}
			throw e;
		}
	}

	public long largeUpdate(String query, Map<Integer, Object> parameters) throws SQLException {
		if (this.connection == null) {
			return -1;
		}

		PreparedStatement statement = null;

		try {
			statement = this.connection.prepareStatement(query);

			if (parameters != null) {

				for (Entry<Integer, Object> entry : parameters.entrySet()) {
					statement.setObject(entry.getKey(), entry.getValue());
				}
			}
			long result = statement.executeLargeUpdate();
			statement.close();
			return result;
		} catch (SQLException e) {

			if (statement != null) {
				statement.close();
			}
			throw e;
		}
	}

	public void save() {
		if (!this.canAccess()) {
			return;
		}

		if (this.connection == null) {

			try {
				this.reconnect();
			} catch (SQLException e) {
				UneiConfiguration.getInstance().getLogger()
						.warning("Could not reload MySQL configuration " + getFileName() + "->" + tableName + ":");
				return;
			}
		}
		PreparedStatement statement = null;

		try {
			UneiConfiguration.getInstance().getLogger()
					.fine("Writing SQL data to SQLite file " + getFileName() + "->" + tableName + "...");
			String table = this.tableName; // TODO: Escape table name
			statement = this.connection
					.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, k, v) VALUES (?, ?, ?)");

			for (Entry<String, String> entry : this.data.entrySet()) {
				statement.setString(1, FlatSQLiteConfig.getHash(entry.getKey()));
				statement.setString(2, entry.getKey());
				statement.setString(3, /* SerializerHelper.toJSONString( */entry.getValue()/* ) */);
				statement.addBatch();
			}

			int[] results = statement.executeBatch();
			statement.close();

			for (int res : results) {

				if (res == PreparedStatement.EXECUTE_FAILED) {
					UneiConfiguration.getInstance().getLogger()
							.warning("SQLite configuration saving failed at some point.");
				}
			}
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (SQLException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("Could not save SQLite configuration " + this.getFileName() + "->" + tableName + ":");
			e.printStackTrace();

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException ex) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("Could not save SQLite configuration " + this.getFileName() + "->" + tableName + ":");
			e.printStackTrace();

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException ex) {
					e.printStackTrace();
				}
			}
			throw e;
		}
	}

	public void reload() {
		if (!this.canAccess()) {
			return;
		}

		if (this.file.getFile() == null) {
			this.data = new StringHashMap<>();
			return;
		}

		if (!this.file.getFile().exists()) {
			this.save();
			return;
		}

		try {
			this.reconnect();
			this.data.clear();

			UneiConfiguration.getInstance().getLogger()
					.fine("Reading SQL data from SQLite file " + getFileName() + "->" + tableName + "...");
			String table = this.tableName; // TODO: Escape table name
			ResultSet result = this.query("SELECT * FROM " + table + "", null);

			while (result.next()) {
				// try {
				String key = result.getString("k");
				// Object value = SerializerHelper.parseJSON(result.getString("v"));

				this.data.put(key, result.getString("v"));
				/*
				 * } catch (IOException e) {
				 * UneiConfiguration.getInstance().getLogger().
				 * warning("Could not reload SQLite configuration " + this.getFileName() + "->"
				 * + tableName + ":");
				 * e.printStackTrace();
				 * }
				 */
			}
			Statement statement = result.getStatement();
			result.close();
			statement.close();
			UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
		} catch (SQLException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("Could not reload SQLite configuration " + this.getFileName() + "->" + tableName + ":");
			e.printStackTrace();
		}
	}

	public void reconnect() throws SQLException {
		if (!this.canAccess()) {
			return;
		}

		if (this.file.getFile() == null) {
			return;
		}
		UneiConfiguration.getInstance().getLogger()
				.fine("Reconnecting to SQLite file " + getFileName() + "->" + tableName + "...");

		try {

			if (this.connection != null) {
				this.connection.close();
			}
		} catch (SQLException e) {
		}

		this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getFile().getPath());

		PreparedStatement statement = null;

		try {
			String table = this.tableName; // TODO: Escape table name
			statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table
					+ " (id VARCHAR(32) UNIQUE PRIMARY KEY, k LONGTEXT, v LONGTEXT)");
			statement.execute();
			statement.close();
			UneiConfiguration.getInstance().getLogger().fine("Successfully reconnected.");
		} catch (SQLException e) {

			if (statement != null) {
				statement.close();
			}
			throw e;
		}
	}

	public void close() {
		if (this.connection == null) {
			return;
		}

		try {
			this.connection.close();
		} catch (SQLException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("Could not close SQLite configuration " + this.getFileName() + "->" + tableName + ":");
			e.printStackTrace();
		}
		this.connection = null;
	}

	public Set<String> getKeys() {
		return this.data.keySet();
	}

	public boolean contains(String key) {
		return data.containsKey(key);
	}

	public String getString(String key) {
		if (!data.containsKey(key)) {
			return null;
		}
		return data.get(key);
	}

	@Override
	public String tryGetString(String key) throws NoFieldException {
		if (!data.containsKey(key)) {
			throw new NoFieldException(key, getFile(), "No value available for this key");
		}
		String res = data.get(key);

		if (res == null) {
			throw new NoFieldException(key, getFile(), "Null value for this key");
		}
		return res;
	}

	public void setString(String key, String value) {
		if (!this.canAccess()) {
			return;
		}

		if (value == null) {
			this.remove(key);
			return;
		}
		data.put(key, value);
	}

	public void remove(String key) {
		if (!this.canAccess()) {
			return;
		}
		data.remove(key);
	}

	@Override
	public String toString() {
		return "FlatSQLiteConfig=" + this.data.toString();
	}
}
