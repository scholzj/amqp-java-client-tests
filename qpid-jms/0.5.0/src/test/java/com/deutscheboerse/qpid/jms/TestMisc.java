package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestMisc {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the sender rollback feature
    @Test
    public void testDuplicateClientID() throws JMSException, NamingException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "jms.clientID=myTestClient");
        connection.start();
        Connection connection2 = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "jms.clientID=myTestClient");
        connection2.start();

        connection.close();
        connection2.close();
    }
}
