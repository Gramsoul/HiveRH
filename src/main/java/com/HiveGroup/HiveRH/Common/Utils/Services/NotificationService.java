package com.HiveGroup.HiveRH.Common.Utils.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    public void notify(String email, String message){
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(email);
        mail.setSubject("Notificacion HiveGroup");
        mail.setText(message);

        mailSender.send(mail);
        System.out.println("email: "+ email + " Subject: "+mail.getSubject()+ " Message: "+message);
    }
}
