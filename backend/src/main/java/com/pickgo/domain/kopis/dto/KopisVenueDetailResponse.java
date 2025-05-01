package com.pickgo.domain.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisVenueDetailResponse(
        @JacksonXmlProperty(localName = "fcltynm")
        String name,
        @JacksonXmlProperty(localName = "adres")
        String address
) {
}
