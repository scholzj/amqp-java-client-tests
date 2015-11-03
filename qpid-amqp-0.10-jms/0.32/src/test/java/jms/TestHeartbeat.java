package jms;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestHeartbeat extends Heartbeat {
    
    @BeforeClass
    public void prepare() {
        super.prepare();
    }
    
    // Test the idle timeout
    @Test
    @Override
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
        super.testHeartbeat();
    }
}
