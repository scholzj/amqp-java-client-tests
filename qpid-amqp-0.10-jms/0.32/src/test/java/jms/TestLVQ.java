package jms;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestLVQ extends LVQ {

    @BeforeClass
    @Override
    public void prepare() {
        super.prepare();
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueBasic() throws JMSException, NamingException {
        super.testLVQQueueBasic();
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        super.testLVQQueueManyMessages();
    }

    // Test the LVQ feature
    @Test
    @Override
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        super.testLVQQueueInTxn();
    }
}
