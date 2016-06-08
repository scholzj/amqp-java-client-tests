package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.commons.CommonBaseTest;
import com.deutscheboerse.amqp.utils.AbstractUtils;

public abstract class BaseTest extends CommonBaseTest {

    protected AbstractUtils utils;

    public void prepare(AbstractUtils utils) {
        /*Date d = new Date();
        System.setProperty("org.slf4j.simpleLogger.logFile", "/var/lib/qpidd/scholzj/hudson/qpidc.log." + d.toString());*/
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        System.setProperty("slf4j.logger.org.apache.qpid", "warn");

        this.utils = utils;
    }
}
