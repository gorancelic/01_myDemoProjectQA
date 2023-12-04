package com.myapp.example.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvironmentConfig {
    protected static Logger log;
    private static Properties properties;


    static {
        properties = new Properties();
        try {
            properties.load(EnvironmentConfig.class.getResourceAsStream("/config.properties"));
            //System.out.println("config.properties loaded successfully.");
            log = LogManager.getLogger("Environment");
            log.info("config.properties loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Error loading config.properties: " + e.getMessage());
           // System.err.println("Error loading config.properties: " + e.getMessage());
        }
    }
    /**
    * Retrieves the environment configuration based on the provided parameter.
    * This method determines the environment setting for the application, either from a provided parameter
    * or a default value from the properties file.
    *
    * @param environmentParameter A string parameter representing the environment setting, typically passed from test configurations.
    *
    * Output:
    * - Returns a String representing the environment setting. This could be the provided parameter or a default value.
    */
    public static String getEnvironment( String environmentParameter) {
        if (environmentParameter != null) {
            log = LogManager.getLogger("Environment");
            log.info(environmentParameter);
            return environmentParameter;
        } else {
            log = LogManager.getLogger("Environment");
            log.info(properties.getProperty("default.environment"));
            return properties.getProperty("default.environment");
        }
    }
    /**
    * Retrieves the base URL for the specified environment.
    * This method determines the appropriate base URL for the application based on the environment setting.
    *
    * @param environmentParameter A string parameter representing the environment setting. This parameter
    *                             determines which URL to retrieve from the properties file.
    *
    * Output:
    * - Returns a String representing the base URL for the specified environment.
    */
    public static String getBaseUrl(String environmentParameter) {
        String environment = getEnvironment(environmentParameter);
        log = LogManager.getLogger("Environment");
        log.info("URL: " + properties.getProperty(environment + ".url"));
        return properties.getProperty(environment + ".url");
    }
}