package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import javax.jms.*;
import javax.naming.NamingException;

import java.util.UUID;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class TempQueues extends BaseTest {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    private static final String USER2_USERNAME = Settings.get("user2.username");
    private static final String USER2_PASSWORD = Settings.get("user2.password");

    private static final String RESPONSE_TOPIC = Settings.get("routing.response_topic");
    private static final String RESPONSE_QUEUE_PREFIX = Settings.get("routing.response_queue_prefix");

    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));

            try (AutoCloseableConnection adminConnection = this.utils.getAdminConnectionBuilder().build()) {
                adminConnection.start();
                Session adminSession = adminConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

                MessageProducer producer = adminSession.createProducer(this.utils.getTopic(RESPONSE_TOPIC, queueName));
                producer.send(session.createTextMessage());
            }

            Message received = receiver.receive(1000);
            assertNotNull("Didn't received expected message", received);
        }
    }

    public void testResponseWrongQueueName() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = "response.user2." + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseWrongRoutingKey() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String routingKey = "response.user2." + UUID.randomUUID().toString();
            String queueName = "response.user1." + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + routingKey + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseInvalidPolicy() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': reject, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseNotAutodelete() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: false, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseWrongExchange() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String responseTopic = "broadcast";
            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: false, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + responseTopic + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseTooBigSize() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 10000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseTooSmallSize() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseTooBigCount() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 10000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseTooSmallCount() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseQueueWrongUser() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            Message received = receiver.receive(1000);

        }
    }

    public void testResponseQueueAutodelete() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));

            try (AutoCloseableConnection adminConnection = this.utils.getAdminConnectionBuilder().build()) {
                adminConnection.start();
                Session adminSession = adminConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

                MessageProducer producer = adminSession.createProducer(this.utils.getTopic(RESPONSE_TOPIC, queueName));
                producer.send(session.createTextMessage());
            }

            Message received = receiver.receive(1000);
            assertNotNull("Didn't received expected message", received);

            // Lets close the receiver / session => the queue should be immediattely deleted
            receiver.close();
            session.close();

            // To prove that the queue was deleted, lets see that the message is gone!
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            received = receiver.receive(1000);
            assertNull("Received unexpected message", received);
        }
    }

    public void testResponseQueueAutodeleteTimeout() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000, 'qpid.auto_delete_timeout': 5}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));

            try (AutoCloseableConnection adminConnection = this.utils.getAdminConnectionBuilder().build()) {
                adminConnection.start();
                Session adminSession = adminConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

                MessageProducer producer = adminSession.createProducer(this.utils.getTopic(RESPONSE_TOPIC, queueName));
                producer.send(session.createTextMessage());
            }

            Message received = receiver.receive(1000);
            assertNotNull("Didn't received expected message", received);

            // Lets close the receiver / session => the queue should NOT be immediattely deleted
            receiver.close();
            session.close();

            Thread.sleep(1000);

            // To prove that the queue was not yet deleted, lets see that the message is gone!
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            received = receiver.receive(1000);
            assertNotNull("Didn't received expected message. Was the queue deleted?", received);

            // Lets close the receiver / session again and wait for the autodeletion
            receiver.close();
            session.close();
            Thread.sleep(5000);

            // Now the message should be gone!
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            receiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));
            received = receiver.receive(1000);
            assertNull("Received unexpected message", received);
        }
    }
}
