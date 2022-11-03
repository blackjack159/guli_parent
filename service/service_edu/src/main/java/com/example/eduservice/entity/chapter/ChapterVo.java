package com.example.eduservice.entity.chapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChapterVo {

    private String id;

    private String title;

    //表示VideoVo视频小节
    private List<VideoVo> children = new ArrayList<>();
}
