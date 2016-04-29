package com.deutscheboerse.amqp.qpid_jms_0_7_0.tests;

import com.deutscheboerse.amqp.tests.Expiration;
import com.deutscheboerse.amqp.qpid_jms_0_7_0.utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = { "enableInQpidJava-6.2" })
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
