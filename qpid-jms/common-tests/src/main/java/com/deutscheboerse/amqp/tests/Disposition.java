package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AbstractUtils;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.Assert;

import javax.jms.*;
import javax.naming.NamingException;

public class Disposition extends BaseTest {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @Override
    public void prepare(AbstractUtils utils) {
        //System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid.jms.provider.amqp.FRAMES", "trace");
        super.prepare(utils);
    }

    public void testAcceptDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            // Acknowledge the message
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't receive expected message");

            // JMS_AMQP_ACK_TYPE=1 ... Accept
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 1);
            received.acknowledge();

            receiver.close();

            // Is the queue really empty?
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNull(received, "Received unexpected message");
            receiver2.close();
            session.close();
        }
    }

    // Note: Rejected messages are discarded by the Qpid broker
    public void testRejectDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);

            // Acknowledge the message
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");

            // JMS_AMQP_ACK_TYPE=2 ... Reject
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 2);
            received.acknowledge();

            receiver.close();

            // Is the rejected message really gone?
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNull(received, "Received unexpected message");
            receiver2.close();
            session.close();
        }
    }

    public void testReleasedDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            session.close();

            // Acknowledge the message
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");
            Assert.assertEquals(received.getJMSRedelivered(), false, "First receiver should not see the message as redelivered");

            // JMS_AMQP_ACK_TYPE=3 ... Released
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 3);
            received.acknowledge();

            // Was the release messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message again");
            Assert.assertEquals(received.getJMSRedelivered(), false, "Released message is set as redelivered on the same receiver");

            receiver.close();
            session.close();

            // Is the released message still there
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            Assert.assertEquals(received.getJMSRedelivered(), true, "Released message is set as redelivered on a new receiver");
            receiver2.close();
            session.close();
        }
    }

    public void testModifiedFailedDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            session.close();

            // Acknowledge the message
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");

            // JMS_AMQP_ACK_TYPE=4 ... Failed
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 4);
            received.acknowledge();

            // Was the modified messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received modified message again");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on the same receiver");

            receiver.close();
            session.close();

            // Is the modified message still there
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on a new receiver");
            receiver2.close();
            session.close();
        }
    }

    public void testModifiedUndeliverableDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            session.close();

            // Acknowledge the message
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");

            // JMS_AMQP_ACK_TYPE=5 ... Undeliverable
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 5);
            received.acknowledge();

            // I don't think the message should be delivered again - to be fixed in MRG-M 3.3 / Qpid 0.36
            // Was the modified messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received modified message again");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on the same receiver");

            receiver.close();
            session.close();

            // Is the modified message still there
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on a new receiver");
            receiver2.close();
            session.close();
        }
    }

    /*
    This is the updated version of the previous test case, which reflects the fix in MRG-M 3.3 / Qpid 0.36
     */
    public void testModifiedUndeliverableDispositionFixed() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            session.close();

            // Acknowledge the message
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");

            // JMS_AMQP_ACK_TYPE=5 ... Undeliverable
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 5);
            received.acknowledge();

            // Was the modified messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNull(received, "Received modified message again");

            receiver.close();
            session.close();

            // Is the modified message still there
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            receiver2.close();
            session.close();
        }
    }

    public void testBlockAcceptDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            Message msg2 = session.createMessage();
            sender.send(msg2);
            Message msg3 = session.createMessage();
            sender.send(msg3);
            Message msg4 = session.createMessage();
            sender.send(msg4);
            Message msg5 = session.createMessage();
            sender.send(msg5);

            // Receive the messages
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            int messageCount = 0;
            Message lastReceived = null;
            Message received = receiver.receive(1000);

            while (received != null)
            {
                // JMS_AMQP_ACK_TYPE=1 ... Accept
                received.setIntProperty("JMS_AMQP_ACK_TYPE", 1);
                lastReceived = received;
                messageCount++;

                received = receiver.receive(1000);
            }

            Assert.assertEquals(5, messageCount, "Didn't received expected number of message");

            if (lastReceived != null)
            {
                lastReceived.acknowledge();
            }

            receiver.close();

            // Is the queue really empty?
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNull(received, "Received unexpected message");
            receiver2.close();
            session.close();
        }
    }

    public void testBlockRejectDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            Message msg2 = session.createMessage();
            sender.send(msg2);
            Message msg3 = session.createMessage();
            sender.send(msg3);
            Message msg4 = session.createMessage();
            sender.send(msg4);
            Message msg5 = session.createMessage();
            sender.send(msg5);

            // Receive the messages
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            int messageCount = 0;
            Message lastReceived = null;
            Message received = receiver.receive(1000);

            while (received != null)
            {
                // JMS_AMQP_ACK_TYPE=2 ... Reject
                received.setIntProperty("JMS_AMQP_ACK_TYPE", 2);
                lastReceived = received;
                messageCount++;

                received = receiver.receive(1000);
            }

            Assert.assertEquals(5, messageCount, "Didn't received expected number of message");

            if (lastReceived != null)
            {
                lastReceived.acknowledge();
            }

            receiver.close();

            // Is the queue really empty?
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver2.receive(1000);
            Assert.assertNull(received, "Received unexpected message");
            receiver2.close();
            session.close();
        }
    }

    public void testBlockReleaseDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            Message msg2 = session.createMessage();
            sender.send(msg2);
            Message msg3 = session.createMessage();
            sender.send(msg3);
            Message msg4 = session.createMessage();
            sender.send(msg4);
            Message msg5 = session.createMessage();
            sender.send(msg5);

            // Receive the messages
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            int messageCount = 0;
            Message lastReceived = null;
            Message received = receiver.receive(1000);

            while (received != null)
            {
                // JMS_AMQP_ACK_TYPE=3 ... Released
                received.setIntProperty("JMS_AMQP_ACK_TYPE", 3);
                lastReceived = received;
                messageCount++;

                received = receiver.receive(1000);
            }

            Assert.assertEquals(5, messageCount, "Didn't received expected number of message");

            if (lastReceived != null)
            {
                lastReceived.acknowledge();
            }

            receiver.close();

            // Are the messages still in the queue?
            MessageConsumer receiver2 = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            messageCount = 0;
            received = receiver2.receive(1000);

            while (received != null)
            {
                messageCount++;
                received.acknowledge();

                received = receiver2.receive(1000);
            }

            Assert.assertEquals(5, messageCount, "Didn't received expected number of previously released message");

            receiver2.close();
            session.close();
        }
    }
}
