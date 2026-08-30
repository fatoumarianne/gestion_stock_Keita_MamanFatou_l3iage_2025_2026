package com.gestionstock.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JPAUtil {
    private static final EntityManagerFactory factory = creerFactory();

    private static EntityManagerFactory creerFactory() {
        Properties properties = new Properties();
        try(InputStream input = DatabaseConfig.class
                .getClassLoader().getResourceAsStream("config.properties")) {

            if(input == null){
                throw new RuntimeException("Fichier config.properties introuvables dans src/main/resources");
            }
            properties.load(input);

            String url = properties.getProperty("db.url");
            boolean postgres = url.contains("postgresql");

            Map<String, String> overides = new HashMap<>();

            overides.put("jakarta.persistence.jdbc.url", url);
            overides.put("jakarta.persistence.jdbc.user", properties.getProperty("db.username"));
            overides.put("jakarta.persistence.jdbc.password", properties.getProperty("db.password"));

            // Configuration MySQL ou Postgresql
            overides.put("jakarta.persistence.jdbc.driver",
                    postgres ? "org.postgresql.Driver" : "com.mysql.cj.jdbc.Driver");

            // Dialecte Hibernate MySQL ou Postgresql
//            overides.put("hibernate.dialect",
//                    postgres ? "org.hibernate.dialect.PostgreSQLDialect" : "org.hibernate.dialect.MySQLDialect");

            return Persistence.createEntityManagerFactory("GestionStockPU", overides);

        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier config.properties");
        }
    }

    public static EntityManager getEntityManager() {return factory.createEntityManager();}
    public static void fermer() {
        if(factory != null && factory.isOpen())
            factory.close();
    }
}
