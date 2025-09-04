package com.backend.backend.util;

import com.backend.backend.dto.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageMapper {
    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .items(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
