package com.atguigu.educenter.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.entity.vo.RegisterVo;
import com.atguigu.educenter.entity.vo.UserQuery;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-06-05
 */
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UcenterMemberService memberService;

    //login
    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member){
        //invoke sevice method, the token is for OAUTH(SSO)
        String token = memberService.login(member);
        return R.ok().data("token",token);
    }

    //register as user
    @PostMapping("register")
    public R registerUser(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);
        return R.ok();
    }

    //get the user info via token
    @GetMapping("getMemberInfo")
    public R getMemberInfo(HttpServletRequest request){
        //invoke jwt utils, to get the header message, return with user id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //read the database to get user info via user id
        UcenterMember member = memberService.getById(memberId);
        return R.ok().data("userInfo",member);


    }


    //get user info via user id
    @PostMapping("getUserInfoOrder/{id}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable String id){
        UcenterMember member = memberService.getById(id);
        //copy the member to UcenterMemberOrder
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);

        return ucenterMemberOrder;
    }

    //check registration user per day
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = memberService.countRegisterDay(day);
        return R.ok().data("countRegister",count);
    }

    @GetMapping("resetPassword/{mobile}/{email}/{code}/{password}")
    public R resetPassword(@PathVariable String mobile,@PathVariable String email, @PathVariable String code, @PathVariable String password){
        if(StringUtils.isEmpty(mobile)){
            throw new GuliException(20001,"Wrong mobile given");
        }
        if(StringUtils.isEmpty(email)){
            throw new GuliException(20001,"Please enter email!");
        }
        if(StringUtils.isEmpty(code)){
            throw new GuliException(20001,"Please enter correct code!");
        }

        String redisCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(redisCode)){
            throw new GuliException(20001,"Incorrect code!");
        }

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember result = memberService.getOne(wrapper);
        result.setPassword(MD5.encrypt(password));
        boolean isUpdate = memberService.updateById(result);
        if(isUpdate){
            redisTemplate.delete(mobile);
            return R.ok();
        }else{
            redisTemplate.delete(mobile);
            throw new GuliException(20001,"Password Reset Failed!");
        }
    }

    //....Admin Site....

    //1. select all the entries in edu_teacher
    @GetMapping("findAll")
    public R findAllUser(){
        List<UcenterMember> list = memberService.list(null);
        return R.ok().data("items",list); //it's a map variable to store the data as JSON

    }

    //rest style to get the parameter
    //update user (ban/unbanned)
    @GetMapping("banUser/{memberId}/{isDisabled}") //can use put , but it will be complex
    public R updateUser(@PathVariable String memberId, @PathVariable Boolean isDisabled){
        UcenterMember member = new UcenterMember();
        member.setId(memberId);
        member.setIsDisabled(isDisabled? false: true);

        boolean flag = memberService.updateById(member); //use front end to obtain only id value
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }

    }

    //3. pagination for teacher entries
    @GetMapping("pageUser/{current}/{limit}")
    public R pageListUser(@PathVariable long current,
                             @PathVariable long limit){
        //instantiate page
        Page<UcenterMember> pageUser = new Page<>(current,limit);//current= current page, limit = number of entries per page. Import maven package

        //invoke method to achieve pagination
        //provide the pagination data into pageUser object
        memberService.page(pageUser,null); //wrapper = regulation

        long total = pageUser.getTotal(); // total entries
        List<UcenterMember> records = pageUser.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //4. Query pagination with conditions
    @PostMapping("pageUserCondition/{current}/{limit}")
    public R pageUserCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) UserQuery userQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page object
        Page<UcenterMember> pageUser = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        //dynamic sql
        String nickname = userQuery.getNickname();
//        String mobile = userQuery.getMobile();
        Boolean disable = userQuery.getIsDisabled();
//        String id = userQuery.getId();
        //start to build up the sql condition
        if(!StringUtils.isEmpty(nickname)){
            wrapper.like("nickname",nickname); //the first parameter is name of table in database
        }
        if(!StringUtils.isEmpty(disable)){
            wrapper.eq("is_disabled",disable);
        }
//        if(!StringUtils.isEmpty(id)){
//            wrapper.ge("id",id);
//        }


        //sorting for frontend
        wrapper.orderByDesc("gmt_create");

        //invoke method to achieve pagination
        memberService.page(pageUser,wrapper);
        long total = pageUser.getTotal(); // total entries
        List<UcenterMember> records = pageUser.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }
}

