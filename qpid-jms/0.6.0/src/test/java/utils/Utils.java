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

    public static ConnectionBuilder getConnectionBuilder() {
        return new ConnectionBuilder().hostname(HOSTNAME).port(TCP_PORT);
    }

    public static ConnectionBuilder getSSLConnectionBuilder() {
        return new ConnectionBuilder().hostname(HOSTNAME).ssl().port(SSL_PORT).truststore(TRUSTSTORE).truststorePassword(TRUSTSTORE_PASSWORD);
    }

    public static Connection getConnection(String connURL) throws JMSException, NamingException {
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
        private Boolean syncPublish = null;
        private List<String> options = new LinkedList<>();

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

        public ConnectionBuilder option(String connectionOption) {
            this.options.add(connectionOption);
            return this;
        }

        public ConnectionBuilder syncPublish(Boolean syncPublish) {
            this.syncPublish = syncPublish;
            return this;
        }

        private String url() {
            String protocol = "amqp";
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
                protocol = "amqps";

                if (keystore != null)
                {
                    options.add("transport.keyStoreLocation=" + keystore);
                }

                if (keystorePassword != null)
                {
                    options.add("transport.keyStorePassword=" + keystorePassword);
                }

                if (keystoreAlias != null)
                {
                    options.add("transport.keyAlias=" + keystoreAlias);
                }

                if (truststore != null)
                {
                    options.add("transport.trustStoreLocation=" + truststore);
                }

                if (truststorePassword != null)
                {
                    options.add("transport.trustStorePassword=" + truststorePassword);
                }
            }

            if (username != null)
            {
                options.add("jms.username=" + username);
            }

            if (password != null)
            {
                options.add("jms.password=" + password);
            }

            if (clientID != null)
            {
                options.add("jms.clientID=" + clientID);
            }

            if (syncPublish != null)
            {
                if (syncPublish) {
                    options.add("jms.alwaysSyncSend=True");
                }
                else
                {
                    options.add("jms.forceAsyncSend=True");
                }
            }

            if (options.size() > 0)
            {
                StringBuilder sb = new StringBuilder();

                for (String option : options) {
                    sb.append("&");
                    sb.append(option);
                }

                sb.replace(0, 1, "?");
                connectionOptionsString = sb.toString();
            }

            return String.format("%1$s://%2$s:%3$s%4$s", protocol, hostname, port, connectionOptionsString);
        }

        public Connection build() throws NamingException, JMSException {
            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            props.setProperty("connectionfactory.connection", url());

            InitialContext ctx = new InitialContext(props);
            ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

            return fact.createConnection();
        }
    }
}
