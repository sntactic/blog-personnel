package sn.niir.blog_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.niir.blog_backend.dto.ArticleRequest;
import sn.niir.blog_backend.dto.ArticleResponse;
import sn.niir.blog_backend.exceptions.ResourceNotFoundException;
import sn.niir.blog_backend.exceptions.UnauthorizedException;
import sn.niir.blog_backend.models.Article;
import sn.niir.blog_backend.repositories.ArticleRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleResponse createArticle(ArticleRequest request, String authorId) {
        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(authorId)
                .tags(request.getTags())
                .images(request.getImages())
                .status(request.getStatus() != null ? request.getStatus() : Article.ArticleStatus.DRAFT)
                .views(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ArticleResponse.fromEntity(articleRepository.save(article));
    }

    public ArticleResponse getArticleById(String id) {
        Article article = findArticleOrThrow(id);
        return ArticleResponse.fromEntity(article);
    }

    public List<ArticleResponse> getPublishedArticles() {
        return articleRepository.findByStatus(Article.ArticleStatus.PUBLISHED).stream()
                .map(ArticleResponse::fromEntity)
                .toList();
    }

    public List<ArticleResponse> getArticlesByAuthor(String authorId) {
        return articleRepository.findByAuthorId(authorId).stream()
                .map(ArticleResponse::fromEntity)
                .toList();
    }

    public List<ArticleResponse> getArticlesByTag(String tag) {
        return articleRepository.findByTagsContaining(tag).stream()
                .map(ArticleResponse::fromEntity)
                .toList();
    }

    public ArticleResponse updateArticle(String id, ArticleRequest request, String userId, String role) {
        Article article = findArticleOrThrow(id);
        checkOwnership(article, userId, role);

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setTags(request.getTags());
        article.setImages(request.getImages());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        article.setUpdatedAt(LocalDateTime.now());

        return ArticleResponse.fromEntity(articleRepository.save(article));
    }

    public void deleteArticle(String id, String userId, String role) {
        Article article = findArticleOrThrow(id);
        checkOwnership(article, userId, role);
        articleRepository.delete(article);
    }

    private Article findArticleOrThrow(String id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable : " + id));
    }

    private void checkOwnership(Article article, String userId, String role) {
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = article.getAuthorId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Vous ne pouvez modifier que vos propres articles");
        }
    }
}