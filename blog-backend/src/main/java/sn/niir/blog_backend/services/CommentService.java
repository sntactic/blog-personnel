package sn.niir.blog_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.niir.blog_backend.dto.CommentRequest;
import sn.niir.blog_backend.dto.CommentResponse;
import sn.niir.blog_backend.exceptions.ResourceNotFoundException;
import sn.niir.blog_backend.exceptions.UnauthorizedException;
import sn.niir.blog_backend.models.Comment;
import sn.niir.blog_backend.repositories.ArticleRepository;
import sn.niir.blog_backend.repositories.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public CommentResponse addComment(String articleId, CommentRequest request) {
        // Vérifie que l'article existe avant d'accepter un commentaire
        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article introuvable : " + articleId);
        }

        Comment comment = Comment.builder()
                .articleId(articleId)
                .authorName(request.getAuthorName())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return CommentResponse.fromEntity(commentRepository.save(comment));
    }

    public List<CommentResponse> getCommentsByArticle(String articleId) {
        return commentRepository.findByArticleId(articleId).stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    // Modération : réservé à l'ADMIN
    public void deleteComment(String id, String role) {
        if (!"ADMIN".equals(role)) {
            throw new UnauthorizedException("Seul un administrateur peut supprimer un commentaire");
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable : " + id));

        commentRepository.delete(comment);
    }
}