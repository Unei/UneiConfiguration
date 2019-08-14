package me.unei.configuration.api.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.IMySQLConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.exceptions.MySQLConnectionException;
import me.unei.configuration.api.exceptions.NotImplementedException;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageConverter;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.plugin.UneiConfiguration;

public final class MySQLConfig extends UntypedStorage<MySQLConfig> implements IMySQLConfiguration {

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private Storage<Object> data = null;

	final Storage<Object> getData() {
		if (data == null)
		{
			data = new StringHashMap<Object>();
		}
		return data;
	}

    private String host;
    private int port;
    private String base;
    private String user;
    private String pass;
    private Connection connection = null;
    private String tableName = "_";

    public MySQLConfig(String host, int port, String base, String user, String pass, String tableName) {
        this(host, port, base, user, pass, tableName, PathSymbolsType.BUKKIT);
    }

    public MySQLConfig(String host, int port, String base, String user, String pass, String tableName, PathSymbolsType symType) {
        super(new SavedFile(), symType);
        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.pass = pass;
        this.tableName = tableName;

        this.subinit();
    }

    private MySQLConfig(MySQLConfig p_parent, String p_nodeName) {
        super(p_parent, p_nodeName);

        this.tableName = this.parent.tableName;
        // this.connection = this.parent.connection;

        this.updateNode();
    }
	
	@Override
	public ConfigurationType getConfigurationType() {
		return ConfigurationType.MySQL;
	}
	
	public String getTableName() {
		return this.tableName;
	}

    private void subinit() {
        if (this.parent != null) {
            this.parent.init();
            return;
        }
        try {
            Class.forName(MySQLConfig.MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not load MySQL driver " + MySQLConfig.MYSQL_DRIVER + ":");
            e.printStackTrace();
            return;
        }
        this.file.init();
        try {
        	this.reload();
        } catch (FileFormatException e) {
        	e.printStackTrace();
        }
    }

    public static MySQLConfig getForPath(String host, int port, String base, String user, String pass, String tableName, String path, PathSymbolsType symType) {
        return MySQLConfig.getForPath(new MySQLConfig(host, port, base, user, pass, tableName, symType), path);
    }

    public static MySQLConfig getForPath(String host, int port, String base, String user, String pass, String tableName, String path) {
        return MySQLConfig.getForPath(new MySQLConfig(host, port, base, user, pass, tableName), path);
    }

    public static MySQLConfig getForPath(MySQLConfig root, String path) {
        if (root == null) {
            return null;
        }
        return root.getSubSection(path);
    }

    private static String getHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return DatatypeConverter.printHexBinary(digest.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not calculate MD5 hash of " + text + ":");
            e.printStackTrace();
        }
        String hex = DatatypeConverter.printHexBinary(text.getBytes());
        if (hex.length() > 32) {
            hex = hex.substring(0, 32);
        }
        while (hex.length() < 32) {
            hex = "0" + hex;
        }
        return hex;
    }

    @Override
	public MySQLConfig getRoot() {
        return (MySQLConfig) super.getRoot();
    }

