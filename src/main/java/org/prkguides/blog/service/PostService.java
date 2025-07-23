package org.prkguides.blog.service;

import org.prkguides.blog.dto.PostCreateDto;
import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.miscellaneous.PaginationResponse;

public interface PostService {

    PostDto createPost(PostCreateDto postCreateDto, String authorUsername);

    PaginationResponse getAllPosts(int pageNo, int pageSize);

    PostDto findPostById(Long id);

    PostDto updatePostById(Long id, PostDto postDto);

    void deletePostById(Long id);
}
