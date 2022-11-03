package com.example.eduservice.controller;


import com.atguigu.commonutils.R;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.subject.OneSubject;
import com.example.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-05-25
 */
@RestController
@RequestMapping("/eduservice/edu-subject")
//@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    //添加课程分类
    //获取上传来的file,把file内容read出来
    @PostMapping("addSubject")
    public R addSubject(MultipartFile file){
        //upload过来excel文件
        subjectService.saveSubject(file,subjectService);
        return R.ok();
    }

    //课程分类列表（tree）
    @GetMapping("getAllSubject")
    public R getAllSubject(){
        List<OneSubject> list = subjectService.getAllOneTwoSubject();
        return R.ok().data("list",list); //return map
    }
}

