package jms;

import com.deutscheboerse.configuration.Settings;
import com.deutscheboerse.utils.GlobalUtils;
import org.apache.qpid.qmf2.common.QmfException;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.Utils;

public class TestDisposition extends Disposition {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    @Test
    @Override
    public void testAcceptDisposition() throws JMSException, NamingException, QmfException {
        super.testAcceptDisposition();
    }
    
    // TODO: Rejected messages are discarded by the Qpid broker
    /*
    @Test
    @Override
    public void testRejectDisposition() throws JMSException, NamingException, QmfException {
    super.testReleasedDisposition();
    }*/
    
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
}
