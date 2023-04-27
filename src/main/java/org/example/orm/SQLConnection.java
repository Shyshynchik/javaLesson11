package org.example.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class SQLConnection {

    private final static String url = DataBinder.getUrl();
    private final static String user = DataBinder.getUser();
    private final static String password = DataBinder.getPassword();

    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
