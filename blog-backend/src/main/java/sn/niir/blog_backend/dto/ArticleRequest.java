package sn.niir.blog_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import sn.niir.blog_backend.models.Article;

import java.util.List;

@Getter
@Setter
public class ArticleRequest {

    @NotBlank(message = "Le titre est requis")
    private String title;

    @NotBlank(message = "Le contenu est requis")
    private String content;

    @NotEmpty(message = "Au moins un tag est requis")
    private List<String> tags;

    private List<String> images;

    private Article.ArticleStatus status; // DRAFT ou PUBLISHED
}