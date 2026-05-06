package com.fii.ewallet.email.service;

public interface EmailService {

    void sendVerificationEmail(String to, String link);

}
