package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.utils.Utils;
import com.deutscheboerse.amqp.tests.SSL;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class TestSSL extends SSL {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testSuccessfullClientAuthentication() throws JMSException, NamingException {
        super.testSuccessfullClientAuthentication();
    }

    @Test(expectedExceptions = JMSException.class, timeOut = 10000)
    @Override
    public void testUnsuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        super.testUnsuccessfullClientAuthentication();
    }

    @Test
    @Override
    public void testHostnameVerification() throws JMSException, NamingException, InterruptedException {
        super.testHostnameVerification();
    }

    @Test
    @Override
    public void testWrongServerCertificate() throws JMSException, NamingException, InterruptedException {
        super.testWrongServerCertificate();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSLWithClientAuth();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSL();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testAnonymousOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        super.testAnonymousOverSSLWithClientAuth();
    }

    @Test(expectedExceptions = JMSException.class, timeOut = 10000)
    @Override
    public void testSignedByCannotLogin() throws JMSException, NamingException, InterruptedException {
        super.testSignedByCannotLogin();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testSSLConnectionToNonSSLPort() throws JMSException, NamingException, InterruptedException {
        super.testSSLConnectionToNonSSLPort();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testNonSSLConnectionToSSLPort() throws JMSException, NamingException, InterruptedException {
        super.testNonSSLConnectionToSSLPort();
    }

    @Test
    @Override
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testMaximumAllowedConnectionsOverSSL();
    }
}
