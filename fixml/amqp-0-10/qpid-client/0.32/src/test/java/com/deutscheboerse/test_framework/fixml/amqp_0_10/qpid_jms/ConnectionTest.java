package com.deutscheboerse.test_framework.fixml.amqp_0_10.qpid_jms;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionTest
{
    private static final int TIMEOUT = 1000;

    private static InitialContext ctx = null;
    private static Connection connection = null;
    private static Session session = null;
    private static MessageConsumer broadcastConsumer = null;
    private static MessageProducer requestProducer = null;
    private static MessageConsumer responseConsumer = null;

    private static InitialContext ctxBackend = null;
    private static Connection connectionBackend = null;
    private static Session sessionBackend = null;
    private static MessageConsumer requestConsumerBackend = null;
    private static MessageProducer responseProducerBackend = null;
    private static MessageProducer broadcastProducerBackend = null;
    
    private static Message receivedRequest = null;
    
    private static Properties propertiesClient = new Properties();
    private static Properties propertiesBackend = new Properties();
    
    @BeforeClass
    public static void loadProperties() throws IOException, NamingException
    {
        propertiesClient = PrepareProperties.getProperties();
        ctx = new InitialContext(propertiesClient);
        propertiesBackend = PrepareProperties.getPropertiesBackend();
        ctxBackend = new InitialContext(propertiesBackend);
    }
    
    @Test
    public void test000CreateConnection() throws JMSException, NamingException
    {
        connection = ((ConnectionFactory) ctx.lookup("connection")).createConnection();
    }
    
    @Test
    public void test005CreateSession() throws JMSException
    {
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
    }
    
    @Test
    public void test010CreateConnectionBackend() throws JMSException, NamingException
    {
        connectionBackend = ((ConnectionFactory) ctxBackend.lookup("connection")).createConnection();
    }
    
    @Test
    public void test015CreateSessionBackend() throws JMSException
    {
        sessionBackend = connectionBackend.createSession(false, Session.CLIENT_ACKNOWLEDGE);
    }
    
    @Test
    public void test020CreateBroadcastConsumer() throws JMSException, NamingException
    {
        broadcastConsumer = session.createConsumer((Destination) ctx.lookup("broadcastQueue"));
    }

    @Test
    public void test025CreateRequestProducer() throws JMSException, NamingException
    {
        requestProducer = session.createProducer((Destination) ctx.lookup("requestExchange"));
    }
    
    @Test
    public void test030CreateResponseConsumer() throws JMSException, NamingException
    {
        responseConsumer = session.createConsumer((Destination) ctx.lookup("responseQueue"));
    }
    
    @Test
    public void test035CreateBroadcastProducerBackend() throws JMSException, NamingException
    {
        broadcastProducerBackend = sessionBackend.createProducer((Destination) ctxBackend.lookup("broadcastExchange"));
    }
    
    @Test
    public void test040CreateRequestConsumerBackend() throws JMSException, NamingException
    {
        requestConsumerBackend = sessionBackend.createConsumer((Destination) ctxBackend.lookup("requestQueue"));
    }

    @Test
    public void test045CreateResponseProducerBackend() throws JMSException, NamingException
    {
        responseProducerBackend = sessionBackend.createProducer((Destination) ctxBackend.lookup("responseExchange"));
    }
    
    @Test
    public void test050StartConnection() throws JMSException
    {
        connection.start();
    }
    
    @Test
    public void test055StartConnectionBackend() throws JMSException
    {
        connectionBackend.start();
    }
    
    @Test
    public void test060SendBroadcastMessageBackend() throws JMSException, NamingException
    {
        TextMessage message = sessionBackend.createTextMessage("<FIXML>broadcast message</FIXML>");
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        broadcastProducerBackend.send(message);
        System.out.println("Backend sent broadcast message with ID: " + message.getJMSCorrelationID());
    }
    
    @Test
    public void test065ConsumeBroadcast() throws JMSException, NamingException
    {
        Message receivedBroadcast = broadcastConsumer.receive(TIMEOUT);
        receivedBroadcast.acknowledge();
        System.out.println("Client received broadcast message with ID: " + receivedBroadcast.getJMSCorrelationID());
    }
    
    @Test
    public void test070SendRequestMessage() throws JMSException, NamingException
    {
        TextMessage message = session.createTextMessage("<FIXML>request message</FIXML>");
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        message.setJMSReplyTo((Destination) ctx.lookup("replyAddress"));
        requestProducer.send(message);
        System.out.println("Client sent request message with ID: " + message.getJMSCorrelationID());
    }

    @Test
    public void test075ConsumeRequestBackend() throws JMSException, NamingException
    {
        receivedRequest = requestConsumerBackend.receive(TIMEOUT);
        receivedRequest.acknowledge();
        System.out.println("Backend received request message with ID: " + receivedRequest.getJMSCorrelationID());
    }
    
    @Test
    public void test080CheckReplyToAddress() throws JMSException, NamingException
    {
        assertTrue(receivedRequest.getJMSReplyTo().toString().equals(PrepareProperties.getReplyToAddressWithQuotes()));
    }
    
    @Test
    public void test085SendResponseBackend() throws JMSException, NamingException
    {
        TextMessage message = sessionBackend.createTextMessage("<FIXML>response message</FIXML>");
        message.setJMSCorrelationID(receivedRequest.getJMSCorrelationID());
        responseProducerBackend.send(message);
        System.out.println("Backend sent response message with ID: " + message.getJMSCorrelationID());
    }
    
    @Test
    public void test090ConsumeResponse() throws JMSException, NamingException
    {
        Message receivedMsg = responseConsumer.receive(TIMEOUT);
        receivedMsg.acknowledge();
        System.out.println("Client received message with ID: " + receivedMsg.getJMSCorrelationID());
    }
    
    @AfterClass
    public static void close() throws JMSException
    {
        if (requestProducer != null)
        {
            requestProducer.close();
        }
        if (responseConsumer != null)
        {
            responseConsumer.close();
        }
        if (broadcastConsumer != null)
        {
            broadcastConsumer.close();
        }
        if (session != null)
        {
            session.close();
        }
        if (connection != null)
        {
            connection.close();
        }
        
        if (broadcastProducerBackend != null)
        {
            broadcastProducerBackend.close();
        }
        if (responseProducerBackend != null)
        {
            responseProducerBackend.close();
        }
        if (requestConsumerBackend != null)
        {
            requestConsumerBackend.close();
        }
        if (sessionBackend != null)
        {
            sessionBackend.close();
        }    
        if (connectionBackend != null)
        {
            connectionBackend.close();
        }
    }
    
}
