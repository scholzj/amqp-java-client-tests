package jms;

import com.deutscheboerse.configuration.Settings;
import com.deutscheboerse.utils.GlobalUtils;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.Assert;
import utils.AbstractUtils;
import utils.AutoCloseableConnection;

public class Disposition extends BaseTest {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    @Override
    public void prepare(AbstractUtils utils) {
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid.jms.provider.amqp.FRAMES", "trace");
        super.prepare(utils);
    }
    
    public void testAcceptDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // Clean the queue first
            GlobalUtils.purgeQueue(RTG_QUEUE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            // Acknowledge the message
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");
            
            // JMS_AMQP_ACK_TYPE=1 ... Accept
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 1);
            received.acknowledge();
            
            receiver.close();
            
            // Is the queue really empty?
            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver.receive(1000);
            Assert.assertNull(received, "Received unexpected message");
        }
    }
    
    // TODO: Rejected messages are discarded by the Qpid broker
    /*
    public void testRejectDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // Clean the queue first
            GlobalUtils.purgeQueue(RTG_QUEUE);
            
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
            
            // Is the rejected message still there
            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received rejected message");
        }
    }*/
    
    public void testReleasedDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("amqp.traceFrames=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // Clean the queue first
            GlobalUtils.purgeQueue(RTG_QUEUE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            // Acknowledge the message
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");
            
            // JMS_AMQP_ACK_TYPE=3 ... Released
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 3);
            received.acknowledge();
            
            // Was the release messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message again");
            // TODO: Released messages are set as redelivered
            //assertEquals("Released message is set as redelivered on a new receiver", false, received.getJMSRedelivered());
            
            receiver.close();
            
            // Is the released message still there
            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            // TODO: Released messages are set as redelivered
            //assertEquals("Released message is set as redelivered on a new receiver", false, received.getJMSRedelivered());
        }
    }
    
    public void testModifiedFailedDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // Clean the queue first
            GlobalUtils.purgeQueue(RTG_QUEUE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            // Acknowledge the message
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
            
            // Is the modified message still there
            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on a new receiver");
        }
    }
    
    public void testModifiedUndeliverableDisposition() throws JMSException, NamingException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // Clean the queue first
            GlobalUtils.purgeQueue(RTG_QUEUE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RTG_QUEUE));
            Message msg = session.createMessage();
            sender.send(msg);
            
            // Acknowledge the message
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received expected message");
            
            // JMS_AMQP_ACK_TYPE=5 ... Undeliveryble
            received.setIntProperty("JMS_AMQP_ACK_TYPE", 5);
            received.acknowledge();
            
            // TODO: I don't think the message should be redelivered
            // Was the modified messages passed again to the same receiver?
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received modified message again");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on the same receiver");
            
            receiver.close();
            
            // Is the modified message still there
            receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            received = receiver.receive(1000);
            Assert.assertNotNull(received, "Didn't received released message");
            Assert.assertEquals(true, received.getJMSRedelivered(), "Modified message is not set as redelivered on a new receiver");
        }
    }
    
}
