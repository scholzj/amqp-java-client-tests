package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.commons.CommonBaseTest;
import com.deutscheboerse.amqp.utils.AbstractUtils;

public abstract class BaseTest extends CommonBaseTest {

    protected AbstractUtils utils;

    public void prepare(AbstractUtils utils) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        this.utils = utils;
    }
}
