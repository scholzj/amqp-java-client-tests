package com.deutscheboerse.amqp.qpid_jms_0_37_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_37_0.utils.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.Expiration;

public class TestExpiration extends Expiration {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        super.testMessageExpiration();
    }
}
