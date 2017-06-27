package me.unei.configuration.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.plugin.UneiConfiguration;

public class SQLiteConfig implements ISQLiteConfiguration {

    public static final String SQLITE_FILE_EXT = ".db";
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";

    private Map<String, Object> data = new HashMap<String, Object>();

    private SavedFile configFile = null;

    private Connection connection = null;
    private String tableName = "_";

    private String fullPath = "";
    private String nodeName = "";
    private SQLiteConfig parent = null;

    public SQLiteConfig(File folder, String fileName, String tableName) {
        this.configFile = new SavedFile(folder, fileName, SQLiteConfig.SQLITE_FILE_EXT);
        this.tableName = tableName;

        this.init();
    }

    private SQLiteConfig(SQLiteConfig p_parent, String p_nodeName) {
        this.parent = p_parent;
        this.nodeName = p_nodeName;
        this.fullPath = SQLiteConfig.buildPath(p_parent.fullPath, p_nodeName);

        this.configFile = this.parent.configFile;
        this.tableName = this.parent.tableName;
        // this.connection = this.parent.connection;

        this.init();
    }

    private void init() {
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
        this.configFile.init();
        this.reload();
    }

    private static String buildPath(String path, String child) {
        if (path == null || path.isEmpty() || child == null) {
            return PathComponent.escapeComponent(child);
        }
        return path + PathNavigator.PATH_SEPARATOR + PathComponent.escapeComponent(child);
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

    public SavedFile getFile() {
        return this.configFile;
    }

    public String getFileName() {
        if (this.parent != null) {
            return this.parent.getFileName();
        }
        return this.configFile.getFileName();
    }

    public String getName() {
        return this.nodeName;
    }

    public String getCurrentPath() {
        return this.fullPath;
    }

    public boolean canAccess() {
        if (this.parent != null) {
            return this.parent.canAccess();
        }
        return this.configFile.canAccess();
    }

    public void lock() {
        if (this.parent != null) {
            this.parent.lock();
        } else {
            this.configFile.lock();
        }
    }

    public SQLiteConfig getRoot() {
        if (this.parent != null) {
            return this.parent.getRoot();
        }
        return this;
    }

    public SQLiteConfig getParent() {
        if (this.parent != null) {
            return this.parent;
        }
        return this;
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
            String table = "\"" + this.tableName.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
            statement = this.connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (`id`, `key`, `value`) VALUES (?, ?, ?)");

            for (Entry<String, Object> entry : this.data.entrySet()) {
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
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save SQLite configuration " + this.getFileName() + ":");
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not save SQLite configuration " + this.getFileName() + ":");
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

            String table = "\"" + this.tableName.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
            ResultSet result = this.query("SELECT * FROM " + table + "", null);
            while (result.next()) {
                try {
                    String key = result.getString("key");
                    byte[] bytes = result.getBytes("value");

                    InputStream bitin = new ByteArrayInputStream(bytes);
                    ObjectInputStream objin = new ObjectInputStream(bitin);
                    Object value = objin.readObject();
                    objin.close();
                    bitin.close();

                    this.data.put(key, value);
                } catch (IOException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + ":");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + ":");
                    e.printStackTrace();
                }
            }
            result.close();
            result.getStatement().close();
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not reload SQLite configuration " + this.getFileName() + ":");
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
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e) {}

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.configFile.getFile().getPath());

        PreparedStatement statement = null;
        try {
            String table = "\"" + this.tableName.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
            statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (`id` VARCHAR(32) UNIQUE PRIMARY KEY, `key` LONGTEXT, `value` LONGBLOB)");
            statement.execute();
            statement.close();
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
            UneiConfiguration.getInstance().getLogger().warning("Could not close SQLite configuration " + this.getFileName() + ":");
            e.printStackTrace();
        }
        this.connection = null;
    }

    @SuppressWarnings("unchecked")
    protected void synchronize() {
        SQLiteConfig currentNode = this.getRoot();
        Map<String, Object> currentData = currentNode.data;

        PathComponent.PathComponentsList path = PathNavigator.parsePath(this.fullPath);
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

    protected void propagate() {
        if (this.parent != null) {
            this.parent.data.put(this.nodeName, this.data);
            this.parent.propagate();
        }
    }

    public Set<String> getKeys() {
        return this.data.keySet();
    }

    public boolean contains(String path) {
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                return this.parent.data.containsKey(this.nodeName);
            } else {
                return true;
            }
        }
        PathNavigator<SQLiteConfig> navigator = new PathNavigator<SQLiteConfig>(this);
        if (navigator.navigate(path)) {
            return navigator.getCurrentNode().contains("");
        }
        return false;
    }

    public Object get(String path) {
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                return this.parent.data.get(this.nodeName);
            } else {
                return this.data;
            }
        }
        PathNavigator<SQLiteConfig> navigator = new PathNavigator<SQLiteConfig>(this);
        if (navigator.navigate(path)) {
            return navigator.getCurrentNode().get("");
        }
        return null;
    }

    public String getString(String path) {
        try {
            return (String) get(path);
        } catch (Exception e) {
            return null;
        }
    }

    public double getDouble(String path) {
        try {
            return ((Number) get(path)).doubleValue();
        } catch (Exception e) {
            return 0.0D;
        }
    }

    public boolean getBoolean(String path) {
        try {
            return ((Boolean) get(path)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public byte getByte(String path) {
        try {
            return ((Number) get(path)).byteValue();
        } catch (Exception e) {
            return (byte) 0;
        }
    }

    public float getFloat(String path) {
        try {
            return ((Number) get(path)).floatValue();
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public int getInteger(String path) {
        try {
            return ((Number) get(path)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String path) {
        try {
            return ((Number) get(path)).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<Byte> getByteList(String path) {
        try {
            List<Byte> list = new ArrayList<Byte>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).byteValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getIntegerList(String path) {
        try {
            List<Integer> list = new ArrayList<Integer>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).intValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public SQLiteConfig getSubSection(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<SQLiteConfig> navigator = new PathNavigator<SQLiteConfig>(this);
        if (navigator.navigate(path)) {
            return navigator.getCurrentNode();
        }
        return null;
    }

    public void set(String path, Object value) {
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                if (value == null) {
                    this.parent.data.remove(this.nodeName);
                } else {
                    this.parent.data.put(this.nodeName, value);
                }
                this.parent.propagate();
            }
            return;
        }
        PathNavigator<SQLiteConfig> navigator = new PathNavigator<SQLiteConfig>(this);
        if (navigator.navigate(path)) {
            navigator.getCurrentNode().set("", value);
        }
    }

    public void setString(String path, String value) {
        set(path, value);
    }

    public void setDouble(String path, double value) {
        set(path, value);
    }

    public void setBoolean(String path, boolean value) {
        set(path, value);
    }

    public void setByte(String path, byte value) {
        set(path, value);
    }

    public void setFloat(String path, float value) {
        set(path, value);
    }

    public void setInteger(String path, int value) {
        set(path, value);
    }

    public void setLong(String path, long value) {
        set(path, value);
    }

    public void setByteList(String path, List<Byte> value) {
        set(path, value);
    }

    public void setIntegerList(String path, List<Integer> value) {
        set(path, value);
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
