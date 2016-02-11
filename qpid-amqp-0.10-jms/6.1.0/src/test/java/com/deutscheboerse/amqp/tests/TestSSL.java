package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.*;
import javax.naming.NamingException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.utils.Utils;

public class TestSSL extends SSL {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }

    /*
    From version 6.1.0, TLSv1 is disabled by default. On older brokers it has to be enabled, otherwise we will be unable
    to connect (they support only TLSv1, not 1.1 or 1.2). This method enables it before every method by setting a
    system property

    This method should disabled for newer broker releases with TLSv1.1 and 1.2
     */
    @BeforeMethod(groups = { "disableInQpid0.36"})
    public void enabledTLSv1()
    {
        System.setProperty("qpid.disabled_ssl_protocols", "SSLv3");
    }

    /*
    From version 6.1.0, TLSv1 is disabled by default. On older brokers it has to be enabled, otherwise we will be unable
    to connect (they support only TLSv1, not 1.1 or 1.2). This method removes the property which enabled TLSv1, so that
    it doesn't interfere with other tests.

    This method should disabled for newer broker releases with TLSv1.1 and 1.2
     */
    @AfterMethod(groups = { "disableInQpid0.36"})
    public void disableTLSv1()
    {
        System.clearProperty("qpid.disabled_ssl_protocols");
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
