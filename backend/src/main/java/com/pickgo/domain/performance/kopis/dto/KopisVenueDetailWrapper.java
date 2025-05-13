package com.pickgo.domain.performance.kopis.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record KopisVenueDetailWrapper(
        @JacksonXmlProperty(localName = "db")
        KopisVenueDetailResponse detail
) {
}
