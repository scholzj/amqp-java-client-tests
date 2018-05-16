package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_1.tests;

import com.deutscheboerse.amqp.tests.Security;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_1.utils.Utils;

public class TestSecurity extends Security {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testAuthenticationDefault() throws JMSException, NamingException, InterruptedException {
        super.testAuthenticationDefault();
    }

    @Test
    @Override
    public void testAuthenticationPlain() throws JMSException, NamingException, InterruptedException {
        super.testAuthenticationPlain();
    }

    @Test
    @Override
    public void testAuthenticationCramMD5() throws JMSException, NamingException, InterruptedException {
        super.testAuthenticationCramMD5();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testAuthenticationAnonymous() throws JMSException, NamingException, InterruptedException {
        super.testAuthenticationAnonymous();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testWrongPassword() throws JMSException, NamingException, InterruptedException {
        super.testWrongPassword();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testWrongUsername() throws JMSException, NamingException, InterruptedException {
        super.testWrongUsername();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testACLDeniedConsumer() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedConsumer();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testACLDeniedProducerForbiddenTopic() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedProducerForbiddenTopic();
    }

    // Works only in 0.6.0 and higher
    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testACLDeniedProducerForbiddenRoutingKey() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedProducerForbiddenRoutingKey();
    }

    @Test
    @Override
    public void testMaximumAllowedConnections() throws JMSException, NamingException, InterruptedException {
        super.testMaximumAllowedConnections();
    }

    @Test
    @Override
    public void testUserID() throws JMSException, NamingException, InterruptedException {
        super.testUserID();
    }
}
