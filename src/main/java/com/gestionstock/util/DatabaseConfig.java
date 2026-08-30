package com.gestionstock.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static String url;
    private static String username;
    private static String password;

    static {
        chargerConfiguration();
    }

    private static void chargerConfiguration() {
        Properties properties = new Properties();
        try(InputStream input = DatabaseConfig.class
                .getClassLoader().getResourceAsStream("config.properties")) {

            if(input == null){
                throw new RuntimeException("Fichier config.properties introuvables dans src/main/resources");
            }
            properties.load(input);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier config.properties");
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion: " + e.getMessage());
        }
    }

    public static void testerConnection() {
        try (Connection connection = getConnection()){
            System.out.println("Connexion reussi a: " + connection.getMetaData().getURL());
            System.out.println("SGBD: " + connection.getMetaData().getDatabaseProductName() + " " + connection.getMetaData().getDatabaseProductVersion());
        }catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion: " + e.getMessage());
        }
    }
}
