package com.example.simplesms.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})

public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("number") int number,
                    @JsonProperty("size") int size,
                    @JsonProperty("totalElements") Long totalElements,
                    @JsonProperty("last") boolean last,
                    @JsonProperty("totalPages") int totalPages,
                    @JsonProperty("first") boolean first,
                    @JsonProperty("numberOfElements") int numberOfElements
    ) {


        super(content, totalElements == 0 ? Pageable.unpaged() : PageRequest.of(number, size), totalElements);
    }

    public static <T> RestPage<T> of(Page<T> page) {
        return new RestPage<T>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.isLast(),
                page.getTotalPages(),
                page.isFirst(),
                page.getNumberOfElements());

    }

    public static <T> RestPage<T> empty() {
        return new RestPage<>(Collections.emptyList());
    }

    private RestPage(List<T> content) {
        super(content);
    }
}