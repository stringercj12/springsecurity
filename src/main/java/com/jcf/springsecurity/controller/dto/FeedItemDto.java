package com.jcf.springsecurity.controller.dto;

public record FeedItemDto(
        Long tweetId,
        String content,
        String username
) {
}
