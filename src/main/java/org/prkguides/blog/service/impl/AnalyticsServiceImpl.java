package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.enums.CommentStatus;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.repository.CommentRepository;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.TagRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.AnalyticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getPostAnalytics(Long postId, int days) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Map<String, Object> analytics = new HashMap<>();

        analytics.put("postId", postId);
        analytics.put("title", post.getTitle());
        analytics.put("viewCount", post.getViewCount());
        analytics.put("commentCount", commentRepository.countByPostIdAndStatus(postId, CommentStatus.APPROVED));
        analytics.put("publishedDate", post.getPublishedDate());
        analytics.put("author", post.getAuthor().getUsername());
        analytics.put("tags", post.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList()));

        // Additional metrics would be implemented based on your tracking system
        analytics.put("dailyViews", getDailyViewsForPost(postId, days));
        analytics.put("engagementRate", calculateEngagementRate(postId));

        return analytics;
    }

    @Override
    public Map<String, Object> getTrafficAnalytics(int days) {
        Map<String, Object> analytics = new HashMap<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        // Basic traffic metrics
        analytics.put("period", days + " days");
        analytics.put("totalPosts", postRepository.countByStatus(PostStatus.PUBLISHED));
        analytics.put("totalViews", getTotalViews());
        analytics.put("averageViewsPerPost", getAverageViewsPerPost());

        // Top performing posts
        List<Post> topPosts = postRepository.findByStatusOrderByViewCountDesc(
                PostStatus.PUBLISHED, PageRequest.of(0, 10)).getContent();
        analytics.put("topPosts", topPosts.stream()
                .map(post -> Map.of(
                        "id", post.getId(),
                        "title", post.getTitle(),
                        "viewCount", post.getViewCount(),
                        "slug", post.getSlug()
                ))
                .collect(Collectors.toList()));

        return analytics;
    }

    @Override
    public Map<String, Object> getEngagementAnalytics(int days) {
        Map<String, Object> analytics = new HashMap<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        analytics.put("totalComments", commentRepository.countByStatus(CommentStatus.APPROVED));
        analytics.put("pendingComments", commentRepository.countByStatus(CommentStatus.PENDING));
        analytics.put("recentComments", commentRepository.countByStatusAndCreatedAtAfter(CommentStatus.APPROVED, startDate));

        // Comment engagement by post
        List<Object[]> commentsByPost = getCommentCountByPost();
        analytics.put("commentsByPost", commentsByPost);

        return analytics;
    }

    @Override
    public Map<String, Object> getContentPerformance(int days) {
        Map<String, Object> analytics = new HashMap<>();

        // Content creation metrics
        analytics.put("totalPublishedPosts", postRepository.countByStatus(PostStatus.PUBLISHED));
        analytics.put("totalDraftPosts", postRepository.countByStatus(PostStatus.DRAFT));
        analytics.put("totalScheduledPosts", postRepository.countByStatus(PostStatus.SCHEDULED));

        // Author performance
        List<Object[]> authorStats = getAuthorPerformanceStats();
        analytics.put("authorPerformance", authorStats);

        // Recent content
        List<Post> recentPosts = postRepository.findTop10ByStatusOrderByPublishedDateDesc(
                PostStatus.PUBLISHED, PageRequest.of(0, 10)).getContent();
        analytics.put("recentPosts", recentPosts.stream()
                .map(post -> Map.of(
                        "id", post.getId(),
                        "title", post.getTitle(),
                        "publishedDate", post.getPublishedDate(),
                        "viewCount", post.getViewCount()
                ))
                .collect(Collectors.toList()));

        return analytics;
    }

    @Override
    public Map<String, Object> getTagAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalTags", tagRepository.count());

        // Popular tags
        List<Object[]> popularTags = tagRepository.findPopularTags();
        analytics.put("popularTags", popularTags.stream()
                .limit(20)
                .map(result -> Map.of(
                        "name", ((org.prkguides.blog.entity.Tag) result[0]).getName(),
                        "postCount", result[1],
                        "color", ((org.prkguides.blog.entity.Tag) result[0]).getColor()
                ))
                .collect(Collectors.toList()));

        return analytics;
    }

    @Override
    public Map<String, Object> getPopularContent(String period, int limit) {
        Map<String, Object> popular = new HashMap<>();

        // Most viewed posts
        List<Post> popularPosts = postRepository.findByStatusOrderByViewCountDesc(
                PostStatus.PUBLISHED, PageRequest.of(0, limit)).getContent();

        popular.put("posts", popularPosts.stream()
                .map(post -> Map.of(
                        "id", post.getId(),
                        "title", post.getTitle(),
                        "slug", post.getSlug(),
                        "viewCount", post.getViewCount(),
                        "author", post.getAuthor().getUsername()
                ))
                .collect(Collectors.toList()));

        return popular;
    }

    @Override
    @Transactional
    public void trackPageView(String page, String referrer, String userAgent) {
        // This is a placeholder for actual analytics tracking
        // In a real application, you might store this in a separate analytics table
        // or send to an external analytics service
        log.info("Page view tracked: {} from referrer: {}", page, referrer);
    }

    private List<Integer> getDailyViewsForPost(Long postId, int days) {
        // This would typically come from a detailed analytics table
        // For now, return dummy data - implement based on your tracking needs
        return List.of(10, 15, 8, 22, 18, 12, 25); // Sample data
    }

    private double calculateEngagementRate(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || post.getViewCount() == 0) {
            return 0.0;
        }

        Long commentCount = commentRepository.countByPostIdAndStatus(postId, CommentStatus.APPROVED);
        return (commentCount.doubleValue() / post.getViewCount()) * 100;
    }

    private Long getTotalViews() {
        List<Post> allPosts = postRepository.findAll();
        return allPosts.stream()
                .mapToLong(post -> post.getViewCount() != null ? post.getViewCount() : 0L)
                .sum();
    }

    private Double getAverageViewsPerPost() {
        List<Post> publishedPosts = postRepository.findAll().stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .collect(Collectors.toList());

        if (publishedPosts.isEmpty()) {
            return 0.0;
        }

        long totalViews = publishedPosts.stream()
                .mapToLong(post -> post.getViewCount() != null ? post.getViewCount() : 0L)
                .sum();

        return (double) totalViews / publishedPosts.size();
    }

    private List<Object[]> getCommentCountByPost() {
        // This would typically be a custom query
        // For now, return basic data structure
        return List.of();
    }

    private List<Object[]> getAuthorPerformanceStats() {
        // This would be a complex query showing author statistics
        return userRepository.findAll().stream()
                .map(user -> new Object[]{
                        user.getUsername(),
                        postRepository.countByAuthorAndStatus(user.getId(), PostStatus.PUBLISHED),
                        user.getPosts().stream().mapToLong(post -> post.getViewCount() != null ? post.getViewCount() : 0L).sum()
                })
                .collect(Collectors.toList());
    }
}
