package jms;

import utils.Utils;

import javax.jms.JMSException;
import javax.naming.NamingException;
import jms.Failover;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestFailover extends Failover {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testPlainFailover() throws JMSException, NamingException, InterruptedException {
        super.testPlainFailover();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testPlainFailoverNested() throws JMSException, NamingException, InterruptedException {
        super.testPlainFailoverNested();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testSSLFailover() throws JMSException, NamingException, InterruptedException {
        super.testSSLFailover();
    }

    // This doesn't test the actual failover - just connecting using the failover URI
    @Test
    @Override
    public void testSSLFailoverNested() throws JMSException, NamingException, InterruptedException {
        super.testSSLFailoverNested();
    }
}
