package com.deutscheboerse.amqp.qpid_jms_0_43_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_43_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.LVQ;

public class TestLVQ extends LVQ {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the LVQ feature
    @Test(groups = { "disableInArtemis" })
    public void testLVQQueueBasic() throws JMSException, NamingException {
        super.testLVQQueueBasic();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueBasicArtemis() throws JMSException, NamingException {
        super.testLVQQueueBasicArtemis();
    }

    // Test the LVQ feature
    @Test(groups = { "disableInArtemis" })
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        super.testLVQQueueManyMessages();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueManyMessagesArtemis() throws JMSException, NamingException {
        super.testLVQQueueManyMessagesArtemis();
    }

    // Test the LVQ feature
    @Test(groups = { "disableInMRG-3.0.0", "disableInArtemis" })
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        super.testLVQQueueInTxn();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueInTxnArtemis() throws JMSException, NamingException {
        super.testLVQQueueInTxnArtemis();
    }
}
