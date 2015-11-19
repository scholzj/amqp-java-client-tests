package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.utils.Utils;

public class TestExpiration extends Expiration {
    
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
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        super.testMessageExpiration();
    }
}
