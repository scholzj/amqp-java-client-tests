package com.deutscheboerse.amqp.qpid_jms_0_5_0.tests;

import com.deutscheboerse.amqp.tests.Misc;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_5_0.utils.Utils;

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
}
