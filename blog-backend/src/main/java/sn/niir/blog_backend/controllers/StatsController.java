package sn.niir.blog_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.niir.blog_backend.dto.MonthlyArticleCount;
import sn.niir.blog_backend.dto.TagCount;
import sn.niir.blog_backend.services.StatsService;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/articles-per-month")
    public ResponseEntity<List<MonthlyArticleCount>> getArticlesPerMonth() {
        return ResponseEntity.ok(statsService.getArticlesPerMonth());
    }

    @GetMapping("/popular-tags")
    public ResponseEntity<List<TagCount>> getPopularTags() {
        return ResponseEntity.ok(statsService.getPopularTags());
    }
}