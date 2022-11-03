package com.atguigu.servicebase.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter and setter
@AllArgsConstructor //constructor with full param
@NoArgsConstructor //constructor without param
public class GuliException extends RuntimeException {

    private Integer code; //status code
    private String msg; //error message
}
