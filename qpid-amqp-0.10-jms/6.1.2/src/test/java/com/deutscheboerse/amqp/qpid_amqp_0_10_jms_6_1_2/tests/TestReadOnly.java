package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_2.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_2.utils.Utils;
import com.deutscheboerse.amqp.tests.ReadOnly;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

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
