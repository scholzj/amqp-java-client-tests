package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.utils.Utils;

public class TestFiltering extends Filtering {
    
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
    public void testCorrelationIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testCorrelationIDFilteringJMSStyle();
    }
    
    @Test
    @Override
    public void testPropertiesFilteringWithPeriod() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringWithPeriod();
    }
    
    @Test
    @Override
    public void testPropertiesFilteringWithoutPeriod() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringWithoutPeriod();
    }
}
