package me.unei.configuration.api;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface ISQLConfiguration extends IConfiguration, Closeable {

    public void reconnect() throws SQLException;

    public boolean execute(String query, Map<Integer, Object> parameters) throws SQLException;
    public ResultSet query(String query, Map<Integer, Object> parameters) throws SQLException;
    public int update(String query, Map<Integer, Object> parameters) throws SQLException;
    public long largeUpdate(String query, Map<Integer, Object> parameters) throws SQLException;
}