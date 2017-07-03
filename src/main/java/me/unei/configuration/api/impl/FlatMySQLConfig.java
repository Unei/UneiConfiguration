package me.unei.configuration.api.impl;

import me.unei.configuration.SavedFile;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.IFlatMySQLConfiguration;
import me.unei.configuration.api.UntypedFlatStorage;
import me.unei.configuration.plugin.UneiConfiguration;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class FlatMySQLConfig extends UntypedFlatStorage<FlatMySQLConfig> implements IFlatMySQLConfiguration {

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private Map<String, Object> data = new HashMap<String, Object>();

    private String host;
    private int port;
    private String base;
    private String user;
    private String pass;
    private Connection connection = null;
    private String tableName = "_";

    public FlatMySQLConfig(String host, int port, String base, String user, String pass, String tableName) {
        super(new SavedFile());
        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.pass = pass;
        this.tableName = tableName;

        this.subinit();
    }

    private void subinit() {
        try {
            Class.forName(FlatMySQLConfig.MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not load MySQL driver " + FlatMySQLConfig.MYSQL_DRIVER + ":");
            e.printStackTrace();
            return;
        }
        this.file.init();
        this.reload();
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

    public boolean execute(String query, Map<Integer, Object> parameters) throws SQLException {
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
        PreparedStatement statement = null;
        try {
            UneiConfiguration.getInstance().getLogger().fine("Sending SQL data to MySQL file " + this.host + ":" + this.port + "->" + tableName + "...");
            String table = this.tableName; // TODO: Escape table name
            statement = this.connection.prepareStatement("INSERT INTO " + table + " (id, k, v) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE v = ?");

            for (Entry<String, Object> entry : this.data.entrySet()) {
                statement.setString(1, FlatMySQLConfig.getHash(entry.getKey()));
                statement.setString(2, entry.getKey());
                statement.setString(3, SerializerHelper.toJSONString(entry.getValue()));
                statement.setString(4, SerializerHelper.toJSONString(entry.getValue()));
                statement.addBatch();
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

    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        try {
            this.reconnect();
            this.data.clear();

            UneiConfiguration.getInstance().getLogger().fine("Retreiving SQL data from MySQL file " + this.host + ":" + this.port + "->" + tableName + "...");
            String table = this.tableName; // TODO: Escape table name
            ResultSet result = this.query("SELECT * FROM " + table + "", null);
            while (result.next()) {
                try {
                    String key = result.getString("k");
                    Object value = SerializerHelper.parseJSON(result.getString("v"));

                    this.data.put(key, value);
                } catch (IOException e) {
                    UneiConfiguration.getInstance().getLogger().warning("Could not reload MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
                    e.printStackTrace();
                }
            }
            Statement statement = result.getStatement();
            result.close();
            statement.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully retreived.");
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not reload MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
            e.printStackTrace();
        }
    }

    public void reconnect() throws SQLException {
        if (!this.canAccess()) {
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
            statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (id VARCHAR(32) UNIQUE PRIMARY KEY, k LONGTEXT, v LONGTEXT)");
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
        try {
            this.connection.close();
        } catch (SQLException e) {
            UneiConfiguration.getInstance().getLogger().warning("Could not close MySQL configuration " + this.host + ":" + this.port + "->" + tableName + ":");
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

    public Object get(String key) {
        return data.get(key);
    }

    public void set(String key, Object value) {
    	if (!this.canAccess()) {
    		return;
    	}
        if (value == null) {
            data.remove(key);
        } else {
        	if (value instanceof Double) {
        		if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
        			data.put(key, value.toString());
        		} else {
        			data.put(key, value);
        		}
        	} else if (value instanceof Float) {
        		if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
        			data.put(key, value.toString());
        		} else {
        			data.put(key, value);
        		}
        	} else {
        		data.put(key, value);
        	}
        }
    }

    public void remove(String key) {
        set(key, null);
    }

    @Override
    public String toString() {
        return "FlatMySQLConfig=" + this.data.toString();
    }
}
