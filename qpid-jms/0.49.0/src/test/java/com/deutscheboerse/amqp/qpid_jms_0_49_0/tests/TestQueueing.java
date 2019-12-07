package com.deutscheboerse.amqp.qpid_jms_0_49_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_49_0.utils.Utils;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.Queueing;

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

    @Test(groups = { "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testRingQueue() throws JMSException, NamingException {
        super.testRingQueue();
    }

    @Test(groups = { "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testFullQueue() throws JMSException, NamingException, InterruptedException, QmfException {
        super.testFullQueue();
    }
}
