package com.okayjam.web.configuration;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步配置类
 * 配置能够传递 MDC 上下文的线程池，解决异步调用中 TRACE_ID 丢失的问题
 * 支持根据配置自动选择虚拟线程或传统线程池
 *
 * @author JamChen
 * @date 2026/01/06
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    @Value("${spring.threads.virtual.enabled:false}")
    private boolean virtualThreadsEnabled;

    /**
     * 配置异步任务执行器
     * 根据配置自动选择虚拟线程执行器或传统线程池
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        if (virtualThreadsEnabled) {
            return createVirtualThreadExecutor();
        }
        return createThreadPoolExecutor();
    }

    /**
     * 创建虚拟线程执行器
     */
    private Executor createVirtualThreadExecutor() {
        TaskExecutorAdapter executor = new TaskExecutorAdapter(
                Executors.newVirtualThreadPerTaskExecutor()
        );
        // 设置任务装饰器，用于传递 MDC 上下文
        executor.setTaskDecorator(new MdcTaskDecorator());
        return executor;
    }

    /**
     * 创建传统线程池执行器
     */
    private Executor createThreadPoolExecutor() {
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