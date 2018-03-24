package com.deutscheboerse.amqp.qpid_jms_0_8_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.qpid_jms_0_8_0.utils.Utils;
import com.deutscheboerse.amqp.tests.Disposition;

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

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testReleasedDisposition() throws JMSException, NamingException, QmfException {
        super.testReleasedDisposition();
    }

    @Test(groups = { "disableInArtemis" })
    @Override
    public void testModifiedFailedDisposition() throws JMSException, NamingException, QmfException {
        super.testModifiedFailedDisposition();
    }

    @Test(groups = { "disableInQpid1.35", "disableInQpid1.36", "disableInQpid1.37", "disableInQpid1.38", "disableInQpid1.39", "disableInArtemis" })
    @Override
    public void testModifiedUndeliverableDisposition() throws JMSException, NamingException, QmfException {
        super.testModifiedUndeliverableDisposition();
    }

    /*
    This is the updated version of the previous test case, which reflects the fix in MRG-M 3.3 / Qpid 0.36
     */
    @Test(groups = { "disableInQpidJava", "disableInQpid0.34", "disableInMRG-3.0.0", "disableInMRG-3.2.0" })
    @Override
    public void testModifiedUndeliverableDispositionFixed() throws JMSException, NamingException, QmfException {
        super.testModifiedUndeliverableDispositionFixed();
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
