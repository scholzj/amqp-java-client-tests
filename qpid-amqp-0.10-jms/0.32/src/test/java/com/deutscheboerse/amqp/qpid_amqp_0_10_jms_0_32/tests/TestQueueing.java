package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_0_32.tests;

import com.deutscheboerse.amqp.tests.Queueing;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_0_32.utils.Utils;


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

    // Works only in 0.6.0 and higher
    @Test
    @Override
    public void testFullQueue() throws JMSException, NamingException, InterruptedException, QmfException {
        super.testFullQueue();
    }
}
