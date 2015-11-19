package com.deutscheboerse.amqp.configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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
            resourceDirectory = Paths.get(Settings.class.getResource("/settings.properties").toURI()).toFile().getParentFile().getAbsolutePath().replace("\\", "/");
        }
        catch (IOException e)
        {
            System.err.println("Could not load property file 'settings.properties'");
        }
        catch (URISyntaxException e)
        {
            System.err.println("Could not get absolute path to resources directory");
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
