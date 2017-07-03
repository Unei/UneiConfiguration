package me.unei.configuration.api.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.ISQLiteConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.plugin.UneiConfiguration;

public final class SQLiteConfig extends UntypedStorage<SQLiteConfig> implements ISQLiteConfiguration {

    public static final String SQLITE_FILE_EXT = ".db";
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";

    private Map<String, Object> data = new HashMap<String, Object>();

    private Connection connection = null;
    private String tableName = "_";

    public SQLiteConfig(File folder, String fileName, String tableName) {
        this(folder, fileName, tableName, PathSymbolsType.BUKKIT);
    }

    public SQLiteConfig(File folder, String fileName, String tableName, PathSymbolsType symType) {
        super(new SavedFile(folder, fileName, SQLiteConfig.SQLITE_FILE_EXT), symType);
        this.tableName = tableName;

        this.subinit();
    }

    private SQLiteConfig(SQLiteConfig p_parent, String p_nodeName) {
        super(p_parent, p_nodeName);

        this.tableName = this.parent.tableName;
        // this.connection = this.parent.connection;

        this.synchronize();
    }

    private void subinit() {
        if (this.parent != null) {
            this.parent.init();
            this.synchronize();
            return;
        }
        try {
            Class.forName(SQLiteConfig.SQLITE_DRIVER);
        } catch (ClassNotFoundException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not load SQLite driver " + SQLiteConfig.SQLITE_DRIVER + ":");
            e.printStackTrace();
            return;
        }
        this.file.init();
        this.reload();
    }

    public static SQLiteConfig getForPath(File folder, String fileName, String tableName, String path, PathSymbolsType symType) {
        return SQLiteConfig.getForPath(new SQLiteConfig(folder, fileName, tableName, symType), path);
    }

    public static SQLiteConfig getForPath(File folder, String fileName, String tableName, String path) {
        return SQLiteConfig.getForPath(new SQLiteConfig(folder, fileName, tableName), path);
    }

    public static SQLiteConfig getForPath(SQLiteConfig root, String path) {
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
	public SQLiteConfig getRoot() {
        return (SQLiteConfig) super.getRoot();
    }

    public SQLiteConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new SQLiteConfig(this, name);
    }

    private Map<String, Object> getParentMap(PathComponent.PathComponentsList path) {
        SQLiteConfig dir;
        PathNavigator<SQLiteConfig> pn = new PathNavigator<SQLiteConfig>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            return new HashMap<String, Object>(data);
        }
        dir = pn.getCurrentNode();
        return new HashMap<String, Object>(dir.data);
    }

    private void setParentMap(PathComponent.PathComponentsList path, Map<String, Object> map) {
        SQLiteConfig dir;
        PathNavigator<SQLiteConfig> pn = new PathNavigator<SQLiteConfig>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            this.data = map;
            this.propagate();
            return;
        }
        dir = pn.getCurrentNode();
        if (dir != null) {
            dir.data = map;
            dir.propagate();
        }
    }

    public boolean execute(String query, Map<Integer, Object> parameters) throws SQLException {
        if (this.parent != null) {
            return this.parent.execute(query, parameters);
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
        PreparedStatement statement = null;
        try {
            UneiConfiguration.getInstance().getLogger().fine("Writing SQL data to SQLite file " + getFileName() + "->" + tableName + "...");
            String table = this.tableName; // TODO: Escape table name
            statement = this.connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, k, v) VALUES (?, ?, ?)");

            for (Entry<String, Object> entry : this.data.entrySet()) {
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

                statement.setString(1, SQLiteConfig.getHash(entry.getKey()));
                statement.setString(2, entry.getKey());
                statement.setBytes(3, bytes);
                statement.addBatch();
            }

            statement.executeBatch();
            statement.close();
            statement = this.connection.prepareStatement("DELETE FROM " + table + " WHERE id = ?");

            for (Entry<String, Object> entry : this.data.entrySet()) {
                if (entry.getValue() == null) {
                    statement.setString(1, SQLiteConfig.getHash(entry.getKey()));
                    statement.addBatch();
                    this.data.remove(entry.getKey());
                }
            }

            statement.executeBatch();
            statement.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save SQLite configuration " + this.getFileName() + "->" + tableName + ":");
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save SQLite configuration " + this.getFileName() + "->" + tableName + ":");
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

    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
            this.synchronize();
            return;
        }
        try {
            this.reconnect();
            this.data.clear();

            UneiConfiguration.getInstance().getLogger().fine("Reading SQL data from SQLite file " + getFileName() + "->" + tableName + "...");
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

                    this.data.put(key, value);
                } catch (IOException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + "->" + tableName + ":");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + "->" + tableName + ":");
                    e.printStackTrace();
                }
            }
            Statement statement = result.getStatement();
            result.close();
            statement.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + "->" + tableName + ":");
            e.printStackTrace();
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
        UneiConfiguration.getInstance().getLogger().fine("Reconnecting to SQLite file " + getFileName() + "->" + tableName + "...");
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
        try {
            this.connection.close();
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not close SQLite configuration " + this.getFileName() + "->" + tableName + ":");
            e.printStackTrace();
        }
        this.connection = null;
    }

    //@Override
	@SuppressWarnings("unchecked")
    protected void synchronize() {
        SQLiteConfig currentNode = this.getRoot();
        Map<String, Object> currentData = currentNode.data;

        PathComponent.PathComponentsList path = fullPath.clone();
        path = PathNavigator.cleanPath(path);
        for (PathComponent component : path) {
            switch(component.getType()) {
                case ROOT:
                    currentNode = currentNode.getRoot();
                    currentData = currentNode.data;
                    break;

                case PARENT:
                    currentNode = currentNode.getParent();
                    currentData = currentNode.data;
                    break;

                case CHILD:
                    currentNode = null;
                    Object childData = currentData.get(component.getValue());
                    if (childData != null && childData instanceof Map) {
                        currentData = (Map<String, Object>) childData;
                    } else {
                        return;
                    }
                    break;
            }
        }
        this.data = currentData;
    }

    @Override
	protected void propagate() {
        if (this.parent != null) {
            this.parent.data.put(this.nodeName, this.data);
            this.parent.propagate();
        }
    }

    public Set<String> getKeys() {
        if (this.parent == null || !this.data.containsValue(null)) {
            return this.data.keySet();
        }
        Set<String> keys = this.data.keySet();
        for (Entry<String, Object> entry : this.data.entrySet()) {
            if (entry.getValue() == null) {
                keys.remove(entry.getKey());
            }
        }
        return keys;
    }

    public boolean contains(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        return node.containsKey(list.lastChild()) && node.get(list.lastChild()) != null;
    }

    public Object get(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        return node.get(list.lastChild());
    }

    @Override
	public SQLiteConfig getSubSection(PathComponent.PathComponentsList path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<SQLiteConfig> navigator = new PathNavigator<SQLiteConfig>(this);
        if (navigator.followPath(path)) {
            return navigator.getCurrentNode();
        }
        return null;
    }

    public void set(String path, Object value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        if (this.parent != null && value == null) {
             node.remove(list.lastChild());
        } else {
            // When a root value is removed, keep a null value in the map so that the save knows it has to delete it from the database
            node.put(list.lastChild(), value);
        }
        this.setParentMap(list, node);
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!(value instanceof SQLiteConfig)) {
            //TODO ConfigType conversion
            return;
        }
        set(path, ((SQLiteConfig) value).data);
    }

    public void remove(String path) {
        set(path, null);
    }

    @Override
    public String toString() {
        return "SQLiteConfig=" + this.data.toString();
    }
}