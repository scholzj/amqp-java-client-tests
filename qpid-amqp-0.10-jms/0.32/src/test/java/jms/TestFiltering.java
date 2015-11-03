package jms;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestFiltering extends Filtering {

    @BeforeClass
    @Override
    public void prepare() {
        super.prepare();
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
