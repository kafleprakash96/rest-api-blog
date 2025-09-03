package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dashboard statistics")
public class DashboardStatsDto {

    @Schema(description = "Total number of posts", example = "150")
    private Long totalPosts;

    @Schema(description = "Number of published posts", example = "120")
    private Long publishedPosts;

    @Schema(description = "Number of draft posts", example = "25")
    private Long draftPosts;

    @Schema(description = "Number of scheduled posts", example = "5")
    private Long scheduledPosts;

    @Schema(description = "Total number of users", example = "50")
    private Long totalUsers;

    @Schema(description = "Number of active users", example = "45")
    private Long activeUsers;

    @Schema(description = "Total number of tags", example = "30")
    private Long totalTags;

    @Schema(description = "Total number of comments", example = "500")
    private Long totalComments;

    @Schema(description = "Number of pending comments", example = "10")
    private Long pendingComments;

    @Schema(description = "Total views across all posts", example = "50000")
    private Long totalViews;

    @Schema(description = "Views in the last 30 days", example = "5000")
    private Long viewsLast30Days;

    @Schema(description = "Recent activity data")
    private Map<String, Object> recentActivity;

    @Schema(description = "Popular posts data")
    private Map<String, Object> popularPosts;

    @Schema(description = "Traffic analytics")
    private Map<String, Object> trafficAnalytics;
}
