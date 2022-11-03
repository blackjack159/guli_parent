package com.atguigu.msmservice.service.impl;

import com.atguigu.msmservice.service.MsmService;
import com.atguigu.msmservice.utils.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MsmServiceImpl implements MsmService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String account;
    @Override
    public boolean send(Map<String, Object> param, String phone) {
        if(StringUtils.isEmpty(phone)){
            return false;
        }
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(account);
        smm.setTo(phone); //receiver email address
        smm.setSubject("Reset Password");
        smm.setText("Please enter this code to reset your password:" + RandomUtil.getFourBitRandom());
        javaMailSender.send(smm);
        return true;
    }

    @Override
    public boolean sendMail(String code, String phone) {
        if(StringUtils.isEmpty(phone)){
            return false;
        }
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(account);
        smm.setTo(phone); //receiver email address
        smm.setSubject("Reset Password for Guideline Education Account");
        smm.setText("Please enter this code to reset your password:" + code);
        javaMailSender.send(smm);
        return true;
    }
}
