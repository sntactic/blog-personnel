package sn.niir.blog_backend.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import sn.niir.blog_backend.models.Article;

import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, String> {

    List<Article> findByAuthorId(String authorId);

    List<Article> findByTagsContaining(String tag);

    List<Article> findByStatus(Article.ArticleStatus status);
}