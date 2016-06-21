package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Txn extends BaseTest {
    private static final String TXN_QUEUE = Settings.get("routing.txn_queue");
    
    // Test the commit feature
    public void testTxnCommit() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;
        
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            
            // Sender session
            Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));
            
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
            
            session.commit();
            
            // Receiver session with com.deutscheboerse.qpid.Txn
            Session session2 = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;
            
            Message received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }
            
            session2.commit();
            
            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
            
            // Receiver session without txn
            Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            receivedNo = 0;
            
            received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }
            
            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }
    
    // Test the sender rollback feature
    public void testTxnSenderRollback() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;
        
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            
            // Sender session
            Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));
            
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
            
            session.rollback();
            
            // Receiver session without txn
            Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;
            
            Message received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }
            
            Assert.assertEquals(receivedNo, 0, "Txn test received unexpected number of messages after rollback");
        }
    }
    
    // Test the receiver rollback feature
    public void testTxnReceiverRollback() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;
        
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            
            // Sender session
            Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));
            
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
            
            session.commit();
            
            // Receiver session with Txn
            Session session2 = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;
            
            Message received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                //received.acknowledge();
                received = receiver.receive(1000);
            }
            
            session2.rollback();
            receiver.close();
            
            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
            
            // Receiver session without txn
            Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            receivedNo = 0;
            
            received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }
            
            Assert.assertEquals(receivedNo, MESSAGE_COUNT, "Txn test received unexpected number of messages after rollback");
        }
    }
    
    // Test the commit feature with large commit
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException {
        int MESSAGE_COUNT = 100000;
        
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            
            // Sender session
            Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));
            
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
            
            session.commit();
            
            // Receiver session with Txn
            Session session2 = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;
            
            Message received = receiver.receive(1000);
            
            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }
            
            session2.commit();
            
            Assert.assertEquals(receivedNo, MESSAGE_COUNT, "Txn test received unexpected number of messages");
        }
    }
}
