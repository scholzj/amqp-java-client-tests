package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.utils.JavaBrokerUtils;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

public class RequestResponse extends BaseTest {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    private static final String REQUEST_TOPIC = Settings.get("routing.request_topic");
    private static final String REQUEST_ROUTING_KEY = Settings.get("routing.request_routing_key");
    private static final String REQUEST_QUEUE = Settings.get("routing.request_queue");

    private static final String RESPONSE_TOPIC = Settings.get("routing.response_topic");
    private static final String RESPONSE_FIXED_QUEUE = Settings.get("routing.response_fixed_queue");

    @BeforeMethod(groups = { "disableInQpidJava" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @BeforeMethod(groups = { "disableInMRG" })
    public void clearAllQueues() throws IllegalAccessException {
        JavaBrokerUtils.getInstance().clearAllQueues();
    }

    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer responseReceiver = session.createConsumer(this.utils.getQueue(RESPONSE_FIXED_QUEUE));

            // Send request
            MessageProducer requestProducer = session.createProducer(this.utils.getTopic(REQUEST_TOPIC));
            Message requestMessage = session.createMessage();
            requestMessage.setJMSType(REQUEST_ROUTING_KEY);
            // TODO: How to send request with reply-to containing routing key?
            requestMessage.setJMSReplyTo(this.utils.getTopic(RESPONSE_TOPIC));
            requestProducer.send(requestMessage);

            // Receive request & send response
            Session serverSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer requestReceiver = serverSession.createConsumer(this.utils.getQueue(REQUEST_QUEUE));
            Message serverMessage = requestReceiver.receive(1000);
            Assert.assertNotNull(serverMessage, "Server didn't received request message");
            MessageProducer serverProducer = serverSession.createProducer(serverMessage.getJMSReplyTo());
            Message serverResponse = serverSession.createMessage();
            serverResponse.setJMSType(RESPONSE_FIXED_QUEUE);
            serverProducer.send(serverResponse);

            // Receive the response
            Message responseMessage = responseReceiver.receive(1000);
            Assert.assertNotNull(responseMessage, "Didn't received response message");
        }
    }
}
