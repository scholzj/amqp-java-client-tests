package com.deutscheboerse.amqp.tests;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.internal.BitronixSystemException;
import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.jms.IllegalStateException;
import javax.naming.NamingException;
import javax.transaction.*;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.deutscheboerse.amqp.utils.AutoCloseableConnectionTransactionManager;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;
import com.deutscheboerse.amqp.utils.AutoCloseableXAConnection;
import com.deutscheboerse.amqp.utils.MyXid;

public class XATxn extends BaseTest {
    private static final String TXN_QUEUE = Settings.get("routing.txn_queue");

    // Test the commit feature
    public void testTxnCommit() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

            XAResource resource2 = xaSession2.getXAResource();
            Xid xid2 = MyXid.createRandom();

            int receivedNo = 0;

            resource2.start(xid2, XAResource.TMNOFLAGS);

            Message received = receiver.receive(1000);

            while (received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            resource2.end(xid2, XAResource.TMSUCCESS);
            resource2.prepare(xid2);
            resource2.commit(xid2, false);

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while (received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    public void testTxnCommitUsingTransactionManager() throws JMSException, NamingException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            connection.begin();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            connection.commit();

            connection.begin();
            Session session2 = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            connection.commit();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while (received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    // Test the sender rollback feature
    public void testTxnSenderRollback() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            XAResource resource = xaSession.getXAResource();
            Xid xid = MyXid.createRandom();

            resource.start(xid, XAResource.TMNOFLAGS);

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            resource.end(xid, XAResource.TMSUCCESS);
            resource.prepare(xid);
            resource.rollback(xid);
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after rollback");
        }
    }

    // Test the sender rollback feature
    public void testTxnSenderRollbackUsingTransactionManager() throws JMSException, NamingException, SystemException, NotSupportedException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            // Sender session
            connection.begin();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            connection.rollback();
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after rollback");
        }
    }

    // Test the receiver rollback feature
    public void testTxnReceiverRollback() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

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

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    // Test the receiver rollback feature
    public void testTxnReceiverRollbackUsingTransactionManager() throws JMSException, NamingException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            // Sender session
            connection.begin();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            connection.commit();

            // Receiver session Txn
            connection.begin();
            Session session2 = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            connection.rollback();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    // Test the commit feature with large commit
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 100000;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

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

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    // Test the commit feature with large commit
    public void testTxnCommitLotOfMessagesUsingTransactionManager() throws JMSException, NamingException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        int MESSAGE_COUNT = 100000;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            // Sender session
            connection.begin();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            connection.commit();

            // Receiver session Txn
            connection.begin();
            Session session2 = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(TXN_QUEUE));

            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            connection.commit();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Txn test received unexpected number of messages");
        }

        // Receiver session without txn
        try (AutoCloseableConnection connection2 = this.utils.getAdminConnectionBuilder().build()) {
            connection2.start();
            Session session3 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session3.createConsumer(this.utils.getQueue(TXN_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            Assert.assertEquals(0, receivedNo, "Txn test received unexpected number of messages after commit");
        }
    }

    // Timeout higher than 600 seconds should cause an error
    public void testTxnMaximumTimeout() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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

                Assert.fail("XA should be unable to set timeout to more than 600 seconds");
            } catch (XAException | IllegalStateException expected) {
                // "Expected" exception ... nothing to do :-o
            }
        }
    }

    // Timeout higher than 600 seconds should cause an error
    public void testTxnMaximumTimeoutUsingTransactionManager() throws JMSException, NamingException, SystemException, NotSupportedException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            try {
                // Sender session
                connection.setTransactionTimeout(601);
                connection.begin();
                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    sender.send(session.createMessage());
                }

                connection.rollback();

                Assert.fail("XA should be unable to set timeout to more than 600 seconds");
            } catch (SystemException | IllegalStateException expected) {
                // "Expected" exception ... nothing to do :-o
            }
        }
    }

    public void testTxnBelowMaximumTimeout() throws JMSException, NamingException, XAException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();
            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
        }
    }

    public void testTxnBelowMaximumTimeoutUsingTransactionManager() throws JMSException, SystemException, NotSupportedException, NamingException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();
            // Sender session
            connection.begin();

            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }

            connection.rollback();
        }
    }

    public void testTxnTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        int MESSAGE_COUNT = 10;
        int TIMEOUT = 5; // seconds
        int WAIT_TIME = (TIMEOUT * 2) * 1000; // milliseconds

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
        }
    }

    public void testTxnTimeoutUsingTransactionManager() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, JMSException, NotSupportedException, InterruptedException, NamingException {
        int MESSAGE_COUNT = 10;
        int TIMEOUT = 5; // seconds
        int WAIT_TIME = (TIMEOUT * 2) * 1000; // milliseconds

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            // Sender session
            connection.setTransactionTimeout(TIMEOUT);
            connection.begin();

            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            Thread.sleep(WAIT_TIME);

            try {
                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    sender.send(session.createMessage());
                }
            } catch (JMSException e) {
                connection.rollback();
                if (e.getCause() instanceof BitronixSystemException) {
                    throw new BitronixSystemException(e.getCause().getMessage());
                } else {
                    throw e;
                }
            }
            connection.commit();
        }
    }

    // Tests the default transaction timeout => Takes quite long, because default timeout is 60 seconds.
    public void testTxnDefaultTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        int MESSAGE_COUNT = 10;
        int WAIT_TIME = (60 + 1) * 1000; // milliseconds

        try (AutoCloseableXAConnection connection = this.utils.getAdminConnectionBuilder().buildXA()) {
            connection.start();

            // Sender session
            XASession xaSession = connection.createXASession();
            Session session = xaSession.getSession();
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

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
        }
    }

    // Tests the default transaction timeout => Takes quite long, because default timeout is 60 seconds.
    public void testTxnDefaultTimeoutUsingTransactionManager() throws InterruptedException, JMSException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, NamingException {
        int MESSAGE_COUNT = 10;
        int WAIT_TIME = (60 + 1) * 1000; // milliseconds

        try (AutoCloseableConnectionTransactionManager connection = this.utils.getAdminConnectionBuilder().buildWithTransactionManager()) {
            connection.start();

            // Sender session
            connection.begin();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.utils.getQueue(TXN_QUEUE));

            Thread.sleep(WAIT_TIME);
            try {
                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    sender.send(session.createMessage());
                }
            } catch (JMSException e) {
                connection.rollback();
                if (e.getCause() instanceof BitronixSystemException) {
                    throw new BitronixSystemException(e.getCause().getMessage());
                } else {
                    throw e;
                }
            }
            connection.commit();
        }
    }
}
