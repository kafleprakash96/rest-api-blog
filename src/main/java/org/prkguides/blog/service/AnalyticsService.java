package org.prkguides.blog.service;

import java.util.Map;

public interface AnalyticsService {

    Map<String, Object> getPostAnalytics(Long postId, int days);
    Map<String, Object> getTrafficAnalytics(int days);
    Map<String, Object> getEngagementAnalytics(int days);
    Map<String, Object> getContentPerformance(int days);
    Map<String, Object> getTagAnalytics();
    Map<String, Object> getPopularContent(String period, int limit);
    void trackPageView(String page, String referrer, String userAgent);
}
