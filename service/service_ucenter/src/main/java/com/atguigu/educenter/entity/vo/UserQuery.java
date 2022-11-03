package com.atguigu.educenter.entity.vo;

import lombok.Data;

@Data
public class UserQuery {
    private String id;

    private String nickname;

    private String mobile;

    private Boolean isDisabled;
}
