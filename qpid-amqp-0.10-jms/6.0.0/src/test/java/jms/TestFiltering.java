package jms;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

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
}
