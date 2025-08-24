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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public PostDto updatePost(Long id, PostCreateDto postUpdateDto) {
        log.info("Updating post with ID: {}", id);

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        // Store previous status to handle publishing workflow
        PostStatus previousStatus = existingPost.getStatus();

        mapCreateDtoToEntity(postUpdateDto, existingPost);

        // Handle slug update if title changed
        if (!existingPost.getTitle().equals(postUpdateDto.getTitle())) {
            existingPost.setSlug(SlugUtils.generateSlug(postUpdateDto.getTitle()));
        }

        // Handle tags
        existingPost.getTags().clear();
        if (postUpdateDto.getTagNames() != null && !postUpdateDto.getTagNames().isEmpty()) {
            Set<Tag> tags = handleTags(postUpdateDto.getTagNames());
            existingPost.setTags(tags);
        }

        // Handle publishing workflow
        if (PostStatus.PUBLISHED.equals(postUpdateDto.getStatus()) &&
                !PostStatus.PUBLISHED.equals(previousStatus) &&
                postUpdateDto.getPublishedDate() == null) {
            existingPost.setPublishedDate(LocalDateTime.now());
        }

        Post updatedPost = postRepository.save(existingPost);
        log.info("Post updated successfully with ID: {}", updatedPost.getId());

        return mapEntityToDto(updatedPost);
    }

    @Override
    @Cacheable(value = "posts", key = "#id")
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));
        return mapEntityToDto(post);
    }

    @Override
    @Cacheable(value = "posts", key = "#slug")
    public PostDto getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));
        return mapEntityToDto(post);
    }

    @Override
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public void deletePost(Long id) {
        log.info("Deleting post with ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        postRepository.delete(post);
        log.info("Post deleted successfully with ID: {}", id);
    }

    @Override
    public PaginationResponse<PostSummaryDto> getAllPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);
        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    @Cacheable(value = "posts", key = "'published-' + #pageNo + '-' + #pageSize")
    public PaginationResponse<PostSummaryDto> getPublishedPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> postsPage = postRepository.findByStatusOrderByPublishedDateDesc(PostStatus.PUBLISHED, pageable);
        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    @Cacheable(value = "featured-posts", key = "#pageNo + '-' + #pageSize")
    public PaginationResponse<PostSummaryDto> getFeaturedPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> postsPage = postRepository.findByIsFeaturedTrueAndStatusOrderByPublishedDateDesc(PostStatus.PUBLISHED, pageable);
        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    public PaginationResponse<PostSummaryDto> getPostsByAuthor(String username, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> postsPage = postRepository.findByAuthorUsernameAndStatusOrderByPublishedDateDesc(username, PostStatus.PUBLISHED, pageable);
        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    public PaginationResponse<PostSummaryDto> getPostsByTag(String tagName, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public PaginationResponse<PostSummaryDto> searchPosts(SearchRequestDto searchRequest) {
        Pageable pageable = createPageable(searchRequest);

        // Use specification for complex queries
        Specification<Post> spec = createSearchSpecification(searchRequest);
        Page<Post> postsPage = postRepository.findAll(spec, pageable);

        return mapToSummaryPaginationResponse(postsPage);
    }

    @Override
    public List<PostSummaryDto> getRelatedPosts(Long postId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> relatedPosts = postRepository.findRelatedPosts(postId, PostStatus.PUBLISHED, pageable);
        return relatedPosts.stream()
                .map(this::mapEntityToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "popular-posts", key = "#limit")
    public List<PostSummaryDto> getPopularPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Post> popularPosts = postRepository.findByStatusOrderByViewCountDesc(PostStatus.PUBLISHED, pageable);
        return popularPosts.getContent().stream()
                .map(this::mapEntityToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "recent-posts", key = "#limit")
    public List<PostSummaryDto> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Post> recentPosts = postRepository.findTop10ByStatusOrderByPublishedDateDesc(PostStatus.PUBLISHED, pageable);
        return recentPosts.getContent().stream()
                .map(this::mapEntityToSummaryDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        postRepository.incrementViewCount(id);
    }

    @Override
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public PostDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedDate(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return mapEntityToDto(savedPost);
    }

    @Override
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public PostDto unpublishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        post.setStatus(PostStatus.DRAFT);

        Post savedPost = postRepository.save(post);
        return mapEntityToDto(savedPost);
    }

    @Override
    @CacheEvict(value = {"posts", "featured-posts", "recent-posts"}, allEntries = true)
    public PostDto schedulePost(Long id, String publishDate) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        post.setStatus(PostStatus.SCHEDULED);
        post.setPublishedDate(LocalDateTime.parse(publishDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Post savedPost = postRepository.save(post);
        return mapEntityToDto(savedPost);
    }

    @Override
    @CacheEvict(value = {"posts", "featured-posts"}, allEntries = true)
    public PostDto toggleFeatured(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        post.setIsFeatured(!post.getIsFeatured());

        Post savedPost = postRepository.save(post);
        return mapEntityToDto(savedPost);
    }

    @Override
    public Long getTotalPostCount() {
        return postRepository.count();
    }

    @Override
    public Long getPublishedPostCount() {
        return postRepository.countByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public Long getDraftPostCount() {
        return postRepository.countByStatus(PostStatus.DRAFT);
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
        response.setPage(postsPage.getNumber());
        response.setSize(postsPage.getSize());
        response.setTotalPages(postsPage.getTotalPages());
        response.setTotalElements(postsPage.getTotalElements());
        response.setLast(postsPage.isLast());
        response.setFirst(postsPage.isFirst());
        response.setEmpty(postsPage.isEmpty());

        return response;
    }

    private Pageable createPageable(SearchRequestDto searchRequest) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(searchRequest.getSortDirection()) ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                searchRequest.getSortBy()
        );
        return PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
    }

    private Specification<Post> createSearchSpecification(SearchRequestDto searchRequest) {
        return (root, query, criteriaBuilder) -> {
            // Implementation of complex search specification
            // This would include all the filtering logic based on the search request
            return criteriaBuilder.conjunction(); // Simplified for brevity
        };
    }


}
