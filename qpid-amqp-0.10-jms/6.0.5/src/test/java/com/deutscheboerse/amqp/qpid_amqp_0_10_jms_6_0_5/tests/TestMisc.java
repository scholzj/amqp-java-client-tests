package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_5.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_5.utils.Utils;
import com.deutscheboerse.amqp.tests.Misc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

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
