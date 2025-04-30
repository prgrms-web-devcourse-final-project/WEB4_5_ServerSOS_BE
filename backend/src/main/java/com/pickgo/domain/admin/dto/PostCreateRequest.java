package com.pickgo.domain.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PostCreateRequest {

    private Long id;
    private String title;
    private Long venueId;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String poster;

}
