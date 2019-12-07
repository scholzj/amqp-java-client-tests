package com.deutscheboerse.amqp.qpid_jms_0_49_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_49_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.RequestResponse;

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
