package com.atguigu.msmservice.controller;

import com.atguigu.commonutils.R;
import com.atguigu.msmservice.service.MsmService;
import com.atguigu.msmservice.utils.RandomUtil;
import com.atguigu.msmservice.utils.SendEmail;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Slf4j
//@RequiredArgsConstructor
@RestController
@RequestMapping("/msmservice/msm")
//@CrossOrigin
public class MsmController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @GetMapping("/send/{phone}/{email}")
    @ApiOperation("发送验证码")
    public R sendMsm(@PathVariable("phone") String phone, @PathVariable("email") String email) {
        //get MSOS code from redis
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)){
            return R.ok();
        }

        code = RandomUtil.getFourBitRandom();
//        Map<String,Object> param = new HashMap<>();
//        param.put("code",code);
//        boolean isSend= msmService.send(param,phone);
        boolean isSend = msmService.sendMail(code,email);
        if(isSend){
            // if send success, put it into redis
            //set the valid time
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else{
            return R.error().message("Email failed to send");
        }
//        SendEmail.sendMail();
    }

//    private final JavaMailSender javaMailSender;

//    @Value("${spring.mail.username}")
//    private String account;
//
//
//    @ApiOperation("Send Simple Email")
//    @PostMapping("sendSimpleMail")
//    public String sendSimpleMail(
//            @ApiParam("Receiver") @RequestParam String address,
//            @ApiParam("Title") @RequestParam String subject,
//            @ApiParam("Content") @RequestParam String body) {
//        SimpleMailMessage smm = new SimpleMailMessage();
//        smm.setFrom(account);
//        smm.setTo(address);
//        smm.setSubject(subject);
//        smm.setText(body);
//        javaMailSender.send(smm);
//        return "发送成功";
//    }


}
