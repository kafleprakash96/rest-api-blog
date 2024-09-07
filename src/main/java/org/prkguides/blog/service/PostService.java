package org.prkguides.blog.service;

import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.miscellaneous.PaginationResponse;

public interface PostService {

    PostDto createPost(PostDto postDto);

    PaginationResponse getAllPosts(int pageNo, int pageSize);

    PostDto findPostById(Long id);

    PostDto updatePostById(Long id, PostDto postDto);

    void deletePostById(Long id);
}
