package utils;

import com.deutscheboerse.configuration.Settings;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by schojak on 02.09.2015.
 */
public class Utils {
    private static final String HOSTNAME = Settings.get("broker.hostname");
    private static final String TCP_PORT = Settings.get("broker.tcp_port");
    private static final String SSL_PORT = Settings.get("broker.ssl_port");
    private static final String TRUSTSTORE = Settings.getPath("broker.truststore");
    private static final String TRUSTSTORE_PASSWORD = Settings.get("broker.truststore_password");
    private static final String ADMIN_USERNAME = Settings.get("admin.username");
    private static final String ADMIN_PASSWORD = Settings.get("admin.password");

    public static Connection getAdminConnection() throws JMSException, NamingException {
        return getConnection(ADMIN_USERNAME, ADMIN_PASSWORD, "");
    }

    public static Connection getAdminConnection(String options) throws JMSException, NamingException {
        return getConnection(ADMIN_USERNAME, ADMIN_PASSWORD, options);
    }

    public static Connection getConnection(String username, String password) throws JMSException, NamingException {
        return getConnection(username, password, "");
    }

    public static Connection getConnection(String username, String password, String options) throws JMSException, NamingException {
        return getConnection(HOSTNAME, TCP_PORT, username, password, options);
    }

    private static Connection getConnection(String hostname, String port, String username, String password, String options) throws JMSException, NamingException {
        if (options == null)
        {
            options = "";
        }

        if (options.length() > 0 && !options.startsWith("&"))
        {
            options = "&" + options;
        }

        String connURL = String.format("amqp://%1$s:%2$s?jms.username=%3$s&jms.password=%4$s", hostname, port, username, password);
        connURL += options;

        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

        return fact.createConnection();
    }

    public static Connection getConnection(String connURL) throws JMSException, NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

        return fact.createConnection();
    }

    public static Connection getSSLConnection(String keystoreFile, String keystorePassword, String keyAlias) throws JMSException, NamingException {
        return getSSLConnection(keystoreFile, keystorePassword, keyAlias, TRUSTSTORE, TRUSTSTORE_PASSWORD);
    }

    public static Connection getSSLConnection(String keystoreFile, String keystorePassword, String keyAlias, String truststoreFile, String truststorePassword) throws JMSException, NamingException {
        return getSSLConnection(keystoreFile, keystorePassword, keyAlias, truststoreFile, truststorePassword, "");
    }

    public static Connection getSSLConnection(String keystoreFile, String keystorePassword, String keyAlias, String truststoreFile, String truststorePassword, String options) throws JMSException, NamingException {
        return getSSLConnection(HOSTNAME, SSL_PORT, keystoreFile, keystorePassword, keyAlias, truststoreFile, truststorePassword, options);
    }

    public static Connection getSSLConnection(String hostname, String port, String keystoreFile, String keystorePassword, String keyAlias, String truststoreFile, String truststorePassword, String options) throws JMSException, NamingException {
        if (options == null)
        {
            options = "";
        }

        if (options.length() > 0 && !options.startsWith("&"))
        {
            options = "&" + options;
        }

        String connURL = String.format("amqps://%1$s:%2$s?transport.keyStoreLocation=%3$s&transport.keyStorePassword=%4$s&transport.keyAlias=%5$s&transport.trustStoreLocation=%6$s&transport.trustStorePassword=%7$s", hostname, port, keystoreFile, keystorePassword, keyAlias, truststoreFile, truststorePassword);
        connURL += options;

        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

        return fact.createConnection();
    }

    public static Queue getQueue(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("queue.queue", queueName);

        InitialContext ctx = new InitialContext(props);

        return (Queue)ctx.lookup("queue");
    }

    public static Topic getTopic(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("topic.topic", queueName);

        InitialContext ctx = new InitialContext(props);

        return (Topic)ctx.lookup("topic");
    }
}
