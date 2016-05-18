package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.utils.AbstractUtils;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.utils.JavaBrokerUtils;
import org.testng.annotations.BeforeMethod;

import java.util.Date;

public abstract class BaseTest {

    protected AbstractUtils utils;

    public void prepare(AbstractUtils utils) {
        Date d = new Date();
        System.setProperty("org.slf4j.simpleLogger.logFile", "/var/lib/qpidd/scholzj/hudson/qpidc.log." + d.toString());
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        System.setProperty("slf4j.logger.org.apache.qpid", "trace");

        this.utils = utils;
    }

    @BeforeMethod(groups = { "disableInQpidJava" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @BeforeMethod(groups = { "disableInMRG" })
    public void clearAllQueues() throws IllegalAccessException {
        JavaBrokerUtils.getInstance().clearAllQueues();
    }
}
