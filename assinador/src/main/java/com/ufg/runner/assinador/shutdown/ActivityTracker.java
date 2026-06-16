package com.ufg.runner.assinador.shutdown;

import java.util.concurrent.atomic.AtomicLong;

public class ActivityTracker {

    private final AtomicLong lastActivity = new AtomicLong(System.currentTimeMillis());

    public void touch() {
        lastActivity.set(System.currentTimeMillis());
    }

    public long millisSinceLastActivity() {
        return System.currentTimeMillis() - lastActivity.get();
    }
}
