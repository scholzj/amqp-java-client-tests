package com.deutscheboerse.examples.fixml.amqp_1_0.swiftmq;

import com.swiftmq.amqp.AMQPContext;
import com.swiftmq.amqp.v100.client.AMQPException;
import com.swiftmq.amqp.v100.client.Connection;
import com.swiftmq.amqp.v100.client.Consumer;
import com.swiftmq.amqp.v100.client.QoS;
import com.swiftmq.amqp.v100.client.Session;

/**
 * Broadcast Receiver Receives broadcasts from the persistent broadcast queue
 */
public class BroadcastReceiver
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
        Listener listener = null;
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
            responseConsumer = session.createConsumer("broadcast.ABCFR_ABCFRALMMACC1.TradeConfirmation", 1000, QoS.AT_LEAST_ONCE, true, null);
        
            /*
             * Step 5: Receiving broadcast messages using listener for timeout seconds
             */
            System.out.println("Receiving broadcast messages for " + timeout/1000 + " seconds");
            listener = new Listener(responseConsumer);
            listener.setTimeout(timeout);
            System.out.println("Finished receiving broadcast messages for " + timeout/1000 + " seconds");            
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
