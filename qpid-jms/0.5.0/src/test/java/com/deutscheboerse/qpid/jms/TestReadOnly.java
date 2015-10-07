package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import com.deutscheboerse.utils.GlobalUtils;
import org.apache.qpid.qmf2.common.QmfException;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestReadOnly {
    private static final String RO_QUEUE = Settings.get("routing.read_only_queue");

    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the read only queue feature
    @Test
    public void testReadOnlyQueue() throws JMSException, NamingException, QmfException {
        int MESSAGE_COUNT = 10;

        // Clean the queue first
        GlobalUtils.purgeQueue(RO_QUEUE);

        Connection senderConnection = Utils.getAdminConnection();
        senderConnection.start();

        // Sender session
        Session session = senderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RO_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.close();
        senderConnection.close();

        Connection receiverConnection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "jms.sendAcksAsync=False");
        receiverConnection.start();

        // First receiver
        Session session2 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(RO_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session2.close();

        assertEquals("Read Only queue test received unexpected number of messages in first run", MESSAGE_COUNT, receivedNo);

        // Second Receiver
        Session session3 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        receiver = session3.createConsumer(Utils.getQueue(RO_QUEUE));
        receivedNo = 0;

        received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session3.close();

        assertEquals("Read Only queue test received unexpected number of messages in second run", MESSAGE_COUNT, receivedNo);

        receiverConnection.close();
    }

    // Test the read only queue feature with transaction reader
    @Test
    public void testReadOnlyQueueWithTxn() throws JMSException, NamingException, QmfException {
        int MESSAGE_COUNT = 10;

        // Clean the queue first
        GlobalUtils.purgeQueue(RO_QUEUE);

        Connection senderConnection = Utils.getAdminConnection();
        senderConnection.start();

        // Sender session
        Session session = senderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RO_QUEUE));

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sender.send(session.createMessage());
        }

        session.close();
        senderConnection.close();

        Connection receiverConnection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "jms.sendAcksAsync=False");
        receiverConnection.start();

        // First receiver
        Session session2 = receiverConnection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(RO_QUEUE));
        int receivedNo = 0;

        Message received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session2.commit();

        session2.close();

        assertEquals("Read Only queue test received unexpected number of messages in first run", MESSAGE_COUNT, receivedNo);

        // Second Receiver
        Session session3 = receiverConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        receiver = session3.createConsumer(Utils.getQueue(RO_QUEUE));
        receivedNo = 0;

        received = receiver.receive(1000);

        while(received != null) {
            receivedNo++;
            received.acknowledge();
            received = receiver.receive(1000);
        }

        session3.close();

        assertEquals("Read Only queue test received unexpected number of messages in second run", MESSAGE_COUNT, receivedNo);

        receiverConnection.close();
    }
}
