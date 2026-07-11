package sn.niir.blog_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.niir.blog_backend.dto.ArticleRequest;
import sn.niir.blog_backend.dto.ArticleResponse;
import sn.niir.blog_backend.services.ArticleService;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> create(
            @Valid @RequestBody ArticleRequest request,
            @RequestAttribute("userId") String userId
    ) {
        return ResponseEntity.ok(articleService.createArticle(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getPublished() {
        return ResponseEntity.ok(articleService.getPublishedArticles());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleResponse>> getByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<ArticleResponse>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ArticleRequest request,
            @RequestAttribute("userId") String userId,
            @RequestAttribute("role") String role
    ) {
        return ResponseEntity.ok(articleService.updateArticle(id, request, userId, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestAttribute("userId") String userId,
            @RequestAttribute("role") String role
    ) {
        articleService.deleteArticle(id, userId, role);
        return ResponseEntity.noContent().build();
    }
}