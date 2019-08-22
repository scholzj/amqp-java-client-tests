package com.deutscheboerse.amqp.qpid_jms_0_46_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.deutscheboerse.amqp.qpid_jms_0_46_0.utils.Utils;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.tests.Filtering;

public class TestFiltering extends Filtering {

    @org.testng.annotations.BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test(groups = { "disableInMRG-3.0.0", "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testCorrelationIDFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testCorrelationIDFilteringAMQPStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0" })
    @Override
    public void testCorrelationIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testCorrelationIDFilteringJMSStyle();
    }

    @Test(groups = { "disableInMRG-3.0.0", "disableInArtemis" })
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

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testMessageIDFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testMessageIDFilteringAMQPStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInArtemis" })
    @Override
    public void testMessageIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testMessageIDFilteringJMSStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testPriorityFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testPriorityFilteringAMQPStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInArtemis" })
    public void testPriorityFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testPriorityFilteringJMSStyle();
    }

    @Test(groups = { "disableInQpidJava", "disableInMRG" })
    public void testPriorityFilteringJMSStyleArtemis() throws JMSException, NamingException, InterruptedException {
        super.testPriorityFilteringJMSStyleArtemis();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testSubjectFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testSubjectFilteringAMQPStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0" })
    @Override
    public void testSubjectFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testSubjectFilteringJMSStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInQpidJava", "disableInArtemis" })
    @Override
    public void testTimestampFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringAMQPStyle();
    }

    @Test(groups = { "disableInQpid0.34", "disableInMRG-3.0.0", "disableInArtemis" })
    public void testTimestampFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringJMSStyle();
    }

    @Test(groups = { "disableInQpidJava", "disableInMRG" })
    public void testTimestampFilteringJMSStyleArtemis() throws JMSException, NamingException, InterruptedException {
        super.testTimestampFilteringJMSStyleArtemis();
    }
}
