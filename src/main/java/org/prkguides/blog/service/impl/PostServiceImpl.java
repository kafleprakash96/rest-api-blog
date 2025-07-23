package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.PostCreateDto;
import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.dto.UserSummaryDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.entity.Tag;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.miscellaneous.PaginationResponse;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.TagRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.PostService;
import org.prkguides.blog.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TagRepository tagRepository;

    @Override
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public PostDto createPost(PostCreateDto postCreateDto, String authorUsername) {
        log.info("Creating new post with title: {}", postCreateDto.getTitle());

        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authorUsername));

        Post post = new Post();
        mapCreateDtoToEntity(postCreateDto, post);
        post.setAuthor(author);
        post.setSlug(SlugUtils.generateSlug(postCreateDto.getTitle()));

        // Handle tags
        if (postCreateDto.getTagNames() != null && !postCreateDto.getTagNames().isEmpty()) {
            Set<Tag> tags = handleTags(postCreateDto.getTagNames());
            post.setTags(tags);
        }

        // Set published date for published posts
        if (PostStatus.PUBLISHED.equals(postCreateDto.getStatus()) && postCreateDto.getPublishedDate() == null) {
            post.setPublishedDate(LocalDateTime.now());
        }

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with ID: {}", savedPost.getId());

        return mapEntityToDto(savedPost);
    }

    private void mapCreateDtoToEntity(PostCreateDto dto, Post entity) {
        entity.setTitle(dto.getTitle());
        entity.setExcerpt(dto.getExcerpt());
        entity.setContent(dto.getContent());
        entity.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        entity.setReadingTimeMinutes(dto.getReadingTimeMinutes());
        entity.setStatus(dto.getStatus());
        entity.setPublishedDate(dto.getPublishedDate());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setMetaKeywords(dto.getMetaKeywords());
        entity.setIsFeatured(dto.getIsFeatured());
        entity.setAllowComments(dto.getAllowComments());
    }

    private PostDto mapEntityToDto(Post entity) {
        PostDto dto = modelMapper.map(entity, PostDto.class);
        dto.setAuthor(modelMapper.map(entity.getAuthor(), UserSummaryDto.class));
        return dto;
    }

    private Set<Tag> handleTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();

        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                Tag tag = tagRepository.findByName(tagName.trim())
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName.trim());
                            newTag.setSlug(SlugUtils.generateSlug(tagName.trim()));
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }

        return tags;
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
//        post.setDescription(postDto.getDescription());
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
