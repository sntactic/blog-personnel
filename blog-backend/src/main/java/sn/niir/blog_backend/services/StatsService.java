package sn.niir.blog_backend.services;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import sn.niir.blog_backend.dto.MonthlyArticleCount;
import sn.niir.blog_backend.dto.TagCount;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MongoTemplate mongoTemplate;

    /**
     * Nombre d'articles publiés par mois (format "YYYY-MM"), triés chronologiquement.
     */
    public List<MonthlyArticleCount> getArticlesPerMonth() {
        Aggregation aggregation = Aggregation.newAggregation(
                context -> new Document("$project", new Document("month",
                        new Document("$dateToString", new Document("format", "%Y-%m").append("date", "$createdAt")))),
                context -> new Document("$group", new Document("_id", "$month").append("count", new Document("$sum", 1))),
                context -> new Document("$sort", new Document("_id", 1))
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "articles", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new MonthlyArticleCount(doc.getString("_id"), toLong(doc.get("count"))))
                .toList();
    }

    /**
     * Les 10 tags les plus utilisés, triés du plus au moins fréquent.
     */
    public List<TagCount> getPopularTags() {
        Aggregation aggregation = Aggregation.newAggregation(
                context -> new Document("$unwind", "$tags"),
                context -> new Document("$group", new Document("_id", "$tags").append("count", new Document("$sum", 1))),
                context -> new Document("$sort", new Document("count", -1)),
                context -> new Document("$limit", 10)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "articles", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> new TagCount(doc.getString("_id"), toLong(doc.get("count"))))
                .toList();
    }

    private long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}