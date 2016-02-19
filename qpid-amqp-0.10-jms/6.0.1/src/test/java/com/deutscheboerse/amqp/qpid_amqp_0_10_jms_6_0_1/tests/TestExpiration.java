package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.tests;

import com.deutscheboerse.amqp.tests.Expiration;
import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_1.utils.Utils;

public class TestExpiration extends Expiration {
    
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
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        super.testMessageExpiration();
    }
}
