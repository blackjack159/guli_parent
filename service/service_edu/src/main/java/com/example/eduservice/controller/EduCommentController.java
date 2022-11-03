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
import com.example.eduservice.entity.vo.CommentQuery;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CourseQuery;
import com.example.eduservice.service.EduCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-08-15
 */
@RestController
@RequestMapping("/eduservice/edu-comment")
public class EduCommentController {

    @Autowired
    private EduCommentService eduCommentService;

    @Autowired
    private UcenterClient ucenterClient;

    //根据课程id_分页查询课程评论的方法
    @GetMapping("/getCommentPage/{page}/{limit}/{courseId}")
    public R getCommentPage(@PathVariable Long page,@PathVariable Long limit,@PathVariable(required = false) String courseId){
        Page<EduComment> commentPage = new Page<>(page, limit);

        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();

        //判断课程id是否为空
        if (!StringUtils.isEmpty(courseId)){
            wrapper.eq("course_id",courseId);
        }else{
            wrapper.eq("course_id","-1");
        }

        //按最新排序
        wrapper.orderByDesc("gmt_create");

        //数据会被封装到commentPage中
        eduCommentService.page(commentPage,wrapper);

        List<EduComment> commentList = commentPage.getRecords();
        long current = commentPage.getCurrent();//当前页
        long size = commentPage.getSize();//一页记录数
        long total = commentPage.getTotal();//总记录数
        long pages = commentPage.getPages();//总页数
        boolean hasPrevious = commentPage.hasPrevious();//是否有上页
        boolean hasNext = commentPage.hasNext();//是否有下页

        HashMap<String, Object> map = new HashMap<>();
        map.put("current",current);
        map.put("size",size);
        map.put("total",total);
        map.put("pages",pages);
        map.put("hasPrevious",hasPrevious);
        map.put("hasNext",hasNext);
        map.put("list",commentList);

        return R.ok().data(map);
    }


    //添加评论
    @PostMapping("/auth/addComment")
    public R addComment(HttpServletRequest request, @RequestBody EduComment eduComment){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //判断用户是否登录
        if (StringUtils.isEmpty(memberId)){
            throw new GuliException(28004,"Please login first");
        }
        eduComment.setMemberId(memberId);

        //远程调用ucenter根据用户id获取用户信息
        UcenterMemberOrder memberVo = ucenterClient.getUserInfoOrder(memberId);
//        System.out.println(memberVo);
        eduComment.setAvatar(memberVo.getAvatar());
        eduComment.setNickname(memberVo.getNickname());

        //保存评论
        eduCommentService.save(eduComment);

        return R.ok();
    }


//    ....Admin Site ....
    //pagination for course list
    @GetMapping("pageComment/{current}/{limit}")
    public R pageListComment(@PathVariable long current,
                             @PathVariable long limit){
        //instantiate page
        Page<EduComment> pageComment = new Page<>(current,limit);//current= current page, limit = number of entries per page. Import maven package

        //invoke method to achieve pagination
        //provide the pagination data into pageTeacher object
        eduCommentService.page(pageComment,null); //wrapper = regulation

        long total = pageComment.getTotal(); // total entries
        List<EduComment> records = pageComment.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //Query pagination with condition for course list
    @PostMapping("pageCommentCondition/{current}/{limit}")
    public R pageCommentCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) CommentQuery commentQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page object
        Page<EduComment> pageComment = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        //dynamic sql
        String content = commentQuery.getContent();
        String begin = commentQuery.getBegin();
        String end = commentQuery.getEnd();
        //start to build up the sql condition
        if(!StringUtils.isEmpty(content)){
            wrapper.like("content",content); //the first parameter is name of table in database
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
        eduCommentService.page(pageComment,wrapper);

        long total = pageComment.getTotal(); // total entries
        List<EduComment> records = pageComment.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //修改Comment信息
    @PostMapping("updateCommentInfo")
    public R updateCommentInfo(@RequestBody EduComment eduComment){
        boolean flag = eduCommentService.updateById(eduComment);
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //根据id查询banner
    @GetMapping("/getCommentById/{id}")
    public R getCommentById(@PathVariable String id){
        EduComment eduComment = eduCommentService.getById(id);
        return R.ok().data("comment",eduComment);
    }

    //根据id删除banner
    @DeleteMapping("/deleteCommentById/{id}")
    public R deleteCommentById(@PathVariable String id){
        boolean flag = eduCommentService.removeById(id);
        if (flag){
            return R.ok();
        }else {
            return R.error();
        }
    }
}

