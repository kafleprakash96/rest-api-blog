package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.PaginationResponse;
import org.prkguides.blog.dto.PostSummaryDto;
import org.prkguides.blog.dto.TagCreateDto;
import org.prkguides.blog.dto.TagDto;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.entity.Tag;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.TagRepository;
import org.prkguides.blog.service.TagService;
import org.prkguides.blog.utils.SlugUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = {"tags", "popular-tags"}, allEntries = true)
    public TagDto createTag(TagCreateDto tagCreateDto) {
        log.info("Creating new tag: {}", tagCreateDto.getName());

        // Check if tag name already exists
        if (tagRepository.findByName(tagCreateDto.getName()).isPresent()) {
            throw new RuntimeException("Tag with name '" + tagCreateDto.getName() + "' already exists");
        }

        Tag tag = new Tag();
        tag.setName(tagCreateDto.getName());
        tag.setSlug(SlugUtils.generateUniqueSlug(tagCreateDto.getName(),
                slug -> tagRepository.findBySlug(slug).isPresent()));
        tag.setDescription(tagCreateDto.getDescription());
        tag.setColor(tagCreateDto.getColor());

        Tag savedTag = tagRepository.save(tag);
        log.info("Tag created successfully with ID: {}", savedTag.getId());

        return mapEntityToDto(savedTag);
    }

    @Override
    @CacheEvict(value = {"tags", "popular-tags"}, allEntries = true)
    public TagDto updateTag(Long id, TagCreateDto tagUpdateDto) {
        log.info("Updating tag with ID: {}", id);

        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id.toString()));

        // Check if new name conflicts with existing tags (excluding current tag)
        tagRepository.findByName(tagUpdateDto.getName())
                .ifPresent(tag -> {
                    if (!tag.getId().equals(id)) {
                        throw new RuntimeException("Tag with name '" + tagUpdateDto.getName() + "' already exists");
                    }
                });

        existingTag.setName(tagUpdateDto.getName());
        existingTag.setDescription(tagUpdateDto.getDescription());
        existingTag.setColor(tagUpdateDto.getColor());

        // Update slug if name changed
        String newSlug = SlugUtils.generateSlug(tagUpdateDto.getName());
        if (!newSlug.equals(existingTag.getSlug())) {
            existingTag.setSlug(SlugUtils.generateUniqueSlug(tagUpdateDto.getName(),
                    slug -> tagRepository.findBySlug(slug).map(t -> !t.getId().equals(id)).orElse(false)));
        }

        Tag savedTag = tagRepository.save(existingTag);
        log.info("Tag updated successfully with ID: {}", savedTag.getId());

        return mapEntityToDto(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#id")
    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id.toString()));
        return mapEntityToDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#slug")
    public TagDto getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "slug", slug));
        return mapEntityToDto(tag);
    }

    @Override
    @CacheEvict(value = {"tags", "popular-tags"}, allEntries = true)
    public void deleteTag(Long id) {
        log.info("Deleting tag with ID: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id.toString()));

        tagRepository.delete(tag);
        log.info("Tag deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "'all-' + #pageNo + '-' + #pageSize")
    public PaginationResponse<TagDto> getAllTags(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("name").ascending());
        Page<Tag> tagsPage = tagRepository.findAll(pageable);

        List<TagDto> content = tagsPage.getContent().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());

        PaginationResponse<TagDto> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPage(tagsPage.getNumber());
        response.setSize(tagsPage.getSize());
        response.setTotalPages(tagsPage.getTotalPages());
        response.setTotalElements(tagsPage.getTotalElements());
        response.setLast(tagsPage.isLast());
        response.setFirst(tagsPage.isFirst());
        response.setEmpty(tagsPage.isEmpty());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "popular-tags", key = "#limit")
    public List<TagDto> getPopularTags(int limit) {
        List<Object[]> popularTagsData = tagRepository.findPopularTags();

        return popularTagsData.stream()
                .limit(limit)
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long postCount = (Long) result[1];
                    TagDto dto = mapEntityToDto(tag);
                    dto.setPostCount(postCount.intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> searchTags(String query) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(query);
        return tags.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<PostSummaryDto> getPostsByTag(Long tagId, int pageNo, int pageSize) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId.toString()));

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("publishedDate").descending());

        // Create specification to find posts by tag
        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.isMember(tag, root.get("tags")),
                    criteriaBuilder.equal(root.get("status"), PostStatus.PUBLISHED)
            );
        };

        Page<Post> postsPage = postRepository.findAll(spec, pageable);

        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(post -> modelMapper.map(post, PostSummaryDto.class))
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

    @Override
    @Transactional(readOnly = true)
    public Long getTotalTagCount() {
        return tagRepository.count();
    }

    //Todo: Replace with mapper class
    private TagDto mapEntityToDto(Tag tag) {
        TagDto dto = modelMapper.map(tag, TagDto.class);
        // Set post count if not already set
        if (dto.getPostCount() == null) {
            dto.setPostCount(tag.getPosts().size());
        }
        return dto;
    }
}
