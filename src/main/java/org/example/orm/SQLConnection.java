package org.example.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class SQLConnection {

    private static String url = DataBinder.getUrl();
    private static String user = DataBinder.getUser();
    private static String password = DataBinder.getPassword();

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
