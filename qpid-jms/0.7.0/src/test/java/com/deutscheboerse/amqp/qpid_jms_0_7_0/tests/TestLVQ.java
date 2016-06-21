package com.deutscheboerse.amqp.qpid_jms_0_7_0.tests;

import com.deutscheboerse.amqp.tests.LVQ;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_7_0.utils.Utils;

public class TestLVQ extends LVQ {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the LVQ feature
    @Test(groups = { "disableInArtemis" })
    @Override
    public void testLVQQueueBasic() throws JMSException, NamingException {
        super.testLVQQueueBasic();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueBasicArtemis() throws JMSException, NamingException {
        super.testLVQQueueBasicArtemis();
    }

    // Test the LVQ feature
    @Test(groups = { "disableInArtemis" })
    @Override
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        super.testLVQQueueManyMessages();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueManyMessagesArtemis() throws JMSException, NamingException {
        super.testLVQQueueManyMessagesArtemis();
    }

    // Test the LVQ feature
    @Test(groups = { "disableInMRG-3.0.0", "disableInArtemis" })
    @Override
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        super.testLVQQueueInTxn();
    }

    @Test(groups = { "disableInMRG", "disableInQpidJava" })
    public void testLVQQueueInTxnArtemis() throws JMSException, NamingException {
        super.testLVQQueueInTxnArtemis();
    }
}
