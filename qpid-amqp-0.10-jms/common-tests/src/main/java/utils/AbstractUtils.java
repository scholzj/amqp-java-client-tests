package utils;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;

public interface AbstractUtils {
    public AbstractConnectionBuilder getConnectionBuilder();

    public AbstractConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException;

    public AbstractConnectionBuilder getSSLConnectionBuilder();

    public Connection getConnection(String connURL) throws JMSException, NamingException;
    
    public Destination getQueue(String queueName) throws NamingException;

    public Destination getTopic(String topicName, String routingKey) throws NamingException;
    
    public Destination getTopic(String topicName) throws NamingException;
}
