package sn.niir.blog_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.niir.blog_backend.dto.ArticleRequest;
import sn.niir.blog_backend.dto.ArticleResponse;
import sn.niir.blog_backend.services.ArticleService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleResponse> create(
            @Valid @RequestPart("article") ArticleRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestAttribute("userId") String userId
    ) {
        return ResponseEntity.ok(
                articleService.createArticle(request, images != null ? images : Collections.emptyList(), userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getPublished() {
        return ResponseEntity.ok(articleService.getPublishedArticles());
    }


    @GetMapping("/my-articles")
    public ResponseEntity<List<ArticleResponse>> getMyArticles(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(articleService.getMyArticles(userId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleResponse>> getByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<ArticleResponse>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tag));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleResponse> update(
            @PathVariable String id,
            @Valid @RequestPart("article") ArticleRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestAttribute("userId") String userId,
            @RequestAttribute("role") String role
    ) {
        return ResponseEntity.ok(
                articleService.updateArticle(id, request, images != null ? images : Collections.emptyList(), userId, role)
        );
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