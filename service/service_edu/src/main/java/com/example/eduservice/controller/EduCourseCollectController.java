package com.example.eduservice.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eduservice.client.UcenterClient;
import com.example.eduservice.entity.EduComment;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseCollect;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.service.EduCourseCollectService;
import com.example.eduservice.service.EduCourseService;
import com.example.eduservice.service.EduTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程收藏 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-08-16
 */
@RestController
@RequestMapping("/eduservice/collect")
public class EduCourseCollectController {

    @Autowired
    private UcenterClient ucenterClient;

    @Autowired
    private EduCourseCollectService eduCourseCollectService;

    @Autowired
    private EduCourseService eduCourseService;

//    @Autowired
//    private EduTeacherService eduTeacherService;

    //添加评论
    @PostMapping("/addCollect")
    public R addCollect(HttpServletRequest request, @RequestBody EduCourseCollect eduCollect){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //判断用户是否登录
        if (StringUtils.isEmpty(memberId)){
            throw new GuliException(28004,"Please login first");
        }
        eduCollect.setMemberId(memberId);

//        eduComment.setMemberId(memberId);

        //远程调用ucenter根据用户id获取用户信息
//        UcenterMemberOrder memberVo = ucenterClient.getUserInfoOrder(memberId);
//        System.out.println(memberVo);

//        eduCollect.setCourseId(eduCollect.getCourseId());
//        eduComment.setAvatar(memberVo.getAvatar());
//        eduComment.setNickname(memberVo.getNickname());

        //保存评论
//        eduCommentService.save(eduComment);
        eduCourseCollectService.save(eduCollect);

        return R.ok();
    }

    //删除collection from the user
    @DeleteMapping("{courseId}")
    public R undoCollect(@PathVariable String courseId, HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.eq("course_id",courseId);
        EduCourseCollect resultCollect = eduCourseCollectService.getOne(wrapper);
        eduCourseCollectService.removeById(resultCollect.getId());
        return R.ok();
    }

    @PostMapping("/getCollectStat/{courseId}")
    public R getCollectStat(@PathVariable String courseId, HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.eq("course_id",courseId);
        EduCourseCollect resultCollect = eduCourseCollectService.getOne(wrapper);
//        EduCourseCollect resultCollect = eduCourseCollectService.getById(wrapper);
        Boolean stat;
        if(resultCollect != null){
            stat = true;
        }else{
            stat = false;
        }
        return R.ok().data("stat",stat);
    }

    //根据user id ,获得全部course for collection page
    @PostMapping("/getCollectList")
    public R getCollectList(HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
//        String memberId = "1556161539277352961";
        QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        Page<EduCourseCollect> collectPage = new Page<>(1, 20);
        //按最新排序
        wrapper.orderByDesc("gmt_create");
        eduCourseCollectService.page(collectPage,wrapper);

        List<CourseWebVo> results = eduCourseService.getCollectCourseInfo(memberId);

        Map<String,Object> map = new HashMap<>();
        map.put("total",collectPage.getTotal());
        map.put("list",results);

        return R.ok().data(map);
    }

    @PostMapping("/searchCourse")
    public R searchCollectList(HttpServletRequest request, @RequestBody(required = false) EduCourseCollect eduCollect){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
//        String memberId = "1556161539277352961";
        QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        Page<EduCourseCollect> collectPage = new Page<>(1, 20);
        //按最新排序
        wrapper.orderByDesc("gmt_create");
        eduCourseCollectService.page(collectPage,wrapper);

//        List<EduCourseCollect> collectList = collectPage.getRecords();
//        List<CourseWebVo> courses = new ArrayList<>();
//        for(EduCourseCollect course:collectList){
//            EduCourse courseResult = eduCourseService.getById(course.getCourseId());
//            EduTeacher teacherResult = eduTeacherService.getById(courseResult.getTeacherId());
//            CourseWebVo courseWebVo = new CourseWebVo();
//            courseWebVo.setId(courseResult.getId());
//            courseWebVo.setPrice(courseResult.getPrice());
//            courseWebVo.setCover(courseResult.getCover());
//            courseWebVo.setTitle(courseResult.getTitle());
//            courseWebVo.setTeacherName(teacherResult.getName());
//            courses.add(courseWebVo);
//        }
        List<CourseWebVo> results;
        if(!StringUtils.isEmpty(eduCollect.getCourseId())){
            String nContent = "%" + eduCollect.getCourseId() + "%";
            results = eduCourseService.getLikeCollectCourseInfo(memberId,nContent);
        }else{
            results = eduCourseService.getCollectCourseInfo(memberId);
        }


        Map<String,Object> map = new HashMap<>();
        map.put("total",collectPage.getTotal());
        map.put("list",results);

        return R.ok().data(map);
    }
}

