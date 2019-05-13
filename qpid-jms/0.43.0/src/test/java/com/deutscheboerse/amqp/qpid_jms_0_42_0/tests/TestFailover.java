package com.deutscheboerse.amqp.qpid_jms_0_42_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_42_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.Failover;

public class TestFailover extends Failover {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testPlainFailover() throws JMSException, NamingException, InterruptedException {
        super.testPlainFailover();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testPlainFailoverNested() throws JMSException, NamingException, InterruptedException {
        super.testPlainFailoverNested();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test(groups = { "disableInArtemis" })
    @Override
    public void testSSLFailover() throws JMSException, NamingException, InterruptedException {
        super.testSSLFailover();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test(groups = { "disableInArtemis" })
    @Override
    public void testSSLFailoverNested() throws JMSException, NamingException, InterruptedException {
        super.testSSLFailoverNested();
    }
}
