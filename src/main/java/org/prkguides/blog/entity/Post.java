package org.prkguides.blog.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="posts", uniqueConstraints = {@UniqueConstraint(columnNames = ("title"))})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name="content")
    private String content;

    @Column(name="description")
    private String description;
}
