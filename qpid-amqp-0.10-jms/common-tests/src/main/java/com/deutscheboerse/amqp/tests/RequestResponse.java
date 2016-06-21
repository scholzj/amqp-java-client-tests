package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.UUID;
import org.testng.Assert;

public class RequestResponse extends BaseTest {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    private static final String REQUEST_TOPIC = Settings.get("routing.request_topic");
    private static final String REQUEST_ROUTING_KEY = Settings.get("routing.request_routing_key");
    private static final String REQUEST_QUEUE = Settings.get("routing.request_queue");

    private static final String RESPONSE_TOPIC = Settings.get("routing.response_topic");
    private static final String RESPONSE_QUEUE_PREFIX = Settings.get("routing.response_queue_prefix");

    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            String queueName = RESPONSE_QUEUE_PREFIX + UUID.randomUUID().toString();
            String responseQueueAddress = queueName + "; {create: receiver, assert: never, node: { type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, x-bindings: [{exchange: '" + RESPONSE_TOPIC + "', queue: '" + queueName + "', key: '" + queueName + "'}]}}";

            MessageConsumer responseReceiver = session.createConsumer(this.utils.getDestinationFromAddress(responseQueueAddress));

            // Send request
            MessageProducer requestProducer = session.createProducer(this.utils.getTopic(REQUEST_TOPIC, REQUEST_ROUTING_KEY));
            Message requestMessage = session.createMessage();
            requestMessage.setJMSReplyTo(this.utils.getTopic(RESPONSE_TOPIC, queueName));
            requestProducer.send(requestMessage);

            // Receive request & send response
            Session serverSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer requestReceiver = serverSession.createConsumer(this.utils.getQueue(REQUEST_QUEUE));
            Message serverMessage = requestReceiver.receive(1000);
            Assert.assertNotNull(serverMessage, "Server didn't receive request message");
            MessageProducer serverProducer = serverSession.createProducer(serverMessage.getJMSReplyTo());
            serverProducer.send(serverSession.createMessage());
            serverMessage.acknowledge();

            // Receive the response
            Message responseMessage = responseReceiver.receive(1000);
            Assert.assertNotNull(responseMessage, "Didn't receive response message");
            responseMessage.acknowledge();
        }
    }

}
