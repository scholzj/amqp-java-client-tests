package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.tests;

import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_0_8.utils.Utils;
import com.deutscheboerse.amqp.tests.Heartbeat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.naming.NamingException;

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
