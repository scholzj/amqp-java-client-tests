package com.deutscheboerse.amqp.qpid_jms_0_8_0.tests;

import com.deutscheboerse.amqp.tests.RequestResponse;
import com.deutscheboerse.amqp.qpid_jms_0_8_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

@Test(groups = { "enableInQpidJava-6.2" })
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
