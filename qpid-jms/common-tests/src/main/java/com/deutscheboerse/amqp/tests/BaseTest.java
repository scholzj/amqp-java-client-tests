package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.AbstractUtils;
import com.deutscheboerse.amqp.utils.ArtemisBrokerUtils;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.utils.JavaBrokerUtils;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class BaseTest {

    protected AbstractUtils utils;

    public void prepare(AbstractUtils utils) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        this.utils = utils;
    }

    @BeforeMethod(groups = { "disableInQpidJava", "disableInArtemis" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @BeforeMethod(groups = { "disableInMRG", "disableInArtemis" })
    public void clearAllQueues() throws IllegalAccessException {
        JavaBrokerUtils.getInstance().clearAllQueues();
    }

    @BeforeMethod
    public void handleTestMethodName(Method method)
    {
        System.out.println("Test method = [" + method.getName() + "]");
    }

    @BeforeMethod(groups = { "disableInMRG", "disableInQpidJava" })
    public void clearAllQueuesArtemis() {
        ArtemisBrokerUtils.getInstance().purgeAllQueues();
    }
}
