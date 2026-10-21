package com.java.springvadin.service;

public interface TokenBlacklistService {

    void addToBlacklist(String token, long expirationTimeMillis);
    boolean isBlacklisted(String token);

}
