package com.backend.backend.dto.common;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
