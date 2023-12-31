package org.prkguides.blog.dto;

import lombok.*;

@Data
public class PostDto {

    private Long id;
    private String title;
    private String description;
    private String content;
}
