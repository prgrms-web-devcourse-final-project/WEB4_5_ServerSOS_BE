package com.pickgo.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    private Long performanceId;
    private String title;
    private String content;
    private Boolean isPublished;

}
