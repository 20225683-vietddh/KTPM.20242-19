package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection {
	private static PostgreSQLConnection instance;
    private Connection connection;

    private PostgreSQLConnection() throws SQLException {
		Properties props = DBConfig.loadProperties();
		String url = props.getProperty("db.url");
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");
		connection = DriverManager.getConnection(url, username, password);
    }

    public static PostgreSQLConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new PostgreSQLConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}