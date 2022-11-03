package com.example.eduservice.controller.front;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.ordervo.CourseWebVoOrder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eduservice.client.OrdersClient;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.chapter.ChapterVo;
import com.example.eduservice.entity.frontvo.CourseFrontVo;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.service.EduChapterService;
import com.example.eduservice.service.EduCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/coursefront")
//@CrossOrigin
public class CourseFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrdersClient ordersClient;
    
    //1. pagination for courselist
    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page, @PathVariable long limit,
                                @RequestBody(required = false) CourseFrontVo courseFrontVo){
        Page<EduCourse> pageCourse = new Page<>(page,limit);
        Map<String,Object> map = courseService.getCourseFrontList(pageCourse,courseFrontVo);

        return R.ok().data(map);
    }
    
    //2. course detail method
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request){
        //sql inquiry based on course Id
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId);

        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);

        //check the course is paid based on course id and user id

        boolean buyCourse = ordersClient.isBuyCourse(courseId, JwtUtils.getMemberIdByJwtToken(request));

        return R.ok().data("courseWebVo",courseWebVo).data("chapterVideoList",chapterVideoList).data("isBuy",buyCourse);
    }

    //get course detail via course id
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOder(@PathVariable String id){
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id);
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo,courseWebVoOrder);
        return courseWebVoOrder;

    }

    //update buy count
    @PostMapping("updateBuyCount/{courseId}")
    public boolean updateBuyCount(@PathVariable String courseId){
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("id",courseId);
        EduCourse course = courseService.getOne(wrapper);
        course.setBuyCount(course.getBuyCount()+1);
        boolean flag = courseService.updateById(course);
        if(flag){
            return true;
        }else{
            return false;
        }

    }

    //update view count
    @GetMapping("updateViewCount/{courseId}")
    public R updateViewCount(@PathVariable String courseId){
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("id",courseId);
        EduCourse course = courseService.getOne(wrapper);
        course.setViewCount(course.getViewCount()+1);
        boolean flag = courseService.updateById(course);
        if(flag){
            return R.ok();
        }else{
            return R.ok();
        }
    }
}
