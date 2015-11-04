package jms;

import javax.jms.JMSException;
import javax.naming.NamingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;

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
