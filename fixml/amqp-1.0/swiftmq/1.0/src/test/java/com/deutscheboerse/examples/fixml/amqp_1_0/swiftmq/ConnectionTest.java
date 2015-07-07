package com.deutscheboerse.examples.fixml.amqp_1_0.swiftmq;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import com.swiftmq.amqp.AMQPContext;
import com.swiftmq.amqp.v100.client.AMQPException;
import com.swiftmq.amqp.v100.client.AuthenticationException;
import com.swiftmq.amqp.v100.client.Connection;
import com.swiftmq.amqp.v100.client.ConnectionClosedException;
import com.swiftmq.amqp.v100.client.Consumer;
import com.swiftmq.amqp.v100.client.InvalidStateException;
import com.swiftmq.amqp.v100.client.Producer;
import com.swiftmq.amqp.v100.client.QoS;
import com.swiftmq.amqp.v100.client.Session;
import com.swiftmq.amqp.v100.client.SessionHandshakeException;
import com.swiftmq.amqp.v100.client.UnsupportedProtocolVersionException;
import com.swiftmq.amqp.v100.generated.messaging.message_format.AddressString;
import com.swiftmq.amqp.v100.generated.messaging.message_format.AmqpValue;
import com.swiftmq.amqp.v100.generated.messaging.message_format.MessageIdString;
import com.swiftmq.amqp.v100.generated.messaging.message_format.Properties;
import com.swiftmq.amqp.v100.messaging.AMQPMessage;
import com.swiftmq.amqp.v100.types.AMQPString;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionTest
{
    private static final int TIMEOUT = 1000;

    private static Connection connection = null;
    private static Session session = null;
    private static Consumer broadcastConsumer = null;
    private static Producer requestProducer = null;
    private static Consumer responseConsumer = null;

    private static Connection connectionBackend = null;
    private static Session sessionBackend = null;
    private static Consumer requestConsumerBackend = null;
    private static Producer responseProducerBackend = null;
    private static Producer broadcastProducerBackend = null;
    
    private static AMQPMessage receivedRequest = null;
    
    @BeforeClass
    public static void loadProperties()
    {
        System.setProperty("javax.net.ssl.trustStore", PrepareProperties.getTruststore());
        System.setProperty("javax.net.ssl.trustStorePassword", PrepareProperties.getTruststorePassword());
        System.setProperty("javax.net.ssl.keyStore", PrepareProperties.getKeystore());
        System.setProperty("javax.net.ssl.keyStorePassword", PrepareProperties.getKeystorePassword());
    }
    
    @Test
    public void test000CreateConnection()
    {
        AMQPContext ctx = new AMQPContext(AMQPContext.CLIENT);
        connection = new Connection(ctx, PrepareProperties.getHost(), PrepareProperties.getPort(), "", "");
        connection.setMechanism("EXTERNAL");
        connection.setSocketFactory(new MySSLSocketFactory(PrepareProperties.getKeystoreAlias()));
    }
    
    @Test
    public void test005StartConnection() throws ConnectionClosedException, IOException, UnsupportedProtocolVersionException, AuthenticationException
    {
        connection.connect();
    }
    
    @Test
    public void test010CreateSession() throws SessionHandshakeException, ConnectionClosedException
    {
        session = connection.createSession(1000, 1000);
    }
    
    @Test
    public void test015CreateConnectionBackend()
    {
        AMQPContext ctx = new AMQPContext(AMQPContext.CLIENT);
        connectionBackend = new Connection(ctx, PrepareProperties.getHost(), PrepareProperties.getPort() + 10000, "amqpsrv", "amqpsrv");
    }

    @Test
    public void test020StartConnectionBackend() throws ConnectionClosedException, IOException, UnsupportedProtocolVersionException, AuthenticationException
    {
        connectionBackend.connect();
    }
    
    @Test
    public void test025CreateSessionBackend() throws SessionHandshakeException, ConnectionClosedException
    {
        sessionBackend = connectionBackend.createSession(1000, 1000);
    }
    
    @Test
    public void test030CreateBroadcastConsumer() throws AMQPException
    {
        broadcastConsumer = session.createConsumer(PrepareProperties.getBroadcastQueue(), 1000, QoS.AT_LEAST_ONCE, true, null);
    }

    @Test
    public void test035CreateRequestProducer() throws AMQPException
    {
        requestProducer = session.createProducer(PrepareProperties.getRequestExchange(), QoS.AT_LEAST_ONCE);
    }
    
    @Test
    public void test040CreateResponseConsumer() throws AMQPException
    {
        responseConsumer = session.createConsumer(PrepareProperties.getResponseQueue(), 1000, QoS.AT_LEAST_ONCE, true, null);
    }
    
    @Test
    public void test045CreateBroadcastProducerBackend() throws AMQPException
    {
        broadcastProducerBackend = sessionBackend.createProducer(PrepareProperties.getBroadcastExchange(), QoS.AT_LEAST_ONCE);
    }
    
    @Test
    public void test050CreateRequestConsumerBackend() throws AMQPException
    {
        requestConsumerBackend = sessionBackend.createConsumer(PrepareProperties.getRequestQueue(), 1000, QoS.AT_LEAST_ONCE, true, null);
    }

    @Test
    public void test055CreateResponseProducerBackend() throws AMQPException
    {
        responseProducerBackend = sessionBackend.createProducer(PrepareProperties.getResponseExchange(), QoS.AT_LEAST_ONCE);
    }

    @Test
    public void test060SendBroadcastMessageBackend() throws AMQPException
    {
        AMQPMessage message = new AMQPMessage();
        message.setAmqpValue(new AmqpValue(new AMQPString("<FIXML>broadcast message</FIXML>")));
        Properties messageProperties = new Properties();
        messageProperties.setSubject(new AMQPString(PrepareProperties.getBroadcastBinding()));
        messageProperties.setCorrelationId(new MessageIdString(UUID.randomUUID().toString()));
        message.setProperties(messageProperties);
        broadcastProducerBackend.send(message);
        System.out.println("Backend sent broadcast message with ID: " + message.getProperties().getCorrelationId().getValueString());
    }
    
    @Test
    public void test065ConsumeBroadcast() throws InvalidStateException
    {
        AMQPMessage receivedBroadcast = broadcastConsumer.receive(TIMEOUT);
        receivedBroadcast.accept();
        System.out.println("Client received broadcast message with ID: " + receivedBroadcast.getProperties().getCorrelationId().getValueString());
    }
    
    @Test
    public void test070SendRequestMessage() throws AMQPException
    {
        AMQPMessage message = new AMQPMessage();
        message.setAmqpValue(new AmqpValue(new AMQPString("<FIXML>request message</FIXML>")));
        Properties messageProperties = new Properties();
        messageProperties.setCorrelationId(new MessageIdString(UUID.randomUUID().toString()));
        messageProperties.setReplyTo(new AddressString(PrepareProperties.getReplyToAddress()));
        message.setProperties(messageProperties);
        requestProducer.send(message);
        System.out.println("Client sent request message with ID: " + message.getProperties().getCorrelationId().getValueString());
    }

    @Test
    public void test075ConsumeRequestBackend() throws InvalidStateException
    {
        receivedRequest = requestConsumerBackend.receive(TIMEOUT);
        receivedRequest.accept();
        System.out.println("Backend received request message with ID: " + receivedRequest.getProperties().getCorrelationId().getValueString());
    }
    
    @Test
    public void test080CheckReplyToAddress()
    {
        assertTrue(receivedRequest.getProperties().getReplyTo().getValueString().equals(PrepareProperties.getReplyToAddress()));
    }
    
    @Test
    public void test085SendResponseBackend() throws AMQPException
    {
        AMQPMessage message = new AMQPMessage();
        message.setAmqpValue(new AmqpValue(new AMQPString("<FIXML>response message</FIXML>")));
        Properties messageProperties = new Properties();
        messageProperties.setCorrelationId(new MessageIdString(receivedRequest.getProperties().getCorrelationId().getValueString()));
        messageProperties.setSubject(new AMQPString(PrepareProperties.getResponseQueue()));
        message.setProperties(messageProperties);
        responseProducerBackend.send(message);
        System.out.println("Backend sent response message with ID: " + message.getProperties().getCorrelationId().getValueString());
    }
    
    @Test
    public void test090ConsumeResponse() throws InvalidStateException
    {
        AMQPMessage receivedMsg = responseConsumer.receive(TIMEOUT);
        receivedMsg.accept();
        System.out.println("Client received message with ID: " + receivedMsg.getProperties().getCorrelationId().getValueString());
    }
    
    @AfterClass
    public static void close() throws AMQPException
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
