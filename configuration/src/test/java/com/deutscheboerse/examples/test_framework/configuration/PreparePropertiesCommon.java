package com.deutscheboerse.examples.test_framework.configuration;

import java.io.IOException;
import java.util.Properties;

public class PreparePropertiesCommon
{
    protected static Properties userProperties = new Properties();
    
    static {
        try
        {
            userProperties.load(PreparePropertiesCommon.class.getResourceAsStream("/test.properties"));
        }
        catch (IOException e)
        {
            System.err.println("Could not load property file 'test.properties'");
        }
    }
    
    public static String getReplyToAddress()
    {
        return userProperties.getProperty("responseExchange") + "/" + userProperties.getProperty("responseQueue");
    }   
}
