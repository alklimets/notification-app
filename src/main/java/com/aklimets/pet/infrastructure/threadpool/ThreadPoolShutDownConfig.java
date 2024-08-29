package com.aklimets.pet.infrastructure.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ThreadPoolShutDownConfig {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @EventListener(ContextClosedEvent.class)
    public void shutdownExecutor() {
        threadPoolTaskExecutor.shutdown();
        log.info("Thread pool is down");
    }
}
