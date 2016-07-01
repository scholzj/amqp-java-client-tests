package com.deutscheboerse.amqp.utils;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jms.PoolingConnectionFactory;

import javax.jms.*;
import javax.transaction.*;
import java.lang.IllegalStateException;
import java.util.HashSet;
import java.util.Set;

public class AutoCloseableConnectionTransactionManager implements Connection, AutoCloseable, TransactionManager {

    private final PoolingConnectionFactory connectionFactory;
    private final Connection connection;
    private final BitronixTransactionManager btm;
    private final Set<Session> sessions;

    public AutoCloseableConnectionTransactionManager(PoolingConnectionFactory connectionFactory) throws JMSException {
        this.connectionFactory = connectionFactory;
        this.connection = this.connectionFactory.createConnection();
        this.btm = TransactionManagerServices.getTransactionManager();
        this.sessions = new HashSet<>();
    }
    
    @Override
    public Session createSession(boolean bln, int i) throws JMSException {
        Session session = this.connection.createSession(bln, i);
        sessions.add(session);
        return session;
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
        for (Session session : this.sessions) {
            if (session != null) {
                session.close();
            }
        }
        if (this.connection != null) {
            this.connection.close();
        }
        if (this.connectionFactory != null) {
            this.connectionFactory.close();
        }
        if (this.btm != null) {
            btm.shutdown();
        }
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination dstntn, String string, ServerSessionPool ssp, int i) throws JMSException {
        return this.connection.createConnectionConsumer(dstntn, string, ssp, i);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String string, String string1, ServerSessionPool ssp, int i) throws JMSException {
        return this.connection.createDurableConnectionConsumer(topic, string, string1, ssp, i);
    }

    /**
     * Starts XA transaction
     * @throws NotSupportedException
     * @throws SystemException
     */
    @Override
    public void begin() throws NotSupportedException, SystemException {
        this.btm.begin();
    }

    /**
     * Commits XA transaction
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws SystemException
     */
    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        this.btm.commit();
    }

    /**
     * Current XA transaction status
     * @return
     * @throws SystemException
     */
    @Override
    public int getStatus() throws SystemException {
        return this.btm.getStatus();
    }

    @Override
    public Transaction getTransaction() throws SystemException {
        return this.btm.getTransaction();
    }

    /**
     * Resume suspended XA transaction
     * @param transaction
     * @throws InvalidTransactionException
     * @throws IllegalStateException
     * @throws SystemException
     */
    @Override
    public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        this.btm.resume(transaction);
    }

    /**
     * Rollback XA transaction
     * @throws IllegalStateException
     * @throws SecurityException
     * @throws SystemException
     */
    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        this.btm.rollback();
    }

    /**
     * Marks XA transaction as rollback only
     * @throws IllegalStateException
     * @throws SystemException
     */
    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        this.btm.setRollbackOnly();
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        this.btm.setTransactionTimeout(seconds);
    }

    /**
     * Suspends current XA transaction
     * @return
     * @throws SystemException
     */
    @Override
    public Transaction suspend() throws SystemException {
        return this.btm.suspend();
    }
}
