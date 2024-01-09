package org.prkguides.blog.service;

import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.miscellaneous.PaginationResponse;
import org.prkguides.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public PaginationResponse getAllPosts(int pageNo, int pageSize) {

        //Create pageable instance
        Pageable pageable = PageRequest.of(pageNo,pageSize);

        Page<Post> page = postRepository.findAll(pageable);

//        List<Post> posts = postRepository.findAll();
        //get content from page
        List<Post> posts = page.getContent();

        List<PostDto> content = posts.stream().map((post) -> modelMapper.map(post,PostDto.class))
                .toList();

        PaginationResponse response = new PaginationResponse();

        response.setContent(content);
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
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

        postRepository.save(post);

        return modelMapper.map(post,PostDto.class);
    }

    @Override
    public void deletePostById(Long id) {
       Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id.toString()));

       postRepository.delete(post);
    }
}
