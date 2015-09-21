package com.deutscheboerse.configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by schojak on 21.09.2015.
 */
public class Settings {
    protected static Properties settings = new Properties();

    static {
        try
        {
            settings.load(Settings.class.getResourceAsStream("/settings.properties"));
        }
        catch (IOException e)
        {
            System.err.println("Could not load property file 'settings.properties'");
        }
    }

    public static String get(String property)
    {
        return settings.getProperty(property);
    }
}
