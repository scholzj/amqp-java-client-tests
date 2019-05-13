package com.deutscheboerse.amqp.qpid_jms_0_42_0.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AbstractConnectionBuilder;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectionBuilder extends AbstractConnectionBuilder {

    public static final String TCP_PORT = Settings.get("broker.tcp_port");
    public static final String SSL_PORT = Settings.get("broker.ssl_port");

    public ConnectionBuilder() {
        super();
    }

    protected String url() {
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
                brokerOptions.add("transport.keyStoreLocation=" + keystore);
            }

            if (keystorePassword != null)
            {
                brokerOptions.add("transport.keyStorePassword=" + keystorePassword);
            }

            if (keystoreAlias != null)
            {
                brokerOptions.add("transport.keyAlias=" + keystoreAlias);
            }

            if (truststore != null)
            {
                brokerOptions.add("transport.trustStoreLocation=" + truststore);
            }

            if (truststorePassword != null)
            {
                brokerOptions.add("transport.trustStorePassword=" + truststorePassword);
            }
        }

        if (username != null)
        {
            brokerOptions.add("jms.username=" + username);
        }

        if (password != null)
        {
            brokerOptions.add("jms.password=" + password);
        }

        if (clientID != null)
        {
            brokerOptions.add("jms.clientID=" + clientID);
        }

        if (syncPublish != null)
        {
            if (syncPublish) {
                brokerOptions.add("jms.forceSyncSend=True");
            }
            else
            {
                brokerOptions.add("jms.forceAsyncSend=True");
            }
        }

        if (brokerOptions.size() > 0)
        {
            StringBuilder sb = new StringBuilder();

            for (String option : brokerOptions) {
                sb.append("&");
                sb.append(option);
            }

            sb.replace(0, 1, "?");
            connectionOptionsString = sb.toString();
        }

        return String.format("%1$s://%2$s:%3$s%4$s", protocol, hostname, port, connectionOptionsString);

    }

    @Override
    public AbstractConnectionBuilder setAsyncAcks(boolean async) {
        brokerOption("jms.forceAsyncAcks=" + async);
        return this;
    }

    @Override
    public AutoCloseableConnection build() throws NamingException, JMSException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.connection", url());

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");
        return new AutoCloseableConnection(fact.createConnection());
    }

}
