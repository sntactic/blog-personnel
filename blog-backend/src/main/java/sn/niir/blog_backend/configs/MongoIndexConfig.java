package sn.niir.blog_backend.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import sn.niir.blog_backend.models.Article;
import sn.niir.blog_backend.models.Comment;
import sn.niir.blog_backend.models.User;


@Configuration
public class MongoIndexConfig implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mappingContext;

    public MongoIndexConfig(MongoTemplate mongoTemplate, MongoMappingContext mappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mappingContext = mappingContext;
    }

    @Override
    public void run(String... args) {
        ensureIndexesFor(User.class);
        ensureIndexesFor(Article.class);
        ensureIndexesFor(Comment.class);
    }

    private void ensureIndexesFor(Class<?> entityClass) {
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
        IndexOperations indexOps = mongoTemplate.indexOps(entityClass);

        resolver.resolveIndexFor(entityClass)
                .forEach(indexOps::createIndex);
    }
}