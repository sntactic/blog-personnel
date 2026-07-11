package sn.niir.blog_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    @NotBlank(message = "Le nom est requis")
    private String authorName;

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String content;
}