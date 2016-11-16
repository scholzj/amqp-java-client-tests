package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_2_0.tests;

import com.deutscheboerse.amqp.tests.Heartbeat;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_2_0.utils.Utils;

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
