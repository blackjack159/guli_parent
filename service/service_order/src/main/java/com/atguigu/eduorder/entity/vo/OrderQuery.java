package com.atguigu.eduorder.entity.vo;

import lombok.Data;

@Data
public class OrderQuery {
    private String memberId;

    private String status;

    private String courseTitle;

    private String begin;

    private String end;

}
