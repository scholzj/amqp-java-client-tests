package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.utils.Utils;
import com.deutscheboerse.amqp.tests.RequestResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestRequestResponse extends RequestResponse {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueue();
    }
}
