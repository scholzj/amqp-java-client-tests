package com.deutscheboerse.amqp.qpid_jms_0_20_0.tests;

import com.deutscheboerse.amqp.qpid_jms_0_20_0.utils.Utils;
import com.deutscheboerse.amqp.tests.Heartbeat;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = { "disableInMRG-3.0.0" })
public class TestHeartbeat extends Heartbeat {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the idle timeout
    @Test
    @Override
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
        super.testHeartbeat();
    }

}
