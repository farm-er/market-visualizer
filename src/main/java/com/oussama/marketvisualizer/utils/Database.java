package com.oussama.marketvisualizer.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    
    // public Database() {
        
    //     String path = getClass().getClassLoader().getResource("database.db").getPath();
    //     String url = "jdbc:sqlite:" + path;

    //     try (Connection conn = DriverManager.getConnection(url)) {
    //         if (conn != null) {
    //             Statement stmt = conn.createStatement();
    //             stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)");
    //             System.out.println("Connection to SQLite has been established.");
    //         }
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }

    // public boolean dataExists() {}

}
