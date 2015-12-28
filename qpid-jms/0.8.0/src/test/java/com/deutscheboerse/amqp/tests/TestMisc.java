package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import com.deutscheboerse.amqp.utils.Utils;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestMisc extends Misc {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    // Test the sender rollback feature
    @Test
    @Override
    public void testDuplicateClientID() throws JMSException, NamingException {
        super.testDuplicateClientID();
    }
    
    @Test
    @Override
    public void testMessageIDFormatUUID() throws JMSException, NamingException, QmfException {
        super.testMessageIDFormatUUID();
    }
    
    @Test
    @Override
    public void testMessageIDFormatUUIDString() throws JMSException, NamingException, QmfException {
        super.testMessageIDFormatUUIDString();
    }
    
}
