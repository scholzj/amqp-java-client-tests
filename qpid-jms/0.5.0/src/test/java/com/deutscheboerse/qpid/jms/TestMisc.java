package com.deutscheboerse.qpid.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;
import jms.Misc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

public class TestMisc extends Misc {
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testDuplicateClientID() throws JMSException, NamingException {
        super.testDuplicateClientID();
    }
}
