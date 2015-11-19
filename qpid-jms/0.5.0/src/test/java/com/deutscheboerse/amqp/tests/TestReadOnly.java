package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import com.deutscheboerse.amqp.utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = { "disableInMRG-3.0.0" })
public class TestReadOnly extends ReadOnly {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    // Test the read only queue feature
    @Test
    @Override
    public void testReadOnlyQueue() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueue();
    }
    
    // Test the read only queue feature with transaction reader
    @Test
    @Override
    public void testReadOnlyQueueWithTxn() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueueWithTxn();
    }
    
}
