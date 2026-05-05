package com.fii.ewallet.email.impl;

import com.fii.ewallet.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class sendVerificationEmailImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String link) {
            SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(to);
                msg.setSubject("Verify your account");
                msg.setText("Click to verify: " + link);

                mailSender.send(msg);
    }

}
