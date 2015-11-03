package jms;

import com.deutscheboerse.configuration.Settings;
import utils.Utils;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;
import utils.AutoCloseableConnection;

public class Heartbeat extends BaseTest {
    private static final Integer HEARTBEAT = 1; //seconds
    private static final Integer WAIT_TIME = 5000; //milliseconds
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    public void prepare() {
        super.prepare();
    }
    
    // Test the idle timeout
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = Utils.getAdminConnectionBuilder().brokerOption("heartbeat='" + HEARTBEAT.toString() + "'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            receiver.receive(1);
            Thread.sleep(WAIT_TIME);
            receiver.receive(1);
        }
    }
}
