package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.tests;

import com.deutscheboerse.amqp.tests.Expiration;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.utils.Utils;

@Test(groups = { "enableInQpidJava-6.2" })
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
