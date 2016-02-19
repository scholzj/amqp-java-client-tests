package com.deutscheboerse.amqp.qpid_jms_0_5_0.tests;

import com.deutscheboerse.amqp.tests.Queueing;
import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_5_0.utils.Utils;

public class TestQueueing extends Queueing {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    @Test
    @Override
    public void testRoutingKey() throws JMSException, NamingException {
        super.testRoutingKey();
    }
    
    @Test
    @Override
    public void testDeadLetterQueue() throws JMSException, NamingException {
        super.testDeadLetterQueue();
    }
    
    @Test
    @Override
    public void testRingQueue() throws JMSException, NamingException {
        super.testRingQueue();
    }
}
