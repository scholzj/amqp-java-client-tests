package com.deutscheboerse.utils;

import org.apache.qpid.qmf2.common.QmfData;
import org.apache.qpid.qmf2.common.QmfException;
import org.apache.qpid.qmf2.console.Console;
import org.apache.qpid.qmf2.console.QmfConsoleData;
import org.apache.qpid.qmf2.util.ConnectionHelper;

import javax.jms.Connection;

import com.deutscheboerse.configuration.Settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by schojak on 7.10.15.
 */
@SuppressWarnings("ALL")
public class GlobalUtils
{
    private static Console qmfConsole = null;
    
    private static void initialize() throws QmfException
    {
        String connectionUrl = String.format("%1$s/%2$s@%3$s:%4$s", Settings.get("admin.username"), Settings.get("admin.password"), Settings.get("broker.hostname"), Settings.get("broker.tcp_port"));
        Connection qmfConnection = ConnectionHelper.createConnection(connectionUrl, "{reconnect: true}");

        qmfConsole = new Console();
        qmfConsole.disableEvents(); // Optimization, as we're only doing getObjects() calls.
        qmfConsole.addConnection(qmfConnection);
    }
    
    public static void purgeQueue(String queueName) throws QmfException
    {
        if (qmfConsole == null)
        {
            initialize();
        }

        List<QmfConsoleData> queues = qmfConsole.getObjects("org.apache.qpid.broker", "queue");
        for (QmfConsoleData queue : queues)
        {
            String name = queue.getStringValue("name");
            if (name.equals(queueName))
            {
                long msgDepth = queue.getLongValue("msgDepth");
                if (msgDepth > 0)
                {
                    Map<String, Object> inArgs = new HashMap<>();
                    inArgs.put("request", 0);
                    QmfData purgeArgs = new QmfData(inArgs);
                    queue.invokeMethod("purge", purgeArgs);
                }
            }
        }
    }
}
