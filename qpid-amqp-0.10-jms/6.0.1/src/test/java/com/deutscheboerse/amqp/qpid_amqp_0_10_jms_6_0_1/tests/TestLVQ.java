package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.tests;

import com.deutscheboerse.amqp.tests.LVQ;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.utils.Utils;

@Test( groups = { "enableInQpidJava-6.2" })
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
