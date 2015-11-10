package jms;

import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;
import jms.Filtering;
import org.testng.annotations.Test;


public class TestFiltering extends Filtering {

    @org.testng.annotations.BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    @Test
    @Override
    public void testCorrelationIDFilteringAMQPStyle() throws JMSException, NamingException, InterruptedException {
        super.testCorrelationIDFilteringAMQPStyle();
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
