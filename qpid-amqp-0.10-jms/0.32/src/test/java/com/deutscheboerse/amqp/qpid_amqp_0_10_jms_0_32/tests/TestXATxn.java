package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_0_32.tests;

import com.deutscheboerse.amqp.tests.XATxn;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.transaction.xa.XAException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_0_32.utils.Utils;

public class TestXATxn extends XATxn {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod(groups = { "disableInQpidJava" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
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
    @Test
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
    @Test(expectedExceptions = XAException.class)
    @Override
    public void testTxnDefaultTimeout() throws JMSException, NamingException, XAException, InterruptedException {
        super.testTxnDefaultTimeout();
    }
}
