package com.deutscheboerse.amqp.qpid_jms_0_31_0.tests;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.qpid_jms_0_31_0.utils.Utils;
import com.deutscheboerse.amqp.tests.SSL;

public class TestSSL extends SSL {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testSuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        super.testSuccessfullClientAuthentication();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testUnsuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        super.testUnsuccessfullClientAuthentication();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testHostnameVerification() throws JMSException, NamingException, InterruptedException {
        super.testHostnameVerification();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testWrongServerCertificate() throws JMSException, NamingException, InterruptedException {
        super.testWrongServerCertificate();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testSSLv3() throws JMSException, NamingException, InterruptedException {
        super.testSSLv3();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testSSLv2() throws JMSException, NamingException, InterruptedException {
        super.testSSLv2();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testTLSv1() throws JMSException, NamingException, InterruptedException {
        super.testTLSv1();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInMRG-3.2.0", "disableInArtemis" })
    @Override
    public void testTLSv11() throws JMSException, NamingException, InterruptedException {
        super.testTLSv11();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInMRG-3.2.0", "disableInArtemis" })
    @Override
    public void testTLSv12() throws JMSException, NamingException, InterruptedException {
        super.testTLSv12();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testCipherSuite3DES() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuite3DES();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testCipherSuiteAES128() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuiteAES128();
    }

    // JCA Unlimited Strength policy files are needed for AES256
    // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    @Test(groups = { "disableInArtemis" })
    @Override
    public void testCipherSuiteAES256() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuiteAES256();
    }

    @Test(groups = { "disableInArtemis" }, expectedExceptions = JMSSecurityException.class)
    @Override
    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSLWithClientAuth();
    }

    @Test(expectedExceptions = JMSException.class, groups = { "disableInArtemis" })
    @Override
    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSL();
    }

    // doesn't work for artemis, because it returns JMSException
    @Test(groups = { "disableInArtemis" }, expectedExceptions = JMSSecurityException.class)
    @Override
    public void testAnonymousOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        super.testAnonymousOverSSLWithClientAuth();
    }

    @Test(expectedExceptions = JMSException.class)
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

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testMaximumAllowedConnectionsOverSSL();
    }
}
