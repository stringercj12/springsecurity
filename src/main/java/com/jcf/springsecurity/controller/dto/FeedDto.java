package com.jcf.springsecurity.controller.dto;

import java.util.List;

public record FeedDto(
        List<FeedItemDto> feedItems,
        int page,
        int pageSize,
        int totalPage,
        long totalElements
) {
}
