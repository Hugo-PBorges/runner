package com.ufg.runner.assinador.shutdown;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class ActivityInterceptor implements HandlerInterceptor {

    private final ActivityTracker activityTracker;

    public ActivityInterceptor(ActivityTracker activityTracker) {
        this.activityTracker = activityTracker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        activityTracker.touch();
        return true;
    }
}
