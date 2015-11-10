package jms;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

public class TestLVQ extends LVQ {

    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
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
