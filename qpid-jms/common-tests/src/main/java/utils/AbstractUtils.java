package utils;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.NamingException;

public interface AbstractUtils {
    public AbstractConnectionBuilder getConnectionBuilder();

    public AbstractConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException;

    public AbstractConnectionBuilder getSSLConnectionBuilder();

    public AutoCloseableConnection getConnection(String connURL) throws JMSException, NamingException;
    
    public Queue getQueue(String queueName) throws NamingException;

    public Topic getTopic(String topicName) throws NamingException;
}
