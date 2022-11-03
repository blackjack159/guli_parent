package com.example.eduservice.controller;


import com.atguigu.commonutils.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.vo.TeacherQuery;
import com.example.eduservice.service.EduTeacherService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-05-22
 */
@RestController
@RequestMapping("/eduservice/teacher")
//@CrossOrigin
public class EduTeacherController {

    //access address: http://localhost:8001/eduservice/teacher/findAll , depends on @RequestMapping()
    //autowire the service bean
    @Autowired
    private EduTeacherService teacherService;

    //1. select all the entries in edu_teacher
    @GetMapping("findAll")
    public R findAllTeacher(){
        List<EduTeacher> list = teacherService.list(null);
        return R.ok().data("items",list); //it's a map variable to store the data as JSON

    }

    //rest style to get the parameter
    //2. delete the target entry
    @DeleteMapping("{id}")
    public R removeTeacher(@PathVariable String id){
        boolean flag = teacherService.removeById(id);
        if(flag){ //if delete operation success
            return R.ok();
        }else{ //if delete operation failed
            return R.error();
        }

    }

    //3. pagination for teacher entries
    @GetMapping("pageTeacher/{current}/{limit}")
    public R pageListTeacher(@PathVariable long current,
                             @PathVariable long limit){
        //instantiate page
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);//current= current page, limit = number of entries per page. Import maven package

        //invoke method to achieve pagination
        //provide the pagination data into pageTeacher object
        teacherService.page(pageTeacher,null); //wrapper = regulation

        long total = pageTeacher.getTotal(); // total entries
        List<EduTeacher> records = pageTeacher.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //4. Query pagination with conditions
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) TeacherQuery teacherQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page object
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        //dynamic sql
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        //start to build up the sql condition
        if(!StringUtils.isEmpty(name)){
            wrapper.like("name",name); //the first parameter is name of table in database
        }
        if(!StringUtils.isEmpty(level)){
            wrapper.eq("level",level);
        }
        if(!StringUtils.isEmpty(begin)){
            wrapper.ge("gmt_create",begin);
        }
        if(!StringUtils.isEmpty(end)){
            wrapper.le("gmt_create",end);
        }

        //sorting for frontend
        wrapper.orderByDesc("gmt_create");

        //invoke method to achieve pagination
        teacherService.page(pageTeacher,wrapper);

        long total = pageTeacher.getTotal(); // total entries
        List<EduTeacher> records = pageTeacher.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //add teacher with interface method，使用了MyMetaObjectHandler 来实现自动填上日期
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        boolean save = teacherService.save(eduTeacher);
        if(save){
            return R.ok();
        }else{
            return R.error();
        }

    }

    //select teacher with id
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        EduTeacher eduTeacher = teacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);

    }

    //update teacher
    @PostMapping("updateTeacher") //can use put , but it will be complex
    public R updateTeacher(@RequestBody EduTeacher eduTeacher){
        boolean flag = teacherService.updateById(eduTeacher); //use front end to obtain only id value
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }

    }
}

