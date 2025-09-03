package org.prkguides.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.prkguides.blog.enums.CommentStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"post", "author", "parentComment", "replies"})
@ToString(exclude = {"post", "author", "parentComment", "replies"})
@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_author", columnList = "author_id"),
                @Index(name = "idx_comment_status", columnList = "status"),
                @Index(name = "idx_comment_parent", columnList = "parent_comment_id")
        })
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommentStatus status = CommentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies = new ArrayList<>();

    // Helper methods
    public boolean isReply() {
        return parentComment != null;
    }

    public boolean isApproved() {
        return CommentStatus.APPROVED.equals(this.status);
    }

    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }

    public void removeReply(Comment reply) {
        replies.remove(reply);
        reply.setParentComment(null);
    }
}

