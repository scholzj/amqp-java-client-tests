package com.deutscheboerse.amqp.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import org.apache.qpid.qmf2.common.QmfData;
import org.apache.qpid.qmf2.common.QmfException;
import org.apache.qpid.qmf2.console.Console;
import org.apache.qpid.qmf2.console.QmfConsoleData;
import org.apache.qpid.qmf2.util.ConnectionHelper;

public class CppBrokerUtils extends GlobalUtils {

    private static final CppBrokerUtils INSTANCE = new CppBrokerUtils();
    private final Console qmfConsole;

    private CppBrokerUtils() {
        try {
            String connectionUrl = String.format("%1$s/%2$s@%3$s:%4$s", Settings.get("admin.username"), Settings.get("admin.password"), Settings.get("broker.hostname"), Settings.get("broker.tcp_port"));
            Connection qmfConnection = ConnectionHelper.createConnection(connectionUrl, "{reconnect: true, sync_publish: all, sync_ack: true}");

            qmfConsole = new Console();
            qmfConsole.disableEvents(); // Optimization, as we're only doing getObjects() calls.
            qmfConsole.addConnection(qmfConnection);
        } catch (QmfException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static CppBrokerUtils getInstance() {
        return INSTANCE;
    }

    public void purgeAllQueues() {
        List<QmfConsoleData> queues = qmfConsole.getObjects("org.apache.qpid.broker", "queue");
        for (QmfConsoleData queue : queues) {
            String name = queue.getStringValue("name");
            if (this.queuesToBeDeleted.contains(name)) {
                long msgDepth = queue.getLongValue("msgDepth");
                if (msgDepth > 0) {
                    try {
                        Map<String, Object> inArgs = new HashMap<>();
                        inArgs.put("request", 0);
                        QmfData purgeArgs = new QmfData(inArgs);
                        queue.invokeMethod("purge", purgeArgs);
                    } catch (QmfException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void purgeQueue(String queueName) throws QmfException {
        List<QmfConsoleData> queues = qmfConsole.getObjects("org.apache.qpid.broker", "queue");
        for (QmfConsoleData queue : queues) {
            String name = queue.getStringValue("name");
            if (name.equals(queueName)) {
                long msgDepth = queue.getLongValue("msgDepth");
                if (msgDepth > 0) {
                    Map<String, Object> inArgs = new HashMap<>();
                    inArgs.put("request", 0);
                    QmfData purgeArgs = new QmfData(inArgs);
                    queue.invokeMethod("purge", purgeArgs);
                }
            }
        }
    }

}
