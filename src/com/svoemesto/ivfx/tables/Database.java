package com.svoemesto.ivfx.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
//    public static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/ivfxdb?allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC&useSSL=false";
//    public static final String CONNECTION_USER = "root";
//    public static final String CONNECTION_PASSWORD = "12345";

    public static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/ivfxdb?allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC&useSSL=false";
//    public static final String CONNECTION_URL = "jdbc:mysql://rc1a-umdzojywzn5qji6c.mdb.yandexcloud.net:3306/ivfxdb?useSSL=true";
    public static final String CONNECTION_USER = "smtec";
    public static final String CONNECTION_PASSWORD = "SMtec-30082012";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
