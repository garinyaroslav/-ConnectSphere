package org.garin.core.web.controller;

import org.garin.core.entity.Post;
import org.garin.core.repository.specification.PostFilter;
import org.garin.core.secutiry.AppUserDetails;
import org.garin.core.service.PostService;
import org.garin.core.web.dto.CreatePostRequest;
import org.garin.core.web.dto.PageResponse;
import org.garin.core.web.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @GetMapping
  public ResponseEntity<PageResponse<PostDto>> getAllPosts(@RequestParam Integer pageSize,
      @RequestParam Integer pageNumber) {
    return constructFromPage(postService.findAll(PageRequest.of(pageNumber, pageSize)));
  }

  @GetMapping("/filter")
  public ResponseEntity<PageResponse<PostDto>> filterPosts(PostFilter filter) {
    return constructFromPage(
        postService.filter(filter, PageRequest.of(filter.getPageNumber(), filter.getPageSize())));
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<PostDto> createPost(@RequestBody CreatePostRequest post,
      @AuthenticationPrincipal UserDetails userDetails) {
    var currentUserId = ((AppUserDetails) userDetails).getId();
    var createdPost = postService.create(new Post(post.getText(), post.getTag()), currentUserId);

    return new ResponseEntity<>(new PostDto(createdPost.getId(), createdPost.getText(),
        createdPost.getTag(), createdPost.getAuthor().getUsername()), HttpStatus.CREATED);
  }

  @DeleteMapping("/{postId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId,
      @AuthenticationPrincipal UserDetails userDetails) {
    var currentUserId = ((AppUserDetails) userDetails).getId();
    postService.deleteById(postId, currentUserId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private ResponseEntity<PageResponse<PostDto>> constructFromPage(Page<Post> page) {
    var content = page.getContent().stream()
        .map(it -> new PostDto(it.getId(), it.getText(), it.getTag(), it.getAuthor().getUsername()))
        .toList();
    return new ResponseEntity<>(new PageResponse<PostDto>(content, page.getTotalPages()),
        HttpStatus.OK);
  }

}
