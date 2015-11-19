package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Misc extends BaseTest {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    
    // Test the sender rollback feature
    public void testDuplicateClientID() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build();
                AutoCloseableConnection connection2 = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build()) {
            connection.start();
            connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection2.start();
            connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        }
    }
    
    public void testMessageIDFormatUUID() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().syncPublish(false).brokerOption("jms.messageIDType=UUID").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't receive expected message");
            Assert.assertEquals(msg.getJMSMessageID(), received.getJMSMessageID(), "The Message IDs are different");
        }
    }
    
    public void testMessageIDFormatUUIDString() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().syncPublish(false).brokerOption("jms.messageIDType=UUID_STRING").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't receive expected message");
            Assert.assertEquals(msg.getJMSMessageID(), received.getJMSMessageID(), "The Message IDs are different");
        }
    }
}
