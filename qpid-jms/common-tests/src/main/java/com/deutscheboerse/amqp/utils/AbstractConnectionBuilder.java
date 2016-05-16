package com.deutscheboerse.amqp.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.jms.JMSException;
import javax.naming.NamingException;

public abstract class AbstractConnectionBuilder {
    protected String hostname;
    protected String port;
    protected String clientID;
    protected String username;
    protected String password;
    protected String keystore;
    protected String keystorePassword;
    protected String keystoreAlias;
    protected String truststore;
    protected String truststorePassword;
    protected Boolean ssl = false;
    protected Boolean syncPublish;
    protected final Set<String> brokerOptions = new HashSet<>();
    
    public AbstractConnectionBuilder ssl() {
        this.ssl = true;
        return this;
    }
    
    public AbstractConnectionBuilder hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }
    
    public AbstractConnectionBuilder port(String port) {
        this.port = port;
        return this;
    }
    
    public AbstractConnectionBuilder clientID(String clientID) {
        this.clientID = clientID;
        return this;
    }
    
    public AbstractConnectionBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public AbstractConnectionBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public AbstractConnectionBuilder keystore(String keystore) {
        this.keystore = keystore;
        return this;
    }
    
    public AbstractConnectionBuilder keystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }
    
    public AbstractConnectionBuilder keystoreAlias(String keystoreAlias) {
        this.keystoreAlias = keystoreAlias;
        return this;
    }
    
    public AbstractConnectionBuilder truststore(String truststore) {
        this.truststore = truststore;
        return this;
    }
    
    public AbstractConnectionBuilder truststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
        return this;
    }
    
    
    public AbstractConnectionBuilder syncPublish(Boolean syncPublish) {
        this.syncPublish = syncPublish;
        return this;
    }
    
    public AbstractConnectionBuilder brokerOption(String brokerOption) {
        this.brokerOptions.add(brokerOption);
        return this;
    }
    
    protected abstract String url();
    
    public abstract AutoCloseableConnection build() throws NamingException, JMSException;
    
}
