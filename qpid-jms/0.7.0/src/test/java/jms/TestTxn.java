package jms;

import com.deutscheboerse.utils.GlobalUtils;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.Utils;

@Test(groups = { "disableInMRG-3.0.0" })
public class TestTxn extends Txn {
    
    @BeforeClass
    public void prepare() {
        super.prepare(new Utils());
    }
    
    @BeforeMethod
    public void deleteAllQueues() {
        GlobalUtils.getInstance().purgeAllQueues();
    }
    
    // Test the commit feature
    @Test
    @Override
    public void testTxnCommit() throws JMSException, NamingException {
        super.testTxnCommit();
    }
    
    // Test the sender rollback feature
    @Test
    @Override
    public void testTxnSenderRollback() throws JMSException, NamingException {
        super.testTxnSenderRollback();
    }
    
    // Test the receiver rollback feature
    @Test
    @Override
    public void testTxnReceiverRollback() throws JMSException, NamingException {
        super.testTxnReceiverRollback();
    }
    
    // Test the commit feature with large commit
    @Test
    @Override
    public void testTxnCommitLotOfMessages() throws JMSException, NamingException {
        super.testTxnCommitLotOfMessages();
    }
}
