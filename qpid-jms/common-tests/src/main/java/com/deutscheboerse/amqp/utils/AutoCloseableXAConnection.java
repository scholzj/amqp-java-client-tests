package com.deutscheboerse.amqp.utils;

import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.XAConnection;
import javax.jms.XASession;

public class AutoCloseableXAConnection  implements XAConnection, AutoCloseable {

    private final XAConnection connection;

    public AutoCloseableXAConnection(XAConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public XASession createXASession() throws JMSException {
        return this.connection.createXASession();
    }

    @Override
    public Session createSession(boolean bln, int i) throws JMSException {
        return this.connection.createSession(bln, i);
    }

    @Override
    public String getClientID() throws JMSException {
        return this.connection.getClientID();
    }

    @Override
    public void setClientID(String string) throws JMSException {
        this.connection.setClientID(string);
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return this.connection.getMetaData();
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return this.connection.getExceptionListener();
    }

    @Override
    public void setExceptionListener(ExceptionListener el) throws JMSException {
        this.connection.setExceptionListener(el);
    }

    @Override
    public void start() throws JMSException {
        this.connection.start();
    }

    @Override
    public void stop() throws JMSException {
        this.connection.stop();
    }

    @Override
    public void close() throws JMSException {
        this.connection.close();
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination dstntn, String string, ServerSessionPool ssp, int i) throws JMSException {
        return this.connection.createConnectionConsumer(dstntn, string, ssp, i);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String string, String string1, ServerSessionPool ssp, int i) throws JMSException {
        return this.connection.createDurableConnectionConsumer(topic, string, string1, ssp, i);
    }
    
}
