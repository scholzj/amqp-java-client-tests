package jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;
import utils.MyXid;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.naming.NamingException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestXATxn {
    private static final String TXN_QUEUE = Settings.get("routing.txn_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the commit feature
    @Test
    public void testTxnCommit() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.commit(xid, false);

        // Receiver session Txn
        XASession xaSession2 = connection.createXASession();
        Session session2 = xaSession2.getSession();
        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));

        XAResource resource2 = xaSession2.getXAResource();
        Xid xid2 = MyXid.createRandom();

        int receivedNo = 0;

        resource2.start(xid2, XAResource.TMNOFLAGS);

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        resource2.end(xid2, XAResource.TMSUCCESS);
        resource2.prepare(xid2);
        resource2.commit(xid2, false);

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        // Receiver session without txn
        Connection connection2 = Utils.getAdminConnectionBuilder().build();
        connection2.start();
        Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

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
        connection2.close();
    }

    // Test the sender rollback feature
    @Test
    public void testTxnSenderRollback() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.rollback(xid);

        // Receiver session without txn
        Connection connection2 = Utils.getAdminConnectionBuilder().build();
        connection2.start();
        Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        assertEquals("Txn test received unexpected number of messages after rollback", 0, receivedNo);

        connection.close();
        connection2.close();
    }

    // Test the receiver rollback feature
    @Test
    public void testTxnReceiverRollback() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.commit(xid, false);

        sender.close();
        session.close();
        xaSession.close();

        // Receiver session Txn
        XASession xaSession2 = connection.createXASession();
        Session session2 = xaSession2.getSession();
        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));

        XAResource resource2 = xaSession2.getXAResource();
        Xid xid2 = MyXid.createRandom();

        int receivedNo = 0;

        resource2.start(xid2, XAResource.TMNOFLAGS);

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        resource2.end(xid2, XAResource.TMSUCCESS);
        resource2.prepare(xid2);
        resource2.rollback(xid2);

        receiver.close();

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        // Receiver session without txn
        Connection connection2 = Utils.getAdminConnectionBuilder().build();
        connection2.start();
        Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        receiver = session3.createConsumer(Utils.getQueue(TXN_QUEUE));
        receivedNo = 0;

        received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        assertEquals("Txn test received unexpected number of messages after commit", MESSAGE_COUNT, receivedNo);

        connection.close();
        connection2.close();
    }

    // Test the commit feature with large commit
    @Test
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 100000;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.commit(xid, false);

        // Receiver session Txn
        XASession xaSession2 = connection.createXASession();
        Session session2 = xaSession2.getSession();
        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(TXN_QUEUE));

        XAResource resource2 = xaSession2.getXAResource();
        Xid xid2 = MyXid.createRandom();

        int receivedNo = 0;

        resource2.start(xid2, XAResource.TMNOFLAGS);

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        resource2.end(xid2, XAResource.TMSUCCESS);
        resource2.prepare(xid2);
        resource2.commit(xid2, false);

        assertEquals("Txn test received unexpected number of messages", MESSAGE_COUNT, receivedNo);

        // Receiver session without txn
        Connection connection2 = Utils.getAdminConnectionBuilder().build();
        connection2.start();
        Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

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
        connection2.close();
    }

    // Timeout higher than 600 seconds should cause an error
    @Test
    public void testTxnMaximumTimeout() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        try {
            XAResource resource = xaSession.getXAResource();
            resource.setTransactionTimeout(601);
            Xid xid = MyXid.createRandom();

            resource.start(xid, XAResource.TMNOFLAGS);

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            resource.end(xid, XAResource.TMSUCCESS);
            resource.prepare(xid);
            resource.rollback(xid);

            fail("XA shoudl be unable to set timeout to more than 600 seconds1");
        }
        catch (XAException e)
        {
            // pass
        }
        catch (IllegalStateException e)
        {
            // pass
        }

        connection.close();
    }

    @Test
    public void testTxnBelowMaximumTimeout() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        resource.setTransactionTimeout(600);
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.rollback(xid);

        connection.close();
    }

    @Test(expected = XAException.class)
    public void testTxnTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        int MESSAGE_COUNT = 10;
        int TIMEOUT = 5; // seconds
        int WAIT_TIME = (TIMEOUT * + 1) * 1000; // milliseconds

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        resource.setTransactionTimeout(TIMEOUT);
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        Thread.sleep(WAIT_TIME);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.commit(xid, false);

        connection.close();
    }

    // Tests the default transaction timeout => Takes quite long, because defualt timeout is 60 seconds.
    @Test(expected = XAException.class)
    public void testTxnDefaultTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        int MESSAGE_COUNT = 10;
        int WAIT_TIME = (60 + 1) * 1000; // milliseconds

        XAConnection connection = Utils.getAdminConnectionBuilder().buildXA();
        connection.start();

        // Sender session
        XASession xaSession = connection.createXASession();
        Session session = xaSession.getSession();
        MessageProducer sender = session.createProducer(Utils.getQueue(TXN_QUEUE));

        XAResource resource = xaSession.getXAResource();
        Xid xid = MyXid.createRandom();

        resource.start(xid, XAResource.TMNOFLAGS);

        Thread.sleep(WAIT_TIME);

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        resource.end(xid, XAResource.TMSUCCESS);
        resource.prepare(xid);
        resource.commit(xid, false);

        connection.close();
    }
}
