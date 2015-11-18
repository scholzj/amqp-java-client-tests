package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.utils.GlobalUtils;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;
import jms.Filtering;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestFiltering extends Filtering {
    
    @org.testng.annotations.BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }

    @Test(groups = { "disableInMRG-3.0.0" })
    @Override
    public void testCorrelationIDFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testCorrelationIDFilteringAMQPStyle();
    }
    
    @Test
    @Override
    public void testPropertiesFilteringWithoutPeriod() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringWithoutPeriod();
    }
    
}
