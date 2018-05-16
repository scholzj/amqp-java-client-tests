package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_1.tests;

import bitronix.tm.internal.BitronixSystemException;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_1.utils.Utils;
import com.deutscheboerse.amqp.tests.XATxn;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.transaction.*;
import javax.transaction.xa.XAException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestXATxn extends XATxn {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the commit feature
    @Test
    @Override
    public void testTxnCommit() throws JMSException, NamingException, XAException {
        super.testTxnCommit();
    }

    // Test the commit feature
    @Test
    @Override
    public void testTxnCommitUsingTransactionManager() throws HeuristicMixedException, NamingException, RollbackException, SystemException, JMSException, HeuristicRollbackException, NotSupportedException {
        super.testTxnCommitUsingTransactionManager();
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testTxnSenderRollback() throws JMSException, NamingException, XAException {
        super.testTxnSenderRollback();
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testTxnSenderRollbackUsingTransactionManager() throws SystemException, NamingException, NotSupportedException, JMSException {
        super.testTxnSenderRollbackUsingTransactionManager();
    }

    // Test the receiver rollback feature
    @Test
    @Override
    public void testTxnReceiverRollback() throws JMSException, NamingException, XAException {
        super.testTxnReceiverRollback();
    }

    // Test the receiver rollback feature
    @Test
    @Override
    public void testTxnReceiverRollbackUsingTransactionManager() throws HeuristicMixedException, NamingException, RollbackException, SystemException, JMSException, HeuristicRollbackException, NotSupportedException {
        super.testTxnReceiverRollbackUsingTransactionManager();
    }

    // Test the commit feature with large commit
    @Test
    @Override
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException, XAException {
        super.testTxnCommitLotOfMessages();
    }

    // Test the commit feature with large commit
    @Test
    @Override
    public void testTxnCommitLotOfMessagesUsingTransactionManager() throws HeuristicMixedException, NamingException, RollbackException, SystemException, JMSException, HeuristicRollbackException, NotSupportedException {
        super.testTxnCommitLotOfMessagesUsingTransactionManager();
    }

    // Timeout higher than 600 seconds should cause an error
    @Test(groups = { "disableInQpidJava" })
    @Override
    public void testTxnMaximumTimeout() throws JMSException, NamingException, XAException {
        super.testTxnMaximumTimeout();
    }

    // Timeout higher than 600 seconds should cause an error
    @Test(groups = { "disableInQpidJava", "disableInMRG" })
    @Override
    public void testTxnMaximumTimeoutUsingTransactionManager() throws JMSException, NamingException, NotSupportedException, SystemException {
        super.testTxnMaximumTimeoutUsingTransactionManager();
    }

    @Test
    @Override
    public void testTxnBelowMaximumTimeout() throws JMSException, NamingException, XAException {
        super.testTxnBelowMaximumTimeout();
    }

    @Test
    @Override
    public void testTxnBelowMaximumTimeoutUsingTransactionManager() throws JMSException, NamingException, NotSupportedException, SystemException {
        super.testTxnBelowMaximumTimeoutUsingTransactionManager();
    }

    @Test(expectedExceptions = XAException.class)
    @Override
    public void testTxnTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        super.testTxnTimeout();
    }

    @Test(expectedExceptions = BitronixSystemException.class)
    @Override
    public void testTxnTimeoutUsingTransactionManager() throws JMSException, NamingException, InterruptedException, HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException, SystemException {
        super.testTxnTimeoutUsingTransactionManager();
    }

    // Tests the default transaction timeout => Takes quite long, because default timeout is 60 seconds.
    @Test(expectedExceptions = XAException.class,  groups = { "disableInQpidJava" })
    @Override
    public void testTxnDefaultTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        super.testTxnDefaultTimeout();
    }

    // Tests the default transaction timeout => Takes quite long, because default timeout is 60 seconds.
    @Test(expectedExceptions = BitronixSystemException.class, groups = { "disableInQpidJava", "disableInMRG" })
    @Override
    public void testTxnDefaultTimeoutUsingTransactionManager() throws InterruptedException, HeuristicMixedException, NamingException, RollbackException, SystemException, JMSException, HeuristicRollbackException, NotSupportedException {
        super.testTxnDefaultTimeoutUsingTransactionManager();
    }
}
