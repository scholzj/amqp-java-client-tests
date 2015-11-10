package com.deutscheboerse.qpid.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;
import jms.Queueing;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

public class TestQueueing extends Queueing {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
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
    
    @Test
    @Override
    public void testFullQueue() throws JMSException, NamingException, InterruptedException, QmfException {
        super.testFullQueue();
    }
}
