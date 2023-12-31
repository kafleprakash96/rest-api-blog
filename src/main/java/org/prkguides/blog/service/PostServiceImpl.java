package org.prkguides.blog.service;

import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<PostDto> getAllPosts() {

        List<Post> posts = postRepository.findAll();

        return posts.stream().map((post) -> modelMapper.map(post,PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDto findPostById(Long id) {
        //find post by id
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto updatePostById(Long id, PostDto postDto) {

        //find post by id.
        Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id.toString()));

        //update the existing post with provided postDto
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return modelMapper.map(post,PostDto.class);
    }

    @Override
    public void deletePostById(Long id) {
       Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id.toString()));

       postRepository.delete(post);
    }
}
