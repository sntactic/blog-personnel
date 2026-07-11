package sn.niir.blog_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sn.niir.blog_backend.dto.ArticleRequest;
import sn.niir.blog_backend.dto.ArticleResponse;
import sn.niir.blog_backend.exceptions.ResourceNotFoundException;
import sn.niir.blog_backend.exceptions.UnauthorizedException;
import sn.niir.blog_backend.models.Article;
import sn.niir.blog_backend.models.User;
import sn.niir.blog_backend.repositories.ArticleRepository;
import sn.niir.blog_backend.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;

    public ArticleResponse createArticle(ArticleRequest request, List<MultipartFile> images, String authorId) {
        List<String> imageUrls = uploadImages(images);
        String authorName = resolveAuthorName(authorId);

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .tags(request.getTags())
                .images(imageUrls)
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

    public ArticleResponse updateArticle(String id, ArticleRequest request, List<MultipartFile> images, String userId, String role) {
        Article article = findArticleOrThrow(id);
        checkOwnership(article, userId, role);

        List<String> currentImages = article.getImages() != null ? article.getImages() : List.of();
        List<String> imagesToKeep = request.getExistingImages() != null ? request.getExistingImages() : List.of();

        // Supprime de MinIO toute image présente en base mais absente de la liste à conserver
        currentImages.stream()
                .filter(imageUrl -> !imagesToKeep.contains(imageUrl))
                .forEach(minioService::deleteImage);

        // Upload les fichiers reçus (toujours nouveaux par nature) et construit la liste finale
        List<String> uploadedUrls = uploadImages(images);
        List<String> finalImages = new ArrayList<>(imagesToKeep);
        finalImages.addAll(uploadedUrls);

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setTags(request.getTags());
        article.setImages(finalImages);
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        article.setUpdatedAt(LocalDateTime.now());

        return ArticleResponse.fromEntity(articleRepository.save(article));
    }

    public void deleteArticle(String id, String userId, String role) {
        Article article = findArticleOrThrow(id);
        checkOwnership(article, userId, role);

        if (article.getImages() != null) {
            article.getImages().forEach(minioService::deleteImage);
        }

        articleRepository.delete(article);
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        return images.stream()
                .filter(file -> !file.isEmpty())
                .map(minioService::uploadImage)
                .toList();
    }

    private String resolveAuthorName(String authorId) {
        return userRepository.findById(authorId)
                .map(User::getFullName)
                .orElse("Auteur inconnu");
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