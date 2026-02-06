package com.shirt.pod.service.impl;

import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import com.shirt.pod.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderService orderService;

    @Bean
    public Task<Void> processPendingOrdersTask() {
        return Tasks.recurring("process-pending-orders-task", FixedDelay.of(Duration.ofMinutes(5)))
                .execute((instance, ctx) -> {
                    log.info("=== DB-SCHEDULER: Starting scheduled task ===");
                    try {
                        orderService.processPendingOrders();
                        log.info("=== DB-SCHEDULER: Completed successfully ===");
                    } catch (Exception e) {
                        log.error("=== DB-SCHEDULER: Failed with error ===", e);
                    }
                });
    }
}
