package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.entity.Tag;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.TagRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.PostService;
import org.prkguides.blog.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import java.util.stream.Collectors;

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

    @Override
    public PostDto updatePost(Long id, PostCreateDto postUpdateDto) {
        return null;
    }

    @Override
    public PostDto getPostById(Long id) {
        return null;
    }

    @Override
    public PostDto getPostBySlug(String slug) {
        return null;
    }

    @Override
    public void deletePost(Long id) {

    }

    @Override
    public PaginationResponse<PostSummaryDto> getAllPosts(int pageNo, int pageSize) {
        return null;
    }

    @Override
    @Cacheable(value = "posts", key = "'published-' + #pageNo + '-' + #pageSize")
    public PaginationResponse<PostSummaryDto> getPublishedPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> postsPage = postRepository.findByStatusOrderByPublishedDateDesc(PostStatus.PUBLISHED, pageable);
        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    public PaginationResponse<PostSummaryDto> getFeaturedPosts(int pageNo, int pageSize) {
        return null;
    }

    @Override
    public PaginationResponse<PostSummaryDto> getPostsByAuthor(String username, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public PaginationResponse<PostSummaryDto> getPostsByTag(String tagName, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public PaginationResponse<PostSummaryDto> searchPosts(SearchRequestDto searchRequest) {
        return null;
    }

    @Override
    public List<PostSummaryDto> getRelatedPosts(Long postId, int limit) {
        return null;
    }

    @Override
    public List<PostSummaryDto> getPopularPosts(int limit) {
        return null;
    }

    @Override
    public List<PostSummaryDto> getRecentPosts(int limit) {
        return null;
    }

    @Override
    public void incrementViewCount(Long id) {

    }

    @Override
    public PostDto publishPost(Long id) {
        return null;
    }

    @Override
    public PostDto unpublishPost(Long id) {
        return null;
    }

    @Override
    public PostDto schedulePost(Long id, String publishDate) {
        return null;
    }

    @Override
    public PostDto toggleFeatured(Long id) {
        return null;
    }

    @Override
    public Long getTotalPostCount() {
        return null;
    }

    @Override
    public Long getPublishedPostCount() {
        return null;
    }

    @Override
    public Long getDraftPostCount() {
        return null;
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

    private PostSummaryDto mapEntityToSummaryDto(Post entity) {
        PostSummaryDto dto = modelMapper.map(entity, PostSummaryDto.class);
        dto.setAuthor(modelMapper.map(entity.getAuthor(), UserSummaryDto.class));
        return dto;
    }

    private PaginationResponse<PostSummaryDto> mapToSummaryPaginationResponse(Page<Post> postsPage) {
        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(this::mapEntityToSummaryDto)
                .collect(Collectors.toList());

        PaginationResponse<PostSummaryDto> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPageNo(postsPage.getNumber());
        response.setPageSize(postsPage.getSize());
        response.setTotalPages(postsPage.getTotalPages());
        response.setTotalElements(postsPage.getTotalElements());
        response.setLast(postsPage.isLast());
        response.setFirst(postsPage.isFirst());
        response.setEmpty(postsPage.isEmpty());

        return response;
    }


}
