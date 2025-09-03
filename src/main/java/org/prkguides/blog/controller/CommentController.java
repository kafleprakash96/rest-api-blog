package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = {"http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create comment", description = "Create a new comment on a post")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<APIResponse<CommentDto>> createComment(
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            Authentication authentication) {

        log.info("Creating comment on post ID: {} by user: {}",
                commentCreateDto.getPostId(), authentication.getName());

        CommentDto createdComment = commentService.createComment(commentCreateDto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Comment created successfully", createdComment));
    }

    @Operation(summary = "Get comments for post", description = "Get all comments for a specific post")
    @GetMapping("/post/{postId}")
    public ResponseEntity<APIResponse<PaginationResponse<CommentDto>>> getCommentsByPost(
            @Parameter(description = "Post ID") @PathVariable Long postId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

        PaginationResponse<CommentDto> comments = commentService.getCommentsByPost(postId, pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Comments retrieved successfully", comments));
    }

    @Operation(summary = "Get comment by ID", description = "Get a specific comment by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CommentDto>> getCommentById(
            @Parameter(description = "Comment ID") @PathVariable Long id) {

        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(APIResponse.success("Comment retrieved successfully", comment));
    }

    @Operation(summary = "Update comment", description = "Update a comment (only by comment author or admin)")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CommentDto>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto,
            Authentication authentication) {

        log.info("Updating comment ID: {} by user: {}", id, authentication.getName());
        CommentDto updatedComment = commentService.updateComment(id, commentUpdateDto, authentication.getName());

        return ResponseEntity.ok(APIResponse.success("Comment updated successfully", updatedComment));
    }

    @Operation(summary = "Delete comment", description = "Delete a comment (only by comment author or admin)")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Deleting comment ID: {} by user: {}", id, authentication.getName());
        commentService.deleteComment(id, authentication.getName());

        return ResponseEntity.ok(APIResponse.success("Comment deleted successfully",
                "Comment with ID " + id + " has been deleted"));
    }

    @Operation(summary = "Reply to comment", description = "Reply to an existing comment")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/reply")
    public ResponseEntity<APIResponse<CommentDto>> replyToComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentReplyDto replyDto,
            Authentication authentication) {

        log.info("Creating reply to comment ID: {} by user: {}", id, authentication.getName());
        CommentDto reply = commentService.replyToComment(id, replyDto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Reply created successfully", reply));
    }

    @Operation(summary = "Get comment replies", description = "Get all replies to a specific comment")
    @GetMapping("/{id}/replies")
    public ResponseEntity<APIResponse<List<CommentDto>>> getCommentReplies(
            @Parameter(description = "Comment ID") @PathVariable Long id) {

        List<CommentDto> replies = commentService.getCommentReplies(id);
        return ResponseEntity.ok(APIResponse.success("Replies retrieved successfully", replies));
    }

    @Operation(summary = "Approve comment", description = "Approve a pending comment (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<APIResponse<CommentDto>> approveComment(@PathVariable Long id) {

        log.info("Approving comment ID: {}", id);
        CommentDto approvedComment = commentService.approveComment(id);

        return ResponseEntity.ok(APIResponse.success("Comment approved successfully", approvedComment));
    }

    @Operation(summary = "Reject comment", description = "Reject a pending comment (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<APIResponse<CommentDto>> rejectComment(@PathVariable Long id) {

        log.info("Rejecting comment ID: {}", id);
        CommentDto rejectedComment = commentService.rejectComment(id);

        return ResponseEntity.ok(APIResponse.success("Comment rejected successfully", rejectedComment));
    }

    @Operation(summary = "Get pending comments", description = "Get all pending comments for moderation (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<APIResponse<PaginationResponse<CommentDto>>> getPendingComments(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

        PaginationResponse<CommentDto> comments = commentService.getPendingComments(pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Pending comments retrieved successfully", comments));
    }

    @Operation(summary = "Get user comments", description = "Get all comments by authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-comments")
    public ResponseEntity<APIResponse<PaginationResponse<CommentDto>>> getUserComments(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            Authentication authentication) {

        PaginationResponse<CommentDto> comments = commentService.getCommentsByUser(authentication.getName(), pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("User comments retrieved successfully", comments));
    }
}
