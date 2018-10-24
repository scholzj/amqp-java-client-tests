package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_7_0_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_7_0_0.utils.Utils;
import com.deutscheboerse.amqp.tests.SSL;

public class TestSSL extends SSL {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    /*
    From version 6.1.0, TLSv1 is disabled by default. On older brokers it has to be enabled, otherwise we will be unable
    to connect (they support only TLSv1, not 1.1 or 1.2). This method enables it before every method by setting a
    system property

    This method should disabled for newer broker releases with TLSv1.1 and 1.2
     */
    @BeforeMethod(groups = { "disableInQpid1.35", "disableInQpid1.36", "disableInQpid1.37", "disableInQpid1.38", "disableInQpid1.39", "disableInQpid1.40" })
    public void enabledTLSv1()
    {
        System.setProperty("qpid.security.tls.protocolWhiteList", "TLSv1, TLSv1.1, TLSv1.2");
    }

    /*
    From version 6.1.0, TLSv1 is disabled by default. On older brokers it has to be enabled, otherwise we will be unable
    to connect (they support only TLSv1, not 1.1 or 1.2). This method removes the property which enabled TLSv1, so that
    it doesn't interfere with other tests.

    This method should disabled for newer broker releases with TLSv1.1 and 1.2
     */
    @AfterMethod(groups = { "disableInQpid1.35", "disableInQpid1.36", "disableInQpid1.37", "disableInQpid1.38", "disableInQpid1.39", "disableInQpid1.40" })
    public void disableTLSv1()
    {
        System.clearProperty("qpid.security.tls.protocolWhiteList");
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
