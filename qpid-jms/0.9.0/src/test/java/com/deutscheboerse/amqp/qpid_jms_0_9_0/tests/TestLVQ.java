package com.deutscheboerse.amqp.qpid_jms_0_9_0.tests;

import com.deutscheboerse.amqp.tests.LVQ;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_9_0.utils.Utils;

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
    @Test(groups = { "disableInMRG-3.0.0" })
    @Override
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        super.testLVQQueueInTxn();
    }
}
