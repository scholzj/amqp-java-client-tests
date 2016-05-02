package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.tests;

import com.deutscheboerse.amqp.tests.ReadOnly;
import org.apache.qpid.qmf2.common.QmfException;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.utils.Utils;

@Test(groups = { "enableInQpidJava-6.2" })
public class TestReadOnly extends ReadOnly {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the read only queue feature
    @Test
    @Override
    public void testReadOnlyQueue() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueue();
    }

    // Test the read only queue feature with transaction reader
    @Test
    @Override
    public void testReadOnlyQueueWithTxn() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueueWithTxn();
    }
}
