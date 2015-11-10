package jms;

import utils.AbstractUtils;

public abstract class BaseTest {

    protected AbstractUtils utils;
    
    public void prepare(AbstractUtils utils) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        
        this.utils = utils;
    }

}
