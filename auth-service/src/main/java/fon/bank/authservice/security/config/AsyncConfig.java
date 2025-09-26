package fon.bank.authservice.security.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    static class MdcTaskDecorator implements TaskDecorator {
        @Override public Runnable decorate(Runnable r) {
            Map<String,String> ctx = MDC.getCopyOfContextMap();
            return () -> {
                Map<String,String> prev = MDC.getCopyOfContextMap();
                if (ctx != null) MDC.setContextMap(ctx); else MDC.clear();
                try { r.run(); }
                finally { if (prev != null) MDC.setContextMap(prev); else MDC.clear(); }
            };
        }
    }

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        var ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(1000);
        ex.setThreadNamePrefix("mail-");
        ex.setTaskDecorator(new MdcTaskDecorator());
        ex.initialize();
        return ex;
    }
}
