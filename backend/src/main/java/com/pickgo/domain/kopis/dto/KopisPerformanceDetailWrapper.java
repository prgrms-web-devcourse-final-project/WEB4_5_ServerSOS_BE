package com.pickgo.domain.kopis.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record KopisPerformanceDetailWrapper(
        @JacksonXmlProperty(localName = "db")
        KopisPerformanceDetailResponse detail
) {
}