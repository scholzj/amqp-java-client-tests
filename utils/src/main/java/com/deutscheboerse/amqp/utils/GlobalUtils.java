package com.deutscheboerse.amqp.utils;


import com.deutscheboerse.amqp.configuration.Settings;
import java.util.ArrayList;

import java.util.List;

public abstract class GlobalUtils {

    protected final List<String> queuesToBeDeleted;

    protected GlobalUtils() {
        this.queuesToBeDeleted = new ArrayList<>();
        queuesToBeDeleted.add(Settings.get("routing.ttl_queue"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_queue"));
        queuesToBeDeleted.add(Settings.get("routing.lvq_queue"));
        queuesToBeDeleted.add(Settings.get("routing.small_queue"));
        queuesToBeDeleted.add(Settings.get("routing.ring_queue"));
        queuesToBeDeleted.add(Settings.get("routing.dlq_queue"));
        queuesToBeDeleted.add(Settings.get("routing.txn_queue"));
        queuesToBeDeleted.add(Settings.get("routing.forbidden_queue"));
        queuesToBeDeleted.add(Settings.get("routing.read_only_queue"));
        queuesToBeDeleted.add(Settings.get("routing.rtg_queue"));
        queuesToBeDeleted.add(Settings.get("routing.response_fixed_queue"));
        queuesToBeDeleted.add(Settings.get("routing.request_queue"));
    }
}
