package com.atguigu.msmservice.utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail {

    public static void sendMail() {

    }
//    public static void sendMail(){
//
//        // Recipient's email ID needs to be mentioned.
//        String from = "chankaichun36@gmail.com";
//
//        // Sender's email ID needs to be mentioned
//        String to = "i19017235@student.newinti.edu.my";
//
//        // Assuming you are sending email from localhost
//        String host = "smtp.gmail.com";
//
//        // Get system properties
//        Properties properties = System.getProperties();
//
//        // Setup mail server
////        properties.setProperty("mail.smtp.host", host);
//        properties.put("mail.smtp.host", host);
//        properties.put("mail.smtp.port", "587");
//        properties.put("mail.smtp.ssl.enable", "true");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.socketFactory.fallback", "true");
//
//        // Get the default Session object.
////        Session session = Session.getDefaultInstance(properties);
//
//        // Get the Session object.// and pass username and password
//        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//
//            protected PasswordAuthentication getPasswordAuthentication() {
//
//                return new PasswordAuthentication("chankaichun36@gmail.com", "uopmmqdbtneosrsm");
//
//            }
//
//        });
//
//        // Used to debug SMTP issues
//        session.setDebug(true);
//        try {
//            // Create a default MimeMessage object.
//            MimeMessage message = new MimeMessage(session);
//
//            // Set From: header field of the header.
//            message.setFrom(new InternetAddress(from));
//
//            // Set To: header field of the header.
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//            // Set Subject: header field
//            message.setSubject("This is the Subject Line!");
//
//            // Now set the actual message
//            message.setText("This is actual message");
//
//            // Send message
//            Transport.send(message);
//            System.out.println("Sent message successfully....");
//        } catch (MessagingException mex) {
//            mex.printStackTrace();
//        }
//    }
}
