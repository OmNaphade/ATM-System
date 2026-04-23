package com.omnaphade.dtos;

public record LoginResponse(String token, String refreshToken, String message) {
}
