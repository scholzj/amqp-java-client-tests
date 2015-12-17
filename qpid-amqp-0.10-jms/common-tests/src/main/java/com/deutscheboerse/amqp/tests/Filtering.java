package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.UUID;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Filtering extends BaseTest {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    public void testCorrelationIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            String correlationID = UUID.randomUUID().toString();
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setJMSCorrelationID(correlationID);
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSCorrelationID = '" + UUID.randomUUID().toString() + "'");
            Message notRcvMsg = receiver.receive(1000);
            
            Assert.assertNull(notRcvMsg, "Received unexpected message");
            
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSCorrelationID = '" + correlationID + "'");
            Message rcvMsg = receiver2.receive(1000);
            
            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(correlationID, rcvMsg.getJMSCorrelationID(), "CorrelationID is wrong");
            
            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringWithPeriod() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            String key = UUID.randomUUID().toString();
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filter.test", key);
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "\"filter.test\" = '" + UUID.randomUUID().toString() + "'");
            Message notRcvMsg = receiver.receive(1000);
            
            Assert.assertNull(notRcvMsg, "Received unexpected message");
            
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "\"filter.test\" = '" + key + "'");
            Message rcvMsg = receiver2.receive(1000);
            
            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key, rcvMsg.getStringProperty("filter.test"), "Key is wrong");
            
            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringWithoutPeriod() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            String key = UUID.randomUUID().toString();
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filterTest", key);
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest = '" + UUID.randomUUID().toString() + "'");
            Message notRcvMsg = receiver.receive(1000);
            
            Assert.assertNull(notRcvMsg, "Received unexpected message");
            
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest = '" + key + "'");
            Message rcvMsg = receiver2.receive(1000);
            
            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key, rcvMsg.getStringProperty("filterTest"), "Key is wrong");
            
            rcvMsg.acknowledge();
        }
    }
}