package com.deutscheboerse.amqp.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by schojak on 21.09.2015.
 */
public class Settings {
    private final static Properties settings = new Properties();
    private static String resourceDirectory;

    static {
        try
        {
            settings.load(Settings.class.getResourceAsStream("/settings.properties"));
            resourceDirectory = new File(Settings.class.getResource("/settings.properties").getFile()).getParent();
//            resourceDirectory = Paths.get(Settings.class.getResource("/settings.properties").toURI()).toFile().getParentFile().getAbsolutePath().replace("\\", "/");
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

    public static String getPath(String property)
    {
        return resourceDirectory + "/" + settings.getProperty(property);
    }
}
