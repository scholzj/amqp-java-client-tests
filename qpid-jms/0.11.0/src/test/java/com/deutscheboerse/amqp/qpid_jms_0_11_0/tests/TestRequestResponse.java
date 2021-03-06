package com.deutscheboerse.amqp.qpid_jms_0_11_0.tests;

import com.deutscheboerse.amqp.tests.RequestResponse;
import com.deutscheboerse.amqp.qpid_jms_0_11_0.utils.Utils;
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
