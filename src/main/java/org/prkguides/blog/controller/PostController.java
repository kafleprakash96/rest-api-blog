package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.miscellaneous.PaginationResponse;
import org.prkguides.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = {"http://localhost:4200", "https://yourdomain.com"})
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Blog post management endpoints")
public class PostController {

    private final PostService postService;


    @Operation(summary = "Create a new blog post", description = "Creates a new blog post. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<APIResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            Authentication authentication) {

        log.info("Creating new post: {}", postCreateDto.getTitle());
        PostDto createdPost = postService.createPost(postCreateDto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Post created successfully", createdPost));
    }

    @GetMapping
    public ResponseEntity<PaginationResponse> getAllPosts(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(value = "pageSize",defaultValue = "10", required = false) int pageSize){

        PaginationResponse paginationResponse = postService.getAllPosts(pageNo,pageSize);
//        List<PostDto> postDtos = postService.getAllPosts(pageNo,pageSize);

        if(!paginationResponse.getContent().isEmpty()){
            return new ResponseEntity<>(paginationResponse,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(paginationResponse,HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> findPostById(@PathVariable Long id){
        return new ResponseEntity<>(postService.findPostById(id),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePostById(@PathVariable Long id, @RequestBody PostDto postDto){
        return new ResponseEntity<>(postService.updatePostById(id,postDto),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable Long id){
        postService.deletePostById(id);
        return ResponseEntity.ok("Post with id " + id + " deleted successfully");
    }


}
