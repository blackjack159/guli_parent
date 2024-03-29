package com.example.eduservice.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseQuery {

    @ApiModelProperty(value = "Course name,LIKELY SEARCH")
    private String title;

    @ApiModelProperty(value = "Course status")
    private String status;

    @ApiModelProperty(value = "查询开始时间", example = "2022-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询结束时间", example = "2022-12-01 10:10:10")
    private String end;
}
