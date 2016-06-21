package com.deutscheboerse.amqp.commons;

import com.deutscheboerse.amqp.utils.ArtemisBrokerUtils;
import com.deutscheboerse.amqp.utils.CppBrokerUtils;
import com.deutscheboerse.amqp.utils.JavaBrokerUtils;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class CommonBaseTest {
    @BeforeMethod(groups = { "disableInQpidJava", "disableInArtemis" })
    public void deleteAllQueues() {
        CppBrokerUtils.getInstance().purgeAllQueues();
    }

    @BeforeMethod(groups = { "disableInMRG", "disableInArtemis" })
    public void clearAllQueues() throws IllegalAccessException {
        JavaBrokerUtils.getInstance().clearAllQueues();
    }

    @BeforeMethod
    public void handleTestMethodName(Method method) {
        System.out.println("Test method = [" + method.getName() + "]");
    }

    @BeforeMethod(groups = { "disableInMRG", "disableInQpidJava" })
    public void clearAllQueuesArtemis() {
        ArtemisBrokerUtils.getInstance().purgeAllQueues();
    }
}
