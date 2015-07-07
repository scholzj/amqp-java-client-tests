package com.deutscheboerse.examples.fixml.amqp_1_0.swiftmq;

import com.deutscheboerse.examples.test_framework.configuration.PreparePropertiesCommon;

public class PrepareProperties extends PreparePropertiesCommon
{
    public static String getTruststore()
    {
        return userProperties.getProperty("truststorePath");
    }
    
    public static String getTruststorePassword()
    {
        return userProperties.getProperty("truststorePass");
    }
    
    public static String getKeystore()
    {
        return userProperties.getProperty("keystorePath");
    }
    
    public static String getKeystorePassword()
    {
        return userProperties.getProperty("keystorePass");
    }
    
    public static String getKeystoreAlias()
    {
        return userProperties.getProperty("keystoreAlias");
    }
    
    public static String getHost()
    {
        return userProperties.getProperty("host");
    }
    
    public static int getPort()
    {
        return Integer.parseInt(userProperties.getProperty("port"));
    }
    
    public static String getBroadcastQueue()
    {
        return userProperties.getProperty("broadcastQueue");
    }
    
    public static String getRequestExchange()
    {
        return userProperties.getProperty("requestExchange");
    }
    
    public static String getResponseQueue()
    {
        return userProperties.getProperty("responseQueue");
    }
    
    public static String getBroadcastExchange()
    {
        return userProperties.getProperty("broadcastExchange");
    }
    
    public static String getBroadcastBinding()
    {
        return userProperties.getProperty("broadcastBinding");
    }
    
    public static String getRequestQueue()
    {
        return userProperties.getProperty("requestQueue");
    }
    
    public static String getResponseExchange()
    {
        return userProperties.getProperty("responseExchange");
    }
}
