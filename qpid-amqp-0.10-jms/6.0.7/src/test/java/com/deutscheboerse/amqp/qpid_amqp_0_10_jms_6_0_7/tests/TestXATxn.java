package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_7.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_7.utils.Utils;
import com.deutscheboerse.amqp.tests.XATxn;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.transaction.xa.XAException;

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

    // Test the sender rollback feature
    @Test
    @Override
    public void testTxnSenderRollback() throws JMSException, NamingException, XAException {
        super.testTxnSenderRollback();
    }

    // Test the receiver rollback feature
    @Test
    @Override
    public void testTxnReceiverRollback() throws JMSException, NamingException, XAException {
        super.testTxnReceiverRollback();
    }

    // Test the commit feature with large commit
    @Test
    @Override
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException, XAException {
        super.testTxnCommitLotOfMessages();
    }

    // Timeout higher than 600 seconds should cause an error
    @Test(groups = { "disableInQpidJava" })
    @Override
    public void testTxnMaximumTimeout() throws JMSException, NamingException, XAException {
        super.testTxnMaximumTimeout();
    }

    @Test
    @Override
    public void testTxnBelowMaximumTimeout() throws JMSException, NamingException, XAException {
        super.testTxnBelowMaximumTimeout();
    }

    @Test(expectedExceptions = XAException.class)
    @Override
    public void testTxnTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        super.testTxnTimeout();
    }

    // Tests the default transaction timeout => Takes quite long, because defualt timeout is 60 seconds.
    @Test(expectedExceptions = XAException.class, groups = { "disableInQpidJava" })
    @Override
    public void testTxnDefaultTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        super.testTxnDefaultTimeout();
    }
}
