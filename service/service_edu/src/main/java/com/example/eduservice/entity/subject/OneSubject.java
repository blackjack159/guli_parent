package com.example.eduservice.entity.subject;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//一级分类first level
@Data
public class OneSubject {

    private String id;
    private String title;

    //一个first level contains multiple secondary level
    private List<TwoSubject> children = new ArrayList<>();
}
