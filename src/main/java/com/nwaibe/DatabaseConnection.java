package com.nwaibe;

import java.sql.Connection;
import java.sql.DriverManager;



public class DatabaseConnection {
    private String url = "jdbc:mysql://localhost:3306/ACTIVITY_FIVE_NWAIBE";

    private String user = "admin";

    private String password = "admin";
    private Connection connection;

    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection successful!");
        } catch (Exception e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }



    }
    public Connection getConnection(){
        return connection;
    }




}
