package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import com.deutscheboerse.amqp.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestRequestResponse extends RequestResponse {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }

    @Test
    @Override
    public void testResponseQueue() throws JMSException, NamingException, InterruptedException {
        super.testResponseQueue();
    }
}
