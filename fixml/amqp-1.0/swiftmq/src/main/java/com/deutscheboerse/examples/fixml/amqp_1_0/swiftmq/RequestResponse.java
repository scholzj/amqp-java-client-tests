package com.deutscheboerse.examples.fixml.amqp_1_0.swiftmq;

import java.util.UUID;

import com.swiftmq.amqp.AMQPContext;
import com.swiftmq.amqp.v100.client.AMQPException;
import com.swiftmq.amqp.v100.client.Connection;
import com.swiftmq.amqp.v100.client.Consumer;
import com.swiftmq.amqp.v100.client.Producer;
import com.swiftmq.amqp.v100.client.QoS;
import com.swiftmq.amqp.v100.client.Session;
import com.swiftmq.amqp.v100.generated.messaging.message_format.*;
import com.swiftmq.amqp.v100.messaging.AMQPMessage;
import com.swiftmq.amqp.v100.types.AMQPString;


/**
 * Broadcast Receiver Receives broadcasts from the persistent broadcast queue
 */
public class RequestResponse
{
    private static int timeout = 100000;

    /**
     * @param args
     *            the command line arguments
     * @throws AMQPException 
     */
    public static void main(String[] args) throws AMQPException
    {
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore", "ABCFR_ABCFRALMMACC1.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        //System.setProperty("javax.net.debug", "ssl");
        //System.setProperty("swiftmq.amqp.debug", "true");
        //System.setProperty("swiftmq.amqp.frame.debug", "true");

        /*
         * Step 1: Initializing the variables
         */
        Connection connection = null;
        Session session = null;
        Producer requestProducer = null;
        Consumer responseConsumer = null;

        try
        {
            /*
             * Step 2: Preparing the connection and session
             */
            AMQPContext ctx = new AMQPContext(AMQPContext.CLIENT);
            connection = new Connection(ctx, "ecag-fixml-simu1.deutsche-boerse.de", 10170, "", "");
            connection.setMechanism("EXTERNAL");
            connection.setSocketFactory(new MySSLSocketFactory("abcfr_abcfralmmacc1"));
            
            /*
             * Step 3: Starting the connection
             */
            connection.connect();
            System.out.println("Connected");

            /*
             * Step 4: Creating a producer and consumer
             */
            session = connection.createSession(1000, 1000);
            requestProducer = session.createProducer("request.ABCFR_ABCFRALMMACC1", QoS.AT_LEAST_ONCE);
            responseConsumer = session.createConsumer("response.ABCFR_ABCFRALMMACC1", 1000, QoS.AT_LEAST_ONCE, true, null);
            
            /*
             * Step 5: Sending a request
             */
            AMQPMessage msg = new AMQPMessage();
            msg.setAmqpValue(new AmqpValue(new AMQPString("<FIXML>...</FIXML>")));
            Properties msgProp = new Properties();
            msgProp.setReplyTo(new AddressString("response/response.ABCFR_ABCFRALMMACC1"));
            msgProp.setCorrelationId(new MessageIdString(UUID.randomUUID().toString()));
            msg.setProperties(msgProp);
            requestProducer.send(msg);

            System.out.println("REQUEST SENT:");
            System.out.println("#############");
            System.out.println("Correlation ID: " + msg.getProperties().getCorrelationId().getValueString());
            System.out.println("Message Text  : " + msg.getAmqpValue().getValue().getValueString());
            System.out.println("#############");

            /*
             * Step 6: Receive response
             */
            System.out.println("Waiting " + timeout/1000 + " seconds for reply");
            AMQPMessage receivedMsg = responseConsumer.receive(timeout);
            if (receivedMsg != null)
            {
                System.out.println("RECEIVED MESSAGE:");
                System.out.println("#################");
                System.out.println("Correlation ID: " + receivedMsg.getProperties().getCorrelationId().getValueString());
                System.out.println("Message Text  : " + new String(receivedMsg.getData().get(0).getValue()));
                System.out.println("#################");
                receivedMsg.accept();
            }
            else
            {
                System.out.println("Reply wasn't received for " + timeout/1000 + " seconds");
            }
        }
        catch (Exception ex)
        {
            System.out.println("Failed to connect and create consumer or producer!");
            ex.printStackTrace();
            System.exit(1);
        }
        finally
        {
            // Closing the connection
            if (requestProducer != null)
            {
                System.out.println("Closing producer");
                requestProducer.close();
            }
            if (responseConsumer != null)
            {
                System.out.println("Closing consumer");
                responseConsumer.close();
            }
            if (session != null)
            {
                System.out.println("Closing session");
                session.close();
            }
            if (connection != null)
            {
                // implicitly closes session and producers/consumers 
                System.out.println("Closing connection");
                connection.close();
            }
        }
    }
}
