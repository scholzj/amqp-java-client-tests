package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_2.tests;

import com.deutscheboerse.amqp.tests.RequestResponse;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_2.utils.Utils;
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
