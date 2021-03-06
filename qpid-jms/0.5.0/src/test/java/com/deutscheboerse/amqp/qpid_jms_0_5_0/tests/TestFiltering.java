package com.deutscheboerse.amqp.qpid_jms_0_5_0.tests;

import com.deutscheboerse.amqp.tests.Filtering;
import com.deutscheboerse.amqp.qpid_jms_0_5_0.utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import org.testng.annotations.Test;

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

    @Test
    @Override
    public void testPropertiesFilteringWithoutPeriod() throws JMSException, NamingException, InterruptedException {
        super.testPropertiesFilteringWithoutPeriod();
    }

}
