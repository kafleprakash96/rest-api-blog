package org.prkguides.blog.service;

import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository){
        this.postRepository = postRepository;
    }
    @Override
    public PostDto createPost(PostDto postDto) {

        //Convert Dto to entity
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());

        Post newPost = postRepository.save(post);

        //Convert entity to DTO
        PostDto postResponse = new PostDto();
        postResponse.setId(newPost.getId());
        postResponse.setContent(newPost.getContent());
        postResponse.setDescription(postDto.getDescription());

        return postResponse;
    }
}
