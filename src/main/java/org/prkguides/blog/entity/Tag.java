package org.prkguides.blog.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"posts"})
@ToString(exclude = {"posts"})
@Entity
@Table(name = "tags", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Tag extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "color", length = 7) // Hex color code
    private String color;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
