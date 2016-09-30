package com.deutscheboerse.amqp.qpid_jms_0_11_1.tests;

import com.deutscheboerse.amqp.qpid_jms_0_11_1.utils.Utils;
import com.deutscheboerse.amqp.tests.Misc;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMisc extends Misc {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testDuplicateClientID() throws JMSException, NamingException {
        super.testDuplicateClientID();
    }

    @Test
    @Override
    public void testMessageIDFormatUUID() throws JMSException, NamingException, QmfException {
        super.testMessageIDFormatUUIDNewFormat();
    }

    @Test
    @Override
    public void testMessageIDFormatUUIDString() throws JMSException, NamingException, QmfException {
        super.testMessageIDFormatUUIDStringNewFormat();
    }

}
