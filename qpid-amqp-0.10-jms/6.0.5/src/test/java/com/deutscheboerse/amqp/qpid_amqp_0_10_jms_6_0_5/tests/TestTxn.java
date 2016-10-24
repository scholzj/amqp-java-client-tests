package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_5.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_5.utils.Utils;
import com.deutscheboerse.amqp.tests.Txn;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestTxn extends Txn {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the commit feature
    @Test
    @Override
    public void testTxnCommit() throws JMSException, NamingException {
        super.testTxnCommit();
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testTxnSenderRollback() throws JMSException, NamingException {
        super.testTxnSenderRollback();
    }

    // Test the receiver rollback feature
    @Test
    @Override
    public void testTxnReceiverRollback() throws JMSException, NamingException {
        super.testTxnReceiverRollback();
    }

    // Test the commit feature with large commit
    @Test
    @Override
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException {
        super.testTxnCommitLotOfMessages();
    }
}
