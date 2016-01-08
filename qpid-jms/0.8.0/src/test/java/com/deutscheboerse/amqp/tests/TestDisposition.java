package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import org.apache.qpid.qmf2.common.QmfException;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.utils.Utils;

public class TestDisposition extends Disposition {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid.jms.provider.amqp.FRAMES", "trace");
    }

    @Test
    @Override
    public void testAcceptDisposition() throws JMSException, NamingException, QmfException {
        super.testAcceptDisposition();
    }

    @Test
    @Override
    public void testRejectDisposition() throws JMSException, NamingException, QmfException {
        super.testRejectDisposition();
    }

    @Test
    @Override
    public void testReleasedDisposition() throws JMSException, NamingException, QmfException {
        super.testReleasedDisposition();
    }

    @Test
    @Override
    public void testModifiedFailedDisposition() throws JMSException, NamingException, QmfException {
        super.testModifiedFailedDisposition();
    }

    @Test
    @Override
    public void testModifiedUndeliverableDisposition() throws JMSException, NamingException, QmfException {
        super.testModifiedUndeliverableDisposition();
    }

    @Test
    @Override
    public void testBlockAcceptDisposition() throws JMSException, NamingException, QmfException {
        super.testBlockAcceptDisposition();
    }

    @Test
    @Override
    public void testBlockRejectDisposition() throws JMSException, NamingException, QmfException {
        super.testBlockRejectDisposition();
    }

    @Test
    @Override
    public void testBlockReleaseDisposition() throws JMSException, NamingException, QmfException {
        super.testBlockReleaseDisposition();
    }
}