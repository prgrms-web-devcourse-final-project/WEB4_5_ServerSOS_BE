package com.pickgo.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCreateRequest {

    private Long id;
    private String title;
    private Long venueId;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String poster;

}
