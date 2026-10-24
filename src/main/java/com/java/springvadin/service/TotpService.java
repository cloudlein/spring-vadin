package com.java.springvadin.service;

public interface TotpService {

    String generateSecretKey();
    String generateQrCodeDataUri(String secret, String accountName, String issuer);

    boolean verifyCode(String secret, String code);

}
