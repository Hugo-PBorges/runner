package com.ufg.runner.assinador.shutdown;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ActivityTracker activityTracker() {
        return new ActivityTracker();
    }

    @Bean
    public ActivityInterceptor activityInterceptor(ActivityTracker activityTracker) {
        return new ActivityInterceptor(activityTracker);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(activityInterceptor(activityTracker()));
    }
}
