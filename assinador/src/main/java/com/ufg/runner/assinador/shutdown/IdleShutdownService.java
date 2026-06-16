package com.ufg.runner.assinador.shutdown;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IdleShutdownService {

    private final ActivityTracker activityTracker;
    private final ConfigurableApplicationContext applicationContext;
    private final long idleTimeoutMillis;

    public IdleShutdownService(
            ActivityTracker activityTracker,
            ConfigurableApplicationContext applicationContext,
            @Value("${idle-timeout:0}") long idleTimeoutMinutes
    ) {
        this.activityTracker = activityTracker;
        this.applicationContext = applicationContext;
        this.idleTimeoutMillis = TimeUnit.MINUTES.toMillis(idleTimeoutMinutes);
    }

    @Scheduled(fixedDelay = 30_000)
    public void checkIdle() {
        if (idleTimeoutMillis <= 0) {
            return;
        }

        if (activityTracker.millisSinceLastActivity() >= idleTimeoutMillis) {
            SpringApplication.exit(applicationContext, () -> 0);
            System.exit(0);
        }
    }
}
