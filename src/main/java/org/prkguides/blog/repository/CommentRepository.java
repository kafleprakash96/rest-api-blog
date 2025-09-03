package org.prkguides.blog.repository;

import org.prkguides.blog.entity.Comment;
import org.prkguides.blog.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by post
    Page<Comment> findByPostIdAndStatusAndParentCommentIsNull(Long postId, CommentStatus status, Pageable pageable);

    // Find all comments by post (including replies)
    Page<Comment> findByPostIdAndStatus(Long postId, CommentStatus status, Pageable pageable);

    // Find comments by author
    Page<Comment> findByAuthorUsername(String username, Pageable pageable);

    // Find comments by status
    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

    // Find replies to a comment
    List<Comment> findByParentCommentIdAndStatus(Long parentCommentId, CommentStatus status, Sort sort);

    // Count by status
    Long countByStatus(CommentStatus status);

    // Find recent comments
    List<Comment> findTop10ByStatusOrderByCreatedAtDesc(CommentStatus status);

    // Find comments by post and user
    List<Comment> findByPostIdAndAuthorUsername(Long postId, String username);

    // Statistics queries
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.status = :status AND c.createdAt >= :since")
    Long countByStatusAndCreatedAtAfter(@Param("status") CommentStatus status, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.status = :status")
    Long countByPostIdAndStatus(@Param("postId") Long postId, @Param("status") CommentStatus status);

    // Find comments that need moderation (pending for more than specified hours)
    @Query("SELECT c FROM Comment c WHERE c.status = 'PENDING' AND c.createdAt < :cutoff ORDER BY c.createdAt ASC")
    List<Comment> findPendingCommentsOlderThan(@Param("cutoff") LocalDateTime cutoff);

}
