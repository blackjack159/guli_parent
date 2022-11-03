package com.atguigu.staservice.schedule;

import com.atguigu.staservice.service.StatisticsDailyService;
import com.atguigu.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService staService;

//    //（0/5 * * * * ？）：execute every five second
//    @Scheduled(cron = "0/5 * * * * ?")//cron expression
//    public void task1(){
//        System.out.println("********Task 1 is executed...");
//    }

    @Scheduled(cron = "0 0 1 * * ?")//execute everyday 1am
    public void task2(){
        staService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(),-1)));
        System.out.println("********Task 2 is executed...");
    }
}
