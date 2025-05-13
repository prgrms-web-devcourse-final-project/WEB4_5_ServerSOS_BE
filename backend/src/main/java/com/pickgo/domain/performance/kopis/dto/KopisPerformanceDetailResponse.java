package com.pickgo.domain.performance.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class KopisPerformanceDetailResponse {
    @JacksonXmlProperty(localName = "prfnm")
    String name;
    @JacksonXmlProperty(localName = "prfpdfrom")
    String startDate;
    @JacksonXmlProperty(localName = "prfpdto")
    String endDate;
    @JacksonXmlProperty(localName = "prfruntime")
    String runtime;
    @JacksonXmlProperty(localName = "poster")
    String poster;
    @JacksonXmlProperty(localName = "prfstate")
    String state;
    @JacksonXmlProperty(localName = "prfage")
    String minAge;
    @JacksonXmlProperty(localName = "prfcast")
    String casts;
    @JacksonXmlProperty(localName = "genrenm")
    String type;
    @JacksonXmlProperty(localName = "mt10id")
    String venueId;
    @JacksonXmlProperty(localName = "dtguidance")
    String schedule;
    @JacksonXmlElementWrapper(localName = "styurls")
    @JacksonXmlProperty(localName = "styurl")
    List<String> introImages;
}