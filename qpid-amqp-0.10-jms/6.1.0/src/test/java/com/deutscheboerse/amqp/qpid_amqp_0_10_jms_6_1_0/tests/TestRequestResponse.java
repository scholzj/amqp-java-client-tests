package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.tests;

import com.deutscheboerse.amqp.tests.RequestResponse;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.naming.NamingException;

public class TestRequestResponse extends RequestResponse {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod(groups = { "disableInQpidJava" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @Test
    @Override
    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueue();
    }
}
