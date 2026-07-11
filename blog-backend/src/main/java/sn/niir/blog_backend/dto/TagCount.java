package sn.niir.blog_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagCount {
    private String tag;
    private long count;
}