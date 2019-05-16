package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_4.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_3_4.utils.Utils;
import com.deutscheboerse.amqp.tests.Filtering;
import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestFiltering extends Filtering {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
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

    @Test(groups = { "disableInQpidJava" })
    @Override
    public void testMessageIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testMessageIDFilteringJMSStyle();
    }

    @Test(groups = { "disableInMRG" })
    @Override
    public void testMessageIDFilteringJMSStyleJavaBroker() throws JMSException, NamingException, InterruptedException {
        super.testMessageIDFilteringJMSStyleJavaBroker();
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

    @Test(groups = { "disableInQpidJava" })
    @Override
    public void testTimestampFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringJMSStyle();
    }

    @Test(groups = { "disableInMRG" })
    @Override
    public void testTimestampFilteringJMSStyleJavaBroker() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringJMSStyleJavaBroker();
    }
}
