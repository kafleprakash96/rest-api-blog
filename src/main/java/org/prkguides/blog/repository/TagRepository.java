package org.prkguides.blog.repository;

import org.prkguides.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
    Optional<Tag> findBySlug(String slug);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    Set<Tag> findByNameIn(@Param("names") Set<String> names);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> findByNameContainingIgnoreCase(@Param("query") String query);

    // Get popular tags (with post count)
    @Query("SELECT t, COUNT(p) as postCount FROM Tag t LEFT JOIN t.posts p " +
            "WHERE p.status = 'PUBLISHED' GROUP BY t ORDER BY postCount DESC")
    List<Object[]> findPopularTags();
}
