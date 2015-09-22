package jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestTxn {
    private static final String TXN_QUEUE = Settings.get("routing.txn_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the commit feature
    @Test
    public void testTxnCommit() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;

        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();

        // Sender session
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.commit();

        // Receiver session with com.deutscheboerse.qpid.jms.Txn
        Session session2 = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session2.commit();

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        // Receiver session without txn
        Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        receiver = session3.createConsumer(Utils.getQueue(TXN_QUEUE));
        receivedNo = 0;

        received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        assertEquals("Txn test received unexpected number of messages after commit", 0, receivedNo);

        connection.close();
    }

    // Test the sender rollback feature
    @Test
    public void testTxnSenderRollback() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;

        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();

        // Sender session
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.rollback();

        // Receiver session without txn
        Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session3.createConsumer(Utils.getQueue(TXN_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        assertEquals("Txn test received unexpected number of messages after rollback", 0, receivedNo);

        connection.close();
    }

    // Test the receiver rollback feature
    @Test
    public void testTxnReceiverRollback() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10;

        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();

        // Sender session
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.commit();

        // Receiver session with Txn
        Session session2 = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            //received.acknowledge();
            received = receiver.receive(1000);
        }

        session2.rollback();
        receiver.close();

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        // Receiver session without txn
        Session session3 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        receiver = session3.createConsumer(Utils.getQueue(TXN_QUEUE));
        receivedNo = 0;

        received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        assertEquals("Txn test received unexpected number of messages after rollback", MESSAGE_COUNT, receivedNo);

        connection.close();
    }

    // Test the commit feature with large commit
    @Test
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException {
        int MESSAGE_COUNT = 100000;

        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();

        // Sender session
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.commit();

        // Receiver session with Txn
        Session session2 = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session2.commit();

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        connection.close();
    }

    // TODO: XA transaction tests ???
}
