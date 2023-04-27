package org.example.orm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;

import java.io.File;

class DataBinder {

    private final static String url = setUrl();

    static String getUrl() {
        return url;
    }

    static String getUser() {
        return "postgres";
    }

    static String getPassword() {
        return "changeme";
    }

    @SneakyThrows
    private static String setUrl() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        File file = new File(classLoader.getResource("dbconnection.yaml").getFile());

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        DbConnectionProperties test = om.readValue(file, DbConnectionProperties.class);

        return "jdbc:postgresql://localhost:15432/jcourse";
    }

}
