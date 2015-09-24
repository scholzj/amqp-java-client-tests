package utils;

import com.deutscheboerse.configuration.Settings;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.LinkedList;
import java.util.List;
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

    public static ConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException {
        return getConnectionBuilder().username(ADMIN_USERNAME).password(ADMIN_PASSWORD);
    }

    public static ConnectionBuilder getConnectionBuilder() throws JMSException, NamingException {
        return new ConnectionBuilder().hostname(HOSTNAME).port(TCP_PORT);
    }

    public static ConnectionBuilder getSSLConnectionBuilder() throws JMSException, NamingException {
        return new ConnectionBuilder().hostname(HOSTNAME).ssl().port(SSL_PORT).truststore(TRUSTSTORE).truststorePassword(TRUSTSTORE_PASSWORD);
    }

    public static Connection getConnection(String connURL) throws JMSException, NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

        return fact.createConnection();
    }

    public static Destination getQueue(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.queue", queueName + "; { node: { type: queue }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("queue");
    }

    public static Destination getTopic(String topicName, String routingKey) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.queue", topicName + "/" + routingKey + "; { node: { type: topic }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("queue");
    }

    public static Destination getTopic(String topicName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.topic", topicName + "; { node: { type: topic }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("topic");
    }

    public static class ConnectionBuilder {
        private String hostname = HOSTNAME;
        private String port = null;
        private String clientID;
        private String username;
        private String password;
        private String keystore;
        private String keystorePassword;
        private String keystoreAlias;
        private String truststore;
        private String truststorePassword;
        private Boolean ssl = false;
        private List<String> connectionOptions = new LinkedList<>();
        private List<String> brokerOptions = new LinkedList<>();

        public ConnectionBuilder() {
        }

        public ConnectionBuilder ssl() {
            this.ssl = true;
            return this;
        }

        public ConnectionBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public ConnectionBuilder port(String port) {
            this.port = port;
            return this;
        }

        public ConnectionBuilder clientID(String clientID) {
            this.clientID = clientID;
            return this;
        }

        public ConnectionBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ConnectionBuilder password(String password) {
            this.password = password;
            return this;
        }

        public ConnectionBuilder keystore(String keystore) {
            this.keystore = keystore;
            return this;
        }

        public ConnectionBuilder keystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
            return this;
        }

        public ConnectionBuilder keystoreAlias(String keystoreAlias) {
            this.keystoreAlias = keystoreAlias;
            return this;
        }

        public ConnectionBuilder truststore(String truststore) {
            this.truststore = truststore;
            return this;
        }

        public ConnectionBuilder truststorePassword(String truststorePassword) {
            this.truststorePassword = truststorePassword;
            return this;
        }

        public ConnectionBuilder connectionOption(String connectionOption) {
            this.connectionOptions.add(connectionOption);
            return this;
        }

        public ConnectionBuilder brokerOption(String brokerOption) {
            this.brokerOptions.add(brokerOption);
            return this;
        }

        private String url() {
            String brokerOptionsString = "";
            String connectionOptionsString = "";

            if (port == null && ssl)
            {
                port = SSL_PORT;
            }
            else if (port == null)
            {
                this.port = TCP_PORT;
            }

            if (ssl)
            {
                brokerOptions.add("ssl='true'");

                if (keystore != null)
                {
                    brokerOptions.add("key_store='" + keystore + "'");
                }

                if (keystorePassword != null)
                {
                    brokerOptions.add("key_store_password='" + keystorePassword + "'");
                }

                if (keystoreAlias != null)
                {
                    brokerOptions.add("ssl_cert_alias='" + keystoreAlias + "'");
                }

                if (truststore != null)
                {
                    brokerOptions.add("trust_store='" + truststore + "'");
                }

                if (truststorePassword != null)
                {
                    brokerOptions.add("trust_store_password='" + truststorePassword + "'");
                }
            }

            if (brokerOptions.size() > 0)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("?");

                for (String option : brokerOptions) {
                    if (sb.length() > 1) { sb.append("&"); }
                    sb.append(option);
                }

                brokerOptionsString = sb.toString();
            }

            if (connectionOptions.size() > 0)
            {
                StringBuilder sb = new StringBuilder();

                for (String option : connectionOptions) {
                    sb.append("&");
                    sb.append(option);
                }

                connectionOptionsString = sb.toString();
            }

            String brokerList = String.format("tcp://%1$s:%2$s%3$s", hostname, port, brokerOptionsString);
            String connURL = String.format("amqp://%1$s:%2$s@%3$s/?brokerlist='%4$s'%5$s", username, password, clientID, brokerList, connectionOptionsString);

            return connURL;
        }

        public Connection build() throws NamingException, JMSException {
            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
            props.setProperty("connectionfactory.connection", url());

            InitialContext ctx = new InitialContext(props);
            ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

            return fact.createConnection();
        }

        public XAConnection buildXA() throws NamingException, JMSException {
            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
            props.setProperty("connectionfactory.connection", url());

            InitialContext ctx = new InitialContext(props);
            XAQueueConnectionFactory fact = (XAQueueConnectionFactory) ctx.lookup("connection");

            return fact.createXAConnection();
        }
    }

}
