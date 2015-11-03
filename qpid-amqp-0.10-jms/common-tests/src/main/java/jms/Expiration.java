package jms;

import com.deutscheboerse.configuration.Settings;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import utils.AutoCloseableConnection;
import utils.Utils;

import static org.testng.AssertJUnit.assertNull;

public class Expiration extends BaseTest {

    private static final int EXPIRATION_TIME = 500; // milliseconds
    private static final String TTL_QUEUE = Settings.get("routing.ttl_queue");

    @Override
    public void prepare() {
        super.prepare();
    }

    // Test the sender rollback feature
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = Utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(Utils.getQueue(TTL_QUEUE));
            sender.setTimeToLive(EXPIRATION_TIME);
            sender.send(session.createMessage());

            Thread.sleep(2 * EXPIRATION_TIME);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(TTL_QUEUE));
            Message received = receiver.receive(1000);
            assertNull("Received unexpected message", received);
        }
    }
}
