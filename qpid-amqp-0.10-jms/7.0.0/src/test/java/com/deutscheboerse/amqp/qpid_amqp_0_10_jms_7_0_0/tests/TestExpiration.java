package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_7_0_0.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_7_0_0.utils.Utils;
import com.deutscheboerse.amqp.tests.Expiration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestExpiration extends Expiration {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        super.testMessageExpiration();
    }
}