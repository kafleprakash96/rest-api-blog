package org.prkguides.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.prkguides.blog.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "posts",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})},
        indexes = {
                @Index(name = "idx_post_status", columnList = "status"),
                @Index(name = "idx_post_published_date", columnList = "publishedDate"),
                @Index(name = "idx_post_author", columnList = "author_id")
        })
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", unique = true, nullable = false, length = 250)
    private String slug;

    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    @Column(name = "reading_time")
    private Integer readingTimeMinutes;

    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "is_featured", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isFeatured = false;

    @Column(name = "allow_comments", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean allowComments = true;

    // Helper methods
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }

    public boolean isPublished() {
        return PostStatus.PUBLISHED.equals(this.status);
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
