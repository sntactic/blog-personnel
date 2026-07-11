package sn.niir.blog_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.niir.blog_backend.dto.CommentRequest;
import sn.niir.blog_backend.dto.CommentResponse;
import sn.niir.blog_backend.services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/article/{articleId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String articleId,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.addComment(articleId, request));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentResponse>> getByArticle(@PathVariable String articleId) {
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestAttribute("role") String role
    ) {
        commentService.deleteComment(id, role);
        return ResponseEntity.noContent().build();
    }
}