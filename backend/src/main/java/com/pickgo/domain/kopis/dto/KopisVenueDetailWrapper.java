package com.pickgo.domain.kopis.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record KopisVenueDetailWrapper(
        @JacksonXmlProperty(localName = "db")
        KopisVenueDetailResponse detail
) {
}
