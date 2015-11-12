package com.deutscheboerse.utils;

import org.apache.qpid.qmf2.common.QmfData;
import org.apache.qpid.qmf2.common.QmfException;
import org.apache.qpid.qmf2.console.Console;
import org.apache.qpid.qmf2.console.QmfConsoleData;
import org.apache.qpid.qmf2.util.ConnectionHelper;

import javax.jms.Connection;

import com.deutscheboerse.configuration.Settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalUtils {
    
    private static final GlobalUtils instance = new GlobalUtils();
    
    private final Console qmfConsole;
    private final Set<String> queuesToBeDeleted = new HashSet<>();
    
    private GlobalUtils() {
        queuesToBeDeleted.add(Settings.get("routing.ttl_queue"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_queue"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_topic"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_routing_key"));
        queuesToBeDeleted.add(Settings.get("routing.lvq_queue"));
        queuesToBeDeleted.add(Settings.get("routing.lvq_key"));
        queuesToBeDeleted.add(Settings.get("routing.small_queue"));
        queuesToBeDeleted.add(Settings.get("routing.ring_queue"));
        queuesToBeDeleted.add(Settings.get("routing.dlq_queue"));
        queuesToBeDeleted.add(Settings.get("routing.dlq_topic"));
        queuesToBeDeleted.add(Settings.get("routing.dlq_routing_key"));
        queuesToBeDeleted.add(Settings.get("routing.txn_queue"));
        queuesToBeDeleted.add(Settings.get("routing.forbidden_queue"));
        queuesToBeDeleted.add(Settings.get("routing.forbidden_topic"));
        queuesToBeDeleted.add(Settings.get("routing.forbidden_routing_key"));
        queuesToBeDeleted.add(Settings.get("routing.read_only_queue"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_queue"));
        
        
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
    
    public static GlobalUtils getInstance() {
        return instance;
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
