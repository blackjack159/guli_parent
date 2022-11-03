package com.example.eduservice.controller;


import com.atguigu.commonutils.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CoursePublishVo;
import com.example.eduservice.entity.vo.CourseQuery;
import com.example.eduservice.entity.vo.TeacherQuery;
import com.example.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-05-25
 */
@RestController
@RequestMapping("/eduservice/course")
//@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    //Course List列表 TODO complete inquiry and paging
    @GetMapping("")
    public R getCourseList(){
        List<EduCourse> list = courseService.list(null);
        return R.ok().data("list",list);
    }

    //pagination for course list
    @GetMapping("pageCourse/{current}/{limit}")
    public R pageListTeacher(@PathVariable long current,
                             @PathVariable long limit){
        //instantiate page
        Page<EduCourse> pageCourse = new Page<>(current,limit);//current= current page, limit = number of entries per page. Import maven package

        //invoke method to achieve pagination
        //provide the pagination data into pageTeacher object
        courseService.page(pageCourse,null); //wrapper = regulation

        long total = pageCourse.getTotal(); // total entries
        List<EduCourse> records = pageCourse.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //Query pagination with condition for course list
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) CourseQuery courseQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page objectD
        Page<EduCourse> pageCourse = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //dynamic sql
        String title = courseQuery.getTitle();
        String status = courseQuery.getStatus();
        String begin = courseQuery.getBegin();
        String end = courseQuery.getEnd();
        //start to build up the sql condition
        if(!StringUtils.isEmpty(title)){
            wrapper.like("title",title); //the first parameter is name of table in database
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
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
        courseService.page(pageCourse,wrapper);

        long total = pageCourse.getTotal(); // total entries
        List<EduCourse> records = pageCourse.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //添加课程基本信息的方法
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        //get the id for course_decription table
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId",id);
    }

    //根据课程id查询课程基本信息 （回显）
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo",courseInfoVo);
    }

    //修改课程信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    //根据id获得课程信息（最终post)
    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVo coursePublishVo = courseService.publishCourseInfo(id);
        return R.ok().data("publishCourse",coursePublishVo);
    }

    //course post (最终)
    //修改课程状态 (draft -> normal)
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);

        return R.ok();
    }

    //删除课程在course list
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }
}

