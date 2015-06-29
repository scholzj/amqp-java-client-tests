package com.deutscheboerse.examples.fixml.amqp_1_0.swiftmq;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.swiftmq.amqp.v100.client.AMQPException;
import com.swiftmq.amqp.v100.client.Consumer;
import com.swiftmq.amqp.v100.client.InvalidStateException;
import com.swiftmq.amqp.v100.messaging.AMQPMessage;

/**
 * Message Listener
 * Processes incoming / received messages by printing them
 */
public class Listener
{
    private Timer timer;
    private int timeout;
    private final Consumer consumer;
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private boolean received = false;

    public Listener(Consumer consumer)
    {
        this.consumer = consumer;
    }

    public void setTimeout(int miliseconds) throws InterruptedException, AMQPException
    {
        timer = new Timer();
        timeout = miliseconds;
        timer.schedule(new StopListening(), miliseconds);
        Future<?> future = service.submit(new MessageReceiver());
        synchronized (this)
        {
            this.wait();
        }
        timer.cancel();
        future.cancel(true);
        service.shutdown();
        if (!received)
        {
            System.out.println("Reply wasn't received for " + timeout/1000 + " seconds");
        }
    }
    
    private class StopListening extends TimerTask
    {
        public void run()
        {
            synchronized (Listener.this)
            {
                Listener.this.notify();   
            }
        }
    }

    private class MessageReceiver implements Callable<Object>
    {
        @Override
        public Object call()
        {
            while (true)
            {
                AMQPMessage receivedMsg = consumer.receive();
                if (receivedMsg != null)
                {
                    Listener.this.received = true;
                    System.out.println("RECEIVED MESSAGE:");
                    System.out.println("#################");
                    String correlationId = (receivedMsg.getProperties() == null) ? "null" : receivedMsg.getProperties().getCorrelationId().getValueString();
                    System.out.println("Correlation ID: " + correlationId);
                    System.out.println("Message Text  : " + new String(receivedMsg.getData().get(0).getValue()));
                    System.out.println("#################");
                    try
                    {
                        receivedMsg.accept();
                    }
                    catch (InvalidStateException e)
                    {
                        System.out.println("Failed to acknowledge message.");
                    }
                }
                else
                {
                    return null;
                }
            }
        }
    }
}