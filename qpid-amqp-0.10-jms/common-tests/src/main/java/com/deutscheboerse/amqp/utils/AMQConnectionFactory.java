package com.deutscheboerse.amqp.utils;

import org.apache.qpid.url.URLSyntaxException;

public class AMQConnectionFactory extends org.apache.qpid.client.AMQConnectionFactory {
    public void setBrokerUrl(String brokerUrl) {
        if (brokerUrl == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        try {
            setConnectionURLString(brokerUrl);
        } catch (URLSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
