package org.prkguides.blog.service;

import org.prkguides.blog.dto.*;

import java.util.List;

public interface CommentService {

    // CRUD operations
    CommentDto createComment(CommentCreateDto commentCreateDto, String username);
    CommentDto updateComment(Long id, CommentUpdateDto commentUpdateDto, String username);
    CommentDto getCommentById(Long id);
    void deleteComment(Long id, String username);

    // Reply functionality
    CommentDto replyToComment(Long parentId, CommentReplyDto replyDto, String username);
    List<CommentDto> getCommentReplies(Long commentId);

    // Listing and pagination
    PaginationResponse<CommentDto> getCommentsByPost(Long postId, int pageNo, int pageSize);
    PaginationResponse<CommentDto> getCommentsByUser(String username, int pageNo, int pageSize);
    PaginationResponse<CommentDto> getPendingComments(int pageNo, int pageSize);

    // Moderation
    CommentDto approveComment(Long id);
    CommentDto rejectComment(Long id);

    // Statistics
    Long getTotalCommentCount();
    Long getPendingCommentCount();
}
