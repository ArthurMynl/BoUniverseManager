package main.java.com.ardian.bouniversemanager.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class AppConfig {
    private static AppConfig instance;
    private Properties properties;

    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());

    private AppConfig() {
        properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {  
            properties.load(input);
        } catch (IOException e) {
            LOGGER.severe("Error loading application.properties: " + e.getMessage());
        }
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            LOGGER.info("Creating new instance of AppConfig");
            instance = new AppConfig();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
