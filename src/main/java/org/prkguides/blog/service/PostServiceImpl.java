package org.prkguides.blog.service;

import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private ModelMapper modelMapper;

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository){
        this.postRepository = postRepository;
    }
    @Override
    public PostDto createPost(PostDto postDto) {

        //Convert Dto to entity
        Post post = modelMapper.map(postDto,Post.class);

        Post newPost = postRepository.save(post);

        //Convert entity to DTO
        PostDto postResponse = modelMapper.map(newPost,PostDto.class);

        return postResponse;
    }
}
