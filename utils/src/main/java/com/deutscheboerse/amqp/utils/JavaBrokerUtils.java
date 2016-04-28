package com.deutscheboerse.amqp.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;

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
        List<String> queuesToCheck = new ArrayList<>();
        queuesToCheck.addAll(queuesToBeDeleted);
        while (!queuesToCheck.isEmpty()) {
            List<String> queuesWithMessages = new ArrayList<>();
            for (String queueName : queuesToCheck) {
                if (!this.queueIsEmpty(queueName)) {
                    queuesWithMessages.add(queueName);
                }
            }
            queuesToCheck.clear();
            queuesToCheck.addAll(queuesWithMessages);
        }
    }

    private boolean queueIsEmpty(String queueName) throws IllegalAccessException {
        Client client = this.getRestClient();
        WebTarget target = client.target(this.getInfoQueueRestUri(queueName));

        JsonArray response = target.request(APPLICATION_JSON).get(JsonArray.class);
        for (JsonObject jsonObject : response.getValuesAs(JsonObject.class)) {
            JsonObject statistics = jsonObject.getJsonObject("statistics");
            return statistics.getInt("queueDepthBytes") == 0;
        }
        return false;
    }

    public void clearQueue(String queueName) throws IllegalAccessException {
        Client client = this.getRestClient();
        WebTarget target = client.target(this.getClearQueueRestUri(queueName));

        Response response = target.request(APPLICATION_JSON).post(Entity.json("{}"));

        if (Response.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IllegalAccessException("Error while queue purge " + response);
        }
    }

    private Client getRestClient() {
        final String adminUserName = Settings.get("admin.username");
        final String adminUserPassword = Settings.get("admin.password");
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(adminUserName, adminUserPassword);
        Client client = ClientBuilder.newClient();
        client.register(feature);
        client.register(JsonProcessingFeature.class);
        return client;
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

    private URI getInfoQueueRestUri(String queueName) {
        final String virtualHostNode = Settings.get("broker.virtual_host_node");
        final String virtualHost = Settings.get("broker.virtual_host");
        return UriBuilder.fromUri("http://{host}/api/latest/queue/{vhn}/{vh}/{queueName}")
                .port(this.brokerPort)
                .resolveTemplate("host", this.brokerHost)
                .resolveTemplate("vhn", virtualHostNode)
                .resolveTemplate("vh", virtualHost)
                .resolveTemplateFromEncoded("queueName", queueName)
                .build();
    }

}
