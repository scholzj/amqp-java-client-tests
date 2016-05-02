package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_0.tests;

import com.deutscheboerse.amqp.tests.TempQueues;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.naming.NamingException;

@Test(groups = { "disableInQpidJava" })
public class TestTempQueues extends TempQueues {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueue();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseWrongQueueName() throws JMSException, NamingException, InterruptedException {
        super.testResponseWrongQueueName();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseWrongRoutingKey() throws JMSException, NamingException, InterruptedException {
        super.testResponseWrongRoutingKey();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseInvalidPolicy() throws JMSException, NamingException, InterruptedException {
        super.testResponseInvalidPolicy();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseNotAutodelete() throws JMSException, NamingException, InterruptedException {
        super.testResponseNotAutodelete();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseWrongExchange() throws JMSException, NamingException, InterruptedException {
        super.testResponseWrongExchange();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseTooBigSize() throws JMSException, NamingException, InterruptedException {
        super.testResponseTooBigSize();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseTooSmallSize() throws JMSException, NamingException, InterruptedException {
        super.testResponseTooSmallSize();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseTooBigCount() throws JMSException, NamingException, InterruptedException {
        super.testResponseTooBigCount();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseTooSmallCount() throws JMSException, NamingException, InterruptedException {
        super.testResponseTooSmallCount();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testResponseQueueWrongUser() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueueWrongUser();
    }

    @Test
    @Override
    public void testResponseQueueAutodelete() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueueAutodelete();
    }

    @Test
    @Override
    public void testResponseQueueAutodeleteTimeout() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueueAutodeleteTimeout();
    }
}
