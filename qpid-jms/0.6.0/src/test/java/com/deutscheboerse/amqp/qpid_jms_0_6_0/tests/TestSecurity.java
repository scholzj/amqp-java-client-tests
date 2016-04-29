package com.deutscheboerse.amqp.qpid_jms_0_6_0.tests;

import com.deutscheboerse.amqp.tests.Security;
import javax.jms.*;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_6_0.utils.Utils;

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

    @Test(expectedExceptions = {JMSSecurityException.class, javax.jms.IllegalStateException.class}, timeOut = 10000)
    @Override
    public void testACLDeniedConsumer() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedConsumer();
    }

    @Test(expectedExceptions = JMSException.class, timeOut = 10000, groups = { "disableInMRG-3.0.0" })
    @Override
    public void testACLDeniedProducerForbiddenTopic() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedProducerForbiddenTopic();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testACLDeniedProducerForbiddenRoutingKey() throws JMSException, NamingException, InterruptedException {
        super.testACLDeniedProducerForbiddenRoutingKey();
    }

    @Test
    @Override
    public void testMaximumAllowedConnections() throws JMSException, NamingException, InterruptedException {
        super.testMaximumAllowedConnections();
    }
}
