package sn.niir.blog_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sn.niir.blog_backend.models.Article;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ArticleResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private List<String> tags;
    private List<String> images;
    private Article.ArticleStatus status;
    private int views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleResponse fromEntity(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .authorId(article.getAuthorId())
                .tags(article.getTags())
                .images(article.getImages())
                .status(article.getStatus())
                .views(article.getViews())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}