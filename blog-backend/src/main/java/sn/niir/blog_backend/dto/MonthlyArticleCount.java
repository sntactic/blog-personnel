package sn.niir.blog_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyArticleCount {
    private String month; // format "YYYY-MM"
    private long count;
}