package com.jcf.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long ExpiresIn) {
}
