package com.okayjam.web.core.configuration;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步配置类
 * 配置能够传递 MDC 上下文的线程池，解决异步调用中 TRACE_ID 丢失的问题
 *
 * @author JamChen
 * @date 2026/01/06
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 配置异步任务执行器
     * 该执行器能够传递 MDC 上下文到异步线程
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(AVAILABLE_PROCESSORS * 2);
        // 最大线程数
        executor.setMaxPoolSize(AVAILABLE_PROCESSORS * 2);
        // 队列容量
        executor.setQueueCapacity(1000);
        // 线程名前缀
        executor.setThreadNamePrefix("Async-Thread-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 设置任务装饰器，用于传递 MDC 上下文
        executor.setTaskDecorator(new MdcTaskDecorator());

        executor.initialize();
        return executor;
    }

    /**
     * MDC 任务装饰器
     * 用于在异步任务执行前传递 MDC 上下文
     */
    public static class MdcTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            // 获取当前线程的 MDC 上下文
            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            return () -> {
                try {
                    // 在异步线程中恢复 MDC 上下文
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    // 执行原始任务
                    runnable.run();
                } finally {
                    // 清理 MDC 上下文
                    MDC.clear();
                }
            };
        }
    }
}