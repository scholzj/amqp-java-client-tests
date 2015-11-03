package jms;

import org.apache.qpid.qmf2.common.QmfException;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestReadOnly extends ReadOnly {
    
    @BeforeClass
    @Override
    public void prepare() {
        super.prepare();
    }
    
    // Test the read only queue feature
    @Test
    @Override
    public void testReadOnlyQueue() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueue();
    }
    
    // Test the read only queue feature with transaction reader
    @Test
    @Override
    public void testReadOnlyQueueWithTxn() throws JMSException, NamingException, QmfException {
        super.testReadOnlyQueueWithTxn();
    }
}
