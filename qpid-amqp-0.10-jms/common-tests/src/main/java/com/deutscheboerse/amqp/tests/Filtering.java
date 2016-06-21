package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class Filtering extends BaseTest {
    private static final String RTG_TOPIC = Settings.get("routing.rtg_topic");
    private static final String RTG_ROUTING_KEY = Settings.get("routing.rtg_routing_key");
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    private static final String OTHER_ROUTING_KEY = Settings.get("routing.dlq_routing_key");

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

    public void testPropertiesFilteringIn() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String key = "blue";

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filterTest", key);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest in ('pink', 'violet', 'smurfy')");
            Message notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest in ('green', 'red', 'blue')");
            Message rcvMsg = receiver2.receive(1000);

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key, rcvMsg.getStringProperty("filterTest"), "Key is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringBetween() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            int key = 13;

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setIntProperty("filterTest", key);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest between 5 and 10");
            Message notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest between 11 and 15");
            Message rcvMsg = receiver2.receive(1000);

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key, rcvMsg.getIntProperty("filterTest"), "Key is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringLike() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String key = "darkbluesky";

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filterTest", key);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest like '%red%'");
            Message notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message for '%red%'");

            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest like 'blue%'");
            notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message for 'blue%'");

            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest like '%blue'");
            notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message for '%blue'");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest like '%blue%'");
            Message rcvMsg = receiver2.receive(1000);

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key, rcvMsg.getStringProperty("filterTest"), "Key is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringAnd() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String key1 = "blue";
            String key2 = "red";

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filterTest1", key1);
            msg.setStringProperty("filterTest2", key2);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'red' AND filterTest2 = 'blue'");
            Message notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message - red & blue");

            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'blue' AND filterTest2 = 'blue'");
            notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message - blue & blue");

            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'red' AND filterTest2 = 'red'");
            notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message - red & red");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'blue' AND filterTest2 = 'red'");
            Message rcvMsg = receiver2.receive(1000);

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message");
            Assert.assertEquals(key1, rcvMsg.getStringProperty("filterTest1"), "Key1 is wrong");
            Assert.assertEquals(key2, rcvMsg.getStringProperty("filterTest2"), "Key2 is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testPropertiesFilteringOr() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String key1 = "blue";
            String key2 = "red";

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setStringProperty("filterTest1", key1);
            msg.setStringProperty("filterTest2", key2);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'red' or filterTest2 = 'blue'");
            Message notRcvMsg = receiver.receive(1000);

            Assert.assertNull(notRcvMsg, "Received unexpected message - red & blue");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'blue' or filterTest2 = 'blue'");
            Message rcvMsg = receiver2.receive(1000);
            receiver2.close();
            session.close(); // In the AMQP 0-10 client, the session has to be closed to release the messages

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message - blue & blue");
            Assert.assertEquals(key1, rcvMsg.getStringProperty("filterTest1"), "Key1 is wrong");
            Assert.assertEquals(key2, rcvMsg.getStringProperty("filterTest2"), "Key2 is wrong");

            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'red' or filterTest2 = 'red'");
            rcvMsg = receiver2.receive(1000);
            receiver2.close();
            session.close(); // In the AMQP 0-10 client, the session has to be closed to release the messages

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message - red & red");
            Assert.assertEquals(key1, rcvMsg.getStringProperty("filterTest1"), "Key1 is wrong");
            Assert.assertEquals(key2, rcvMsg.getStringProperty("filterTest2"), "Key2 is wrong");

            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "filterTest1 = 'blue' or filterTest2 = 'red'");
            rcvMsg = receiver2.receive(1000);

            Assert.assertNotNull(rcvMsg, "Didn't receive expected message - blue & red");
            Assert.assertEquals(key1, rcvMsg.getStringProperty("filterTest1"), "Key1 is wrong");
            Assert.assertEquals(key2, rcvMsg.getStringProperty("filterTest2"), "Key2 is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testMessageIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            String messageID = msg.getJMSMessageID();

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSMessageID = '" + UUID.randomUUID().toString() + "'");
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSMessageID = '" + messageID + "'");
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");
            assertEquals(messageID, rcvMsg.getJMSMessageID(), "MessageID is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testMessageIDFilteringJMSStyleJavaBroker() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            String messageID = msg.getJMSMessageID();
            String JMSMessageID = messageID.substring(3);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSMessageID = '" + UUID.randomUUID().toString() + "'");
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSMessageID = '" + JMSMessageID + "'");
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");
            assertEquals(messageID, rcvMsg.getJMSMessageID(), "MessageID is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testPriorityFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            sender.setPriority(9);
            Message msg = session.createMessage();
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSPriority < 5");
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSPriority > 5");
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");
            assertEquals(9, rcvMsg.getJMSPriority(), "JMSPriority is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testJMSTypeFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            msg.setJMSType("my.subject.key");
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSType = 'other.subject.key'");
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSType = 'my.subject.key'");
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");
            assertEquals("my.subject.key", rcvMsg.getJMSType(), "JMSPriority is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testRoutingKeyFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC, RTG_ROUTING_KEY));
            Message msg = session.createMessage();
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "\"qpid.subject\" = '" + OTHER_ROUTING_KEY + "'");
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message");

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "\"qpid.subject\" = '" + RTG_ROUTING_KEY + "'");
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");
            assertEquals(RTG_ROUTING_KEY, rcvMsg.getStringProperty("qpid.subject"), "Routing key is wrong");

            rcvMsg.acknowledge();
        }
    }

    public void testTimestampFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date yesterday = cal.getTime();

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = cal.getTime();

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSTimestamp > " + tomorrow.getTime()/1000);
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message for time " + tomorrow.getTime()/1000);

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSTimestamp > " + yesterday.getTime()/1000);
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");

            rcvMsg.acknowledge();
        }
    }

    public void testTimestampFilteringJMSStyleJavaBroker() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date yesterday = cal.getTime();

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = cal.getTime();

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSTimestamp > " + tomorrow.getTime());
            Message notRcvMsg = receiver.receive(1000);

            assertNull(notRcvMsg, "Received unexpected message for time " + tomorrow.getTime());

            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE), "JMSTimestamp > " + yesterday.getTime());
            Message rcvMsg = receiver2.receive(1000);

            assertNotNull(rcvMsg, "Didn't receive expected message");

            rcvMsg.acknowledge();
        }
    }
}
