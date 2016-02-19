package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.tests;

import com.deutscheboerse.amqp.tests.Filtering;
import com.deutscheboerse.amqp.utils.GlobalUtils;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_0.utils.Utils;

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

    @Test
    @Override
    public void testPropertiesFilteringIn() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringIn();
    }

    @Test
    @Override
    public void testPropertiesFilteringBetween() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringBetween();
    }

    @Test
    @Override
    public void testPropertiesFilteringLike() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringLike();
    }

    @Test
    @Override
    public void testPropertiesFilteringAnd() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringAnd();
    }

    @Test
    @Override
    public void testPropertiesFilteringOr() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringOr();
    }

    @Test
    @Override
    public void testMessageIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testMessageIDFilteringJMSStyle();
    }

    @Test
    @Override
    public void testPriorityFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testPriorityFilteringJMSStyle();
    }

    @Test
    @Override
    public void testJMSTypeFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testJMSTypeFilteringJMSStyle();
    }

    @Test
    @Override
    public void testRoutingKeyFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testRoutingKeyFilteringJMSStyle();
    }

    @Test
    @Override
    public void testTimestampFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringJMSStyle();
    }
}
