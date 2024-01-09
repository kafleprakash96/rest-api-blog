package org.prkguides.blog.controller;

import org.prkguides.blog.dto.PostDto;
import org.prkguides.blog.miscellaneous.PaginationResponse;
import org.prkguides.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/posts")
public class PostController {

    PostService postService;

    PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost (@RequestBody PostDto postDto){
        return  new ResponseEntity<>(postService.createPost(postDto) , HttpStatus.CREATED);
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
