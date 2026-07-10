package sn.niir.blog_backend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    private String id;

    private String title;

    private String content;

    @Indexed
    private String authorId; // référence vers User

    @Indexed
    private List<String> tags;

    private List<String> images; // URLs MinIO

    private ArticleStatus status; // DRAFT, PUBLISHED

    private int views;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum ArticleStatus {
        DRAFT,
        PUBLISHED
    }
}