package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.utils.GlobalUtils;
import utils.Utils;

import javax.jms.JMSException;
import javax.naming.NamingException;
import jms.Heartbeat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestHeartbeat extends Heartbeat {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    // Test the idle timeout
    @Test
    @Override
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
        super.testHeartbeat();
    }
    
}
