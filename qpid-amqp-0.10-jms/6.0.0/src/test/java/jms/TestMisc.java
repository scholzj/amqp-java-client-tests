package jms;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMisc extends Misc {

    @BeforeClass
    @Override
    public void prepare() {
        super.prepare();
    }

    // Test the sender rollback feature
    @Test
    @Override
    public void testDuplicateClientID() throws JMSException, NamingException {
        super.testDuplicateClientID();
    }
}
