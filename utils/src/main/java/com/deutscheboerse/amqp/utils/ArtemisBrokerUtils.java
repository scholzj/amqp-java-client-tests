package com.deutscheboerse.amqp.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.management.ObjectNameBuilder;
import org.apache.activemq.artemis.api.jms.management.JMSQueueControl;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;

public class ArtemisBrokerUtils extends GlobalUtils {

    private static final ArtemisBrokerUtils INSTANCE = new ArtemisBrokerUtils();

    private final MBeanServerConnection connection;

    private ArtemisBrokerUtils() {
        try {
            String jmxUrl = "service:jmx:rmi:///jndi/rmi://" +
                    Settings.get("broker.hostname") + ":" + Settings.get("broker.jmx_port") + "/jmxrmi";
            JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl), new HashMap());
            this.connection = connector.getMBeanServerConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArtemisBrokerUtils getInstance() {
        return INSTANCE;
    }

    public void purgeAllQueues() {
        for (String queueName : queuesToBeDeleted) {
            try {
                purgeQueue(queueName);
                Thread.sleep(1000);
                checkDeletion(queueName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkDeletion(String queueName) throws Exception {
        ObjectName name = ObjectNameBuilder.create(null, Settings.get("broker.name")).getQueueObjectName(new SimpleString(queueName), new SimpleString(queueName));
        long messageCount = MBeanServerInvocationHandler.newProxyInstance(connection, name, JMSQueueControl.class, false).listMessages("").length;
        while (messageCount > 0) {
            System.out.println("There are '" + messageCount + "' messages still in queue '" + queueName + "'");
            MBeanServerInvocationHandler.newProxyInstance(connection, name, JMSQueueControl.class, false).removeMessages("");
            Thread.sleep(300);
        }
    }

    public void purgeQueue(String queueName) throws Exception {
        ObjectName name = ObjectNameBuilder.create(null, Settings.get("broker.name")).getQueueObjectName(new SimpleString(queueName), new SimpleString(queueName));
        MBeanServerInvocationHandler.newProxyInstance(connection, name, JMSQueueControl.class, false).removeMessages("");
    }

}
