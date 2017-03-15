package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_2.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_2.utils.Utils;
import com.deutscheboerse.amqp.tests.LVQ;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestLVQ extends LVQ {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueBasic() throws JMSException, NamingException {
        super.testLVQQueueBasic();
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        super.testLVQQueueManyMessages();
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        super.testLVQQueueInTxn();
    }
}
