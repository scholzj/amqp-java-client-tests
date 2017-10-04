package com.deutscheboerse.amqp.qpid_jms_0_27_0.tests;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deutscheboerse.amqp.qpid_jms_0_27_0.utils.Utils;
import com.deutscheboerse.amqp.tests.Heartbeat;

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
