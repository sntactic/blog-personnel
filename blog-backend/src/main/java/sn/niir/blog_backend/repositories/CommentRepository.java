package sn.niir.blog_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sn.niir.blog_backend.models.Comment;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByArticleId(String articleId);

    void deleteByArticleId(String articleId);
}