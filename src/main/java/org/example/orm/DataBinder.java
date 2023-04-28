package org.example.orm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

class DataBinder {

    static {
        File file = new File("src/main/resources/dbconnection.yaml");

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        DbConnectionProperties dbConnectionProperties = null;
        try {
            dbConnectionProperties = om.readValue(file, DbConnectionProperties.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        url = dbConnectionProperties.getUrl();
        user = dbConnectionProperties.getUser();
        password = dbConnectionProperties.getPassword();
    }

    private final static String url;
    private final static String user;
    private final static String password;

    static String getUrl() {
        return url;
    }

    static String getUser() {
        return user;
    }

    static String getPassword() {
        return password;
    }

}
