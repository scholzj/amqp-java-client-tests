package com.deutscheboerse.amqp.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class JavaBrokerUtils extends GlobalUtils {

    private static final JavaBrokerUtils INSTANCE = new JavaBrokerUtils();

    private final String brokerHost;
    private final int brokerPort;

    private JavaBrokerUtils() {
        this.brokerHost = Settings.get("broker.hostname");
        this.brokerPort = Integer.valueOf(Settings.get("broker.http_port"));
    }

    public static JavaBrokerUtils getInstance() {
        return INSTANCE;
    }

    public void clearAllQueues() throws IllegalAccessException {
        for (String queueName : queuesToBeDeleted) {
            this.clearQueue(queueName);
        }
    }

    public void clearQueue(String queueName) throws IllegalAccessException {
        final String adminUserName = Settings.get("admin.username");
        final String adminUserPassword = Settings.get("admin.password");
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(adminUserName, adminUserPassword);
        Client client = ClientBuilder.newClient();
        client.register(feature);
        WebTarget target = client.target(this.getClearQueueRestUri(queueName));

        Response response = target.request(APPLICATION_JSON).post(Entity.json("{}"));

        if (Response.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IllegalAccessException("Error while queue purge " + response);
        }
    }

    private URI getClearQueueRestUri(String queueName) {
        final String virtualHostNode = Settings.get("broker.virtual_host_node");
        final String virtualHost = Settings.get("broker.virtual_host");
        return UriBuilder.fromUri("http://{host}/api/latest/queue/{vhn}/{vh}/{queueName}/clearQueue")
                .port(this.brokerPort)
                .resolveTemplate("host", this.brokerHost)
                .resolveTemplate("vhn", virtualHostNode)
                .resolveTemplate("vh", virtualHost)
                .resolveTemplateFromEncoded("queueName", queueName)
                .build();
    }

}
