package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.APIResponse;
import org.prkguides.blog.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = {"http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get post analytics", description = "Get analytics data for a specific post")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<APIResponse<Map<String, Object>>> getPostAnalytics(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @RequestParam(value = "days", defaultValue = "30") int days) {

        Map<String, Object> analytics = analyticsService.getPostAnalytics(postId, days);
        return ResponseEntity.ok(APIResponse.success("Post analytics retrieved successfully", analytics));
    }

    @Operation(summary = "Get traffic analytics", description = "Get overall traffic analytics")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/traffic")
    public ResponseEntity<APIResponse<Map<String, Object>>> getTrafficAnalytics(
            @RequestParam(value = "days", defaultValue = "30") int days) {

        Map<String, Object> analytics = analyticsService.getTrafficAnalytics(days);
        return ResponseEntity.ok(APIResponse.success("Traffic analytics retrieved successfully", analytics));
    }

    @Operation(summary = "Get user engagement analytics", description = "Get user engagement metrics")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/engagement")
    public ResponseEntity<APIResponse<Map<String, Object>>> getEngagementAnalytics(
            @RequestParam(value = "days", defaultValue = "30") int days) {

        Map<String, Object> analytics = analyticsService.getEngagementAnalytics(days);
        return ResponseEntity.ok(APIResponse.success("Engagement analytics retrieved successfully", analytics));
    }

    @Operation(summary = "Get content performance", description = "Get content performance metrics")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/content-performance")
    public ResponseEntity<APIResponse<Map<String, Object>>> getContentPerformance(
            @RequestParam(value = "days", defaultValue = "30") int days) {

        Map<String, Object> analytics = analyticsService.getContentPerformance(days);
        return ResponseEntity.ok(APIResponse.success("Content performance retrieved successfully", analytics));
    }

    @Operation(summary = "Get tag analytics", description = "Get tag usage and performance analytics")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tags")
    public ResponseEntity<APIResponse<Map<String, Object>>> getTagAnalytics() {

        Map<String, Object> analytics = analyticsService.getTagAnalytics();
        return ResponseEntity.ok(APIResponse.success("Tag analytics retrieved successfully", analytics));
    }

    @Operation(summary = "Track page view", description = "Track a page view (public endpoint)")
    @PostMapping("/track-view")
    public ResponseEntity<APIResponse<String>> trackPageView(
            @RequestParam String page,
            @RequestParam(required = false) String referrer,
            @RequestParam(required = false) String userAgent) {

        analyticsService.trackPageView(page, referrer, userAgent);
        return ResponseEntity.ok(APIResponse.success("Page view tracked", "View recorded"));
    }

    @Operation(summary = "Get popular content", description = "Get most popular content")
    @GetMapping("/popular")
    public ResponseEntity<APIResponse<Map<String, Object>>> getPopularContent(
            @RequestParam(value = "period", defaultValue = "week") String period,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        Map<String, Object> popular = analyticsService.getPopularContent(period, limit);
        return ResponseEntity.ok(APIResponse.success("Popular content retrieved successfully", popular));
    }
}
