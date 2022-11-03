package com.atguigu.servicebase.exceptionhandler;

import com.atguigu.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//统一处理异常的controller advice
@ControllerAdvice
@Slf4j //for logback
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) //指定出现什么exception,执行这个method
    @ResponseBody //返回 data
    public R error(Exception e){ //这个R要加dependency 在 service_base下的 pom.xml
        e.printStackTrace();
        return R.error().message("Executed Global Exception Handling!!!");
    }

    //specific exception
    @ExceptionHandler(ArithmeticException.class) //指定出现什么exception,执行这个method
    @ResponseBody //返回 data
    public R error(ArithmeticException e){
        e.printStackTrace();
        return R.error().message("Executed Arithmetic Exception Handling!!!");
    }

    //self defined exception
    @ExceptionHandler(GuliException.class) //指定出现什么exception,执行这个method
    @ResponseBody //返回 data
    public R error(GuliException e){
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMsg());
    }

}
