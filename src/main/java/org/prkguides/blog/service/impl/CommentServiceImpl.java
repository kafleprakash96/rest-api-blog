package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.entity.Comment;
import org.prkguides.blog.entity.Post;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.CommentStatus;
import org.prkguides.blog.enums.Role;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.CommentRepository;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDto createComment(CommentCreateDto commentCreateDto, String username) {
        log.info("Creating comment on post ID: {} by user: {}", commentCreateDto.getPostId(), username);

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Post post = postRepository.findById(commentCreateDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", commentCreateDto.getPostId().toString()));

        // Check if comments are allowed on this post
        if (!post.getAllowComments()) {
            throw new RuntimeException("Comments are not allowed on this post");
        }

        Comment comment = new Comment();
        comment.setContent(commentCreateDto.getContent());
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setStatus(CommentStatus.PENDING); // Comments require approval

        // Handle parent comment for replies
        if (commentCreateDto.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentCreateDto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentCreateDto.getParentCommentId().toString()));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with ID: {}", savedComment.getId());

        return mapEntityToDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long id, CommentUpdateDto commentUpdateDto, String username) {
        log.info("Updating comment ID: {} by user: {}", id, username);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id.toString()));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check permissions: only comment author or admin can update
        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !Role.ADMIN.equals(currentUser.getRole())) {
            throw new RuntimeException("You don't have permission to update this comment");
        }

        comment.setContent(commentUpdateDto.getContent());
        comment.setStatus(CommentStatus.PENDING); // Reset to pending after edit

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment updated successfully with ID: {}", savedComment.getId());

        return mapEntityToDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id.toString()));
        return mapEntityToDto(comment);
    }

    @Override
    public void deleteComment(Long id, String username) {
        log.info("Deleting comment ID: {} by user: {}", id, username);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id.toString()));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check permissions: only comment author or admin can delete
        if (!comment.getAuthor().getId().equals(currentUser.getId()) &&
                !Role.ADMIN.equals(currentUser.getRole())) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully with ID: {}", id);
    }

    @Override
    public CommentDto replyToComment(Long parentId, CommentReplyDto replyDto, String username) {
        log.info("Creating reply to comment ID: {} by user: {}", parentId, username);

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentId.toString()));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if comments are allowed on the post
        if (!parentComment.getPost().getAllowComments()) {
            throw new RuntimeException("Comments are not allowed on this post");
        }

        Comment reply = new Comment();
        reply.setContent(replyDto.getContent());
        reply.setPost(parentComment.getPost());
        reply.setAuthor(author);
        reply.setParentComment(parentComment);
        reply.setStatus(CommentStatus.PENDING);

        Comment savedReply = commentRepository.save(reply);
        log.info("Reply created successfully with ID: {}", savedReply.getId());

        return mapEntityToDto(savedReply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentReplies(Long commentId) {
        List<Comment> replies = commentRepository.findByParentCommentIdAndStatus(commentId, CommentStatus.APPROVED,
                Sort.by("createdAt").ascending());

        return replies.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<CommentDto> getCommentsByPost(Long postId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByPostIdAndStatusAndParentCommentIsNull(
                postId, CommentStatus.APPROVED, pageable);

        return mapToPaginationResponse(commentsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<CommentDto> getCommentsByUser(String username, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByAuthorUsername(username, pageable);

        return mapToPaginationResponse(commentsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<CommentDto> getPendingComments(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").ascending());
        Page<Comment> commentsPage = commentRepository.findByStatus(CommentStatus.PENDING, pageable);

        return mapToPaginationResponse(commentsPage);
    }

    @Override
    public CommentDto approveComment(Long id) {
        log.info("Approving comment ID: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id.toString()));

        comment.setStatus(CommentStatus.APPROVED);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment approved successfully with ID: {}", id);
        return mapEntityToDto(savedComment);
    }

    @Override
    public CommentDto rejectComment(Long id) {
        log.info("Rejecting comment ID: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id.toString()));

        comment.setStatus(CommentStatus.REJECTED);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment rejected successfully with ID: {}", id);
        return mapEntityToDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalCommentCount() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPendingCommentCount() {
        return commentRepository.countByStatus(CommentStatus.PENDING);
    }

    private CommentDto mapEntityToDto(Comment comment) {
        CommentDto dto = modelMapper.map(comment, CommentDto.class);
        dto.setAuthor(modelMapper.map(comment.getAuthor(), UserSummaryDto.class));
        dto.setPostId(comment.getPost().getId());

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        // Map replies (only approved ones)
        List<CommentDto> replies = comment.getReplies().stream()
                .filter(reply -> CommentStatus.APPROVED.equals(reply.getStatus()))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
        dto.setReplies(replies);

        return dto;
    }

    private PaginationResponse<CommentDto> mapToPaginationResponse(Page<Comment> commentsPage) {
        List<CommentDto> content = commentsPage.getContent().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());

        PaginationResponse<CommentDto> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPage(commentsPage.getNumber());
        response.setSize(commentsPage.getSize());
        response.setTotalPages(commentsPage.getTotalPages());
        response.setTotalElements(commentsPage.getTotalElements());
        response.setLast(commentsPage.isLast());
        response.setFirst(commentsPage.isFirst());
        response.setEmpty(commentsPage.isEmpty());

        return response;
    }
}
