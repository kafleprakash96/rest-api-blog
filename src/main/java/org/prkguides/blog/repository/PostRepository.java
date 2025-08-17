package org.prkguides.blog.repository;

import org.prkguides.blog.entity.*;
import org.prkguides.blog.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {

    Optional<Post> findBySlug(String slug);

    // Find published posts
    Page<Post> findByStatusOrderByPublishedDateDesc(PostStatus status, Pageable pageable);

    // Find featured posts
    Page<Post> findByIsFeaturedTrueAndStatusOrderByPublishedDateDesc(PostStatus status, Pageable pageable);

//    // Find posts by author
    Page<Post> findByAuthorUsernameAndStatusOrderByPublishedDateDesc(String username, PostStatus status, Pageable pageable);

//    // Find posts by tag
//    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.status = :status ORDER BY p.publishedDate DESC")
//    Page<Post> findByTagNameAndStatus(@Param("tagName") String tagName, @Param("status") PostStatus status, Pageable pageable);

//
//    // Full-text search
//    @Query("SELECT p FROM Post p WHERE p.status = :status AND " +
//            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
//            "ORDER BY p.publishedDate DESC")
//    Page<Post> searchByQuery(@Param("query") String query, @Param("status") PostStatus status, Pageable pageable);
//
//    // Advanced search with multiple filters
//    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
//            "(:query IS NULL OR (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
//            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
//            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%')))) " +
//            "AND (:status IS NULL OR p.status = :status) " +
//            "AND (:author IS NULL OR p.author.username = :author) " +
//            "AND (:featured IS NULL OR p.isFeatured = :featured) " +
//            "AND (:tagName IS NULL OR t.name = :tagName)")
//    Page<Post> findByFilters(
//            @Param("query") String query,
//            @Param("status") PostStatus status,
//            @Param("author") String author,
//            @Param("featured") Boolean featured,
//            @Param("tagName") String tagName,
//            Pageable pageable);


    // Get related posts (by tags)
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t IN " +
            "(SELECT t2 FROM Post p2 JOIN p2.tags t2 WHERE p2.id = :postId) " +
            "AND p.id != :postId AND p.status = :status ORDER BY p.publishedDate DESC")
    List<Post> findRelatedPosts(@Param("postId") Long postId, @Param("status") PostStatus status, Pageable pageable);

    // Increment view count
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Get posts to publish (scheduled posts)
    @Query("SELECT p FROM Post p WHERE p.status = 'SCHEDULED' AND p.publishedDate <= :now")
    List<Post> findScheduledPostsToPublish(@Param("now") LocalDateTime now);

    // Statistics queries
    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = :status")
    Long countByStatus(@Param("status") PostStatus status);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId AND p.status = :status")
    Long countByAuthorAndStatus(@Param("authorId") Long authorId, @Param("status") PostStatus status);

    // Popular posts (by view count)
    Page<Post> findByStatusOrderByViewCountDesc(PostStatus status, Pageable pageable);

    // Recent posts
    Page<Post> findTop10ByStatusOrderByPublishedDateDesc(PostStatus status, Pageable pageable);
}
