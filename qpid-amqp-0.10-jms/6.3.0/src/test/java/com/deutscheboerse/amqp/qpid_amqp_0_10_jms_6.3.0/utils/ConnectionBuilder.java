package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_0.utils;

import bitronix.tm.resource.jms.PoolingConnectionFactory;
import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AbstractConnectionBuilder;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;
import com.deutscheboerse.amqp.utils.AutoCloseableConnectionTransactionManager;
import com.deutscheboerse.amqp.utils.AutoCloseableXAConnection;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.XAQueueConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class ConnectionBuilder extends AbstractConnectionBuilder {

    public static final String TCP_PORT = Settings.get("broker.tcp_port");
    public static final String SSL_PORT = Settings.get("broker.ssl_port");

    public ConnectionBuilder() {
        super();
    }
    
    protected String url() {
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
        
        return String.format("amqp://%1$s:%2$s@%3$s/?brokerlist='%4$s'%5$s", username, password, clientID, brokerList, connectionOptionsString);
    }
    
    @Override
    public AutoCloseableConnection build() throws NamingException, JMSException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("connectionfactory.connection", url());
        
        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");
        
        return new AutoCloseableConnection(fact.createConnection());
    }
    
    @Override
    public AutoCloseableXAConnection buildXA() throws NamingException, JMSException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("connectionfactory.connection", url());

        InitialContext ctx = new InitialContext(props);
        XAQueueConnectionFactory fact = (XAQueueConnectionFactory) ctx.lookup("connection");
        
        return new AutoCloseableXAConnection(fact.createXAConnection());
    }

    @Override
    public AutoCloseableConnectionTransactionManager buildWithTransactionManager() throws NamingException, JMSException {
        PoolingConnectionFactory connectionFactory = new PoolingConnectionFactory ();
        connectionFactory.setClassName("com.deutscheboerse.amqp.utils.AMQConnectionFactory");
        connectionFactory.setUniqueName("test");
        connectionFactory.setMinPoolSize(1);
        connectionFactory.setMaxPoolSize(2);
        connectionFactory.setAllowLocalTransactions(true);
        connectionFactory.setUser(username);
        connectionFactory.setPassword(password);
        connectionFactory.getDriverProperties().setProperty("brokerUrl", url());
        connectionFactory.init();

        return new AutoCloseableConnectionTransactionManager(connectionFactory);
    }
}
