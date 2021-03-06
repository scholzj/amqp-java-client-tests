package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import org.apache.qpid.qmf2.common.QmfException;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class ReadOnly extends BaseTest {
    private static final String RO_QUEUE = Settings.get("routing.read_only_queue");

    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    // Test the read only queue feature
    public void testReadOnlyQueue() throws JMSException, NamingException, QmfException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnection senderConnection = this.utils.getAdminConnectionBuilder().build()) {
            senderConnection.start();

            // Sender session
            Session session = senderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RO_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
        }

        try (AutoCloseableConnection receiverConnection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).setAsyncAcks(false).build()) {
            receiverConnection.start();

            // First receiver
            Session session2 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(RO_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            session2.close();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Read Only queue test received unexpected number of messages in first run");

            // Second Receiver
            Session session3 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            receiver = session3.createConsumer(this.utils.getQueue(RO_QUEUE));
            receivedNo = 0;

            received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            session3.close();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Read Only queue test received unexpected number of messages in second run");
        }
    }

    // Test the read only queue feature with transaction reader
    public void testReadOnlyQueueWithTxn() throws JMSException, NamingException, QmfException {
        int MESSAGE_COUNT = 10;

        try (AutoCloseableConnection senderConnection = this.utils.getAdminConnectionBuilder().build()) {
            senderConnection.start();

            // Sender session
            Session session = senderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(RO_QUEUE));

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                sender.send(session.createMessage());
            }
        }

        try (AutoCloseableConnection receiverConnection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).setAsyncAcks(false).build()) {
            receiverConnection.start();

            // First receiver
            Session session2 = receiverConnection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(RO_QUEUE));
            int receivedNo = 0;

            Message received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            session2.commit();

            session2.close();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Read Only queue test received unexpected number of messages in first run");

            // Second Receiver
            Session session3 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            receiver = session3.createConsumer(this.utils.getQueue(RO_QUEUE));
            receivedNo = 0;

            received = receiver.receive(1000);

            while(received != null) {
                receivedNo++;
                received.acknowledge();
                received = receiver.receive(1000);
            }

            session3.close();

            Assert.assertEquals(MESSAGE_COUNT, receivedNo, "Read Only queue test received unexpected number of messages in second run");
        }
    }
}
