package org.prkguides.blog.service;

import org.prkguides.blog.dto.PostDto;
import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto);

    List<PostDto> getAllPosts();

    PostDto findPostById(Long id);

    PostDto updatePostById(Long id, PostDto postDto);

    void deletePostById(Long id);
}