    public MySQLConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        MySQLConfig child = super.findInChildrens(new Key(name));
		if (child != null) {
			child.parent = this;
			return child;
		}
        return new MySQLConfig(this, name);
    }

	private MySQLConfig getParentObj(PathComponent.PathComponentsList path) {
		PathNavigator<MySQLConfig> pn = new PathNavigator<MySQLConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		if (!pn.followPath(pathList)) {
			return this;
		}
		return pn.getCurrentNode();
	}

    private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
        MySQLConfig dir;
        PathNavigator<MySQLConfig> pn = new PathNavigator<MySQLConfig>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            return data;
        }
        dir = pn.getCurrentNode();
        return dir.data;
    }

    public boolean execute(String query, Map<Integer, Object> parameters) throws SQLException {
        if (this.parent != null) {
            return this.parent.execute(query, parameters);
        }
        
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
        if (this.parent != null) {
            return this.parent.query(query, parameters);
        }
        
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
        if (this.parent != null) {
            return this.parent.update(query, parameters);
        }
        
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
        if (this.parent != null) {
            return this.parent.largeUpdate(query, parameters);
        }
        
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
        if (this.parent != null) {
            this.parent.save();
            return;
        }
        if (this.connection == null) {
        	try {
        		this.reconnect();
        	} catch (SQLException e) {
        		UneiConfiguration.getInstance().getLogger().warning("Could not save MySQL configuration (no connection) " + this.host + ":" + this.port + "->" + tableName);
        		return;
        	}
        }
        PreparedStatement statement = null;
        try {
            UneiConfiguration.getInstance().getLogger().fine("Sending SQL data to MySQL database " + this.host + ":" + this.port + "->" + tableName + "...");
            String table = this.tableName; // TODO: Escape table name
            statement = this.connection.prepareStatement("INSERT INTO " + table + " (id, k, v) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE v = ?");

            for (Entry<Key, Object> entry : this.getData().entryIterable()) {
                if (entry.getValue() == null) {
                    continue;
                }

                ByteArrayOutputStream bitout = new ByteArrayOutputStream();
                ObjectOutputStream objout = new ObjectOutputStream(bitout);
                objout.writeObject(entry.getValue());
                objout.flush();
                byte[] bytes = bitout.toByteArray();
                objout.close();
                bitout.close();

                statement.setString(1, MySQLConfig.getHash(entry.getKey().getKeyString()));
                statement.setString(2, entry.getKey().getKeyString());
                statement.setBytes(3, bytes);
                statement.setBytes(4, bytes);
                statement.addBatch();
            }

            statement.executeBatch();
            statement.close();
            statement = this.connection.prepareStatement("DELETE FROM " + table + " WHERE id = ?");

            for (Entry<Key, Object> entry : this.data.entryIterable()) {
                if (entry.getValue() == null) {
                    statement.setString(1, MySQLConfig.getHash(entry.getKey().getKeyString()));
                    statement.addBatch();
                    this.data.remove(entry.getKey());
                }
            }

            statement.executeBatch();
            statement.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully sent.");
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reload() throws FileFormatException {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
            return;
        }
		if (this.getType() != StorageType.MAP) {
			this.data = new StringHashMap<Object>();
		}
        try {
            this.reconnect();
            this.data.clear();

            UneiConfiguration.getInstance().getLogger().fine("Retreiving SQL data from MySQL database " + this.host + ":" + this.port + "->" + tableName + "...");
            String table = this.tableName; // TODO: Escape table name
            ResultSet result = this.query("SELECT * FROM " + table + "", null);
            while (result.next()) {
                try {
                    String key = result.getString("k");
                    byte[] bytes = result.getBytes("v");

                    InputStream bitin = new ByteArrayInputStream(bytes);
                    ObjectInputStream objin = new ObjectInputStream(bitin);
                    Object value = objin.readObject();
                    objin.close();
                    bitin.close();

                    this.data.set(new Key(key), value);
                } catch (IOException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
                    e.printStackTrace();
                }
            }
            Statement statement = result.getStatement();
            result.close();
            statement.close();
            this.runTreeUpdate();
            UneiConfiguration.getInstance().getLogger().fine("Successfully retreived.");
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not reload MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
            throw new MySQLConnectionException(host, port, tableName, null, e);
        }
    }

    public void reconnect() throws SQLException {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reconnect();
            return;
        }
        UneiConfiguration.getInstance().getLogger().fine("Reconnecting to MySQL file " + this.host + ":" + this.port + "->" + tableName + "...");
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e) {
        }

        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.base, this.user, this.pass);

        PreparedStatement statement = null;
        try {
            String table = this.tableName; // TODO: Escape table name
            statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (id VARCHAR(32) UNIQUE PRIMARY KEY, k LONGTEXT, v LONGBLOB)");
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
        if (this.parent != null) {
            this.parent.close();
            return;
        }
        if (this.connection == null) {
        	return;
        }
        try {
            this.connection.close();
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not close MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
            e.printStackTrace();
        }
        this.connection = null;
    }

	@Override
	public StorageType getType() {
		return (this.data != null) ? this.data.getStorageType() : StorageType.UNDEFINED;
	}
    
    @Override
	protected void updateFromParent() {
		if (this.parent != null && this.parent.data != null) {
			if (this.parent.getData().getStorageType() != StorageType.UNDEFINED) {
				Object me = this.parent.data.get(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName));
				Storage<Object> tmp = StorageConverter.allocateBest(me, null, null);
				if (tmp != null) {
					this.data = tmp;
				} else {
					this.data = new StringHashMap<Object>();
				}
				this.parent.data.set(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName), this.data);
			}
		} else if (this.parent == null) {
			this.data = new StringHashMap<Object>(); // Root must be a string-key map.
		}
	}

	@Override
	public void setType(StorageType type) {
		if (!this.canAccess()) {
			return;
		}
		if (this.parent == null && type != this.getType()) {
			throw new UnsupportedOperationException("Cannot change the type of a NBT root Tag to anything but " + this.getType().name());
		}
		if (type == this.getType()) {
			return;
		}
		throw new NotImplementedException();
	}

	public Set<String> getKeys() {
		if (this.parent == null || !this.data.hasValue(null)) {
			return this.data.getKeys();
		}
		Set<String> keys = this.data.getKeys();
		for (Entry<Key, Object> entry : this.data.entryIterable()) {
			if (entry.getValue() == null) {
				keys.remove(entry.getKey().getKeyString());
			}
		}
		return keys;
	}

	public boolean contains(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		Key key = list.last().getKey(node.getStorageType());
		return node.has(key) && node.get(key) != null;
	}

	public Object get(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		return node.get(list.last().getKey(node.getStorageType()));
	}

    @Override
	public MySQLConfig getSubSection(PathComponent.PathComponentsList path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<MySQLConfig> navigator = new PathNavigator<MySQLConfig>(this);
        if (navigator.followPath(path)) {
            return navigator.getCurrentNode();
        }
        return null;
    }

    public void set(String path, Object value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        if (this.parent != null && value == null) {
			node.remove(list.last().getKey(node.getStorageType()));
        } else {
            // When a root value is removed, keep a null value in the map so that the save knows it has to delete it from the database
            node.set(list.last().getKey(node.getStorageType()), value);
        }
    }

    public void setSubSection(String path, IConfiguration value) {
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			this.remove(path);
		}
        if (!(value instanceof MySQLConfig)) {
            //TODO ConfigType conversion
            return;
        }
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		MySQLConfig node = this.getParentObj(list);
		Key key = list.last().getKey(node.getType());
		((MySQLConfig) value).validate(node, key);
		node.data.set(key, value);
    }

    public void remove(String path) {
        set(path, null);
    }

    @Override
    public String toString() {
        return "MySQLConfig=" + this.data.toString();
    }
}
