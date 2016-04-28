package com.deutscheboerse.amqp.qpid_jms_0_6_0.tests;

import com.deutscheboerse.amqp.tests.SSL;
import javax.jms.*;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_jms_0_6_0.utils.Utils;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.utils.JavaBrokerUtils;
import org.testng.annotations.BeforeMethod;

@Test(groups = { "enableInQpidJava-6.2" })
public class TestSSL extends SSL {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @BeforeMethod(groups = { "disableInQpidJava" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @BeforeMethod(groups = { "disableInMRG" })
    public void clearAllQueues() throws IllegalAccessException {
        JavaBrokerUtils.getInstance().clearAllQueues();
    }

    @Test
    @Override
    public void testSuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        super.testSuccessfullClientAuthentication();
    }

    @Test(expectedExceptions = JMSException.class)
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
    public void testSSLv3() throws JMSException, NamingException, InterruptedException {
        super.testSSLv3();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testSSLv2() throws JMSException, NamingException, InterruptedException {
        super.testSSLv2();
    }

    @Test
    @Override
    public void testTLSv1() throws JMSException, NamingException, InterruptedException {
        super.testTLSv1();
    }

    @Test
    @Override
    public void testCipherSuite3DES() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuite3DES();
    }

    @Test
    @Override
    public void testCipherSuiteAES128() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuiteAES128();
    }

    // JCA Unlimited Strength policy files are needed for AES256
    // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    @Test
    @Override
    public void testCipherSuiteAES256() throws JMSException, NamingException, InterruptedException {
        super.testCipherSuiteAES256();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
    @Override
    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSLWithClientAuth();
    }

    @Test(expectedExceptions = JMSException.class)
    @Override
    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testPlainOverSSL();
    }

    @Test(expectedExceptions = JMSSecurityException.class)
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

    @Test
    @Override
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        super.testMaximumAllowedConnectionsOverSSL();
    }
}
