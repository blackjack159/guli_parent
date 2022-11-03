package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.eduorder.client.EduClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.entity.vo.OrderQuery;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-08-10
 */
@RestController
@RequestMapping("/eduorder/order")
//@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EduClient eduClient;

    @GetMapping("/updateBuyCount/{courseId}")
    public R updateBuyCount(@PathVariable String courseId){
        boolean flag = eduClient.updateBuyCount(courseId);
        return (flag) ? R.ok() : R.error();
    }
    //1.generate order method
    @PostMapping("/createOrder/{courseId}")
    public R createOrder(@PathVariable String courseId, HttpServletRequest request){
        //从请求头中获取用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //判断是否登录
        if (StringUtils.isEmpty(memberId)){
            throw new GuliException(20001,"Please login");
        }

        //生成订单，并生成对应的订单号
        String orderNo = orderService.createOrders(courseId,memberId);

        return R.ok().data("orderId",orderNo);
    }

    //check order detail based on order id
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item",order);

    }

    //check order status based on course id and user id
    @GetMapping("isBuyCourse/{courseId}/{memberId}")
    public boolean isBuyCourse(@PathVariable String courseId, @PathVariable String memberId)
    {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        wrapper.eq("member_id",memberId);
        wrapper.eq("status",1); // 1= paid
        int count = orderService.count(wrapper);
        if(count>0){
            return true;
        }else{
            return false;
        }


    }

    //...Admin Site...
    //pagination for course list
    @GetMapping("pageOrder/{current}/{limit}")
    public R pageListOrder(@PathVariable long current,
                             @PathVariable long limit){
        //instantiate page
        Page<Order> pageOrder= new Page<>(current,limit);//current= current page, limit = number of entries per page. Import maven package

        //invoke method to achieve pagination
        //provide the pagination data into pageTeacher object
        orderService.page(pageOrder,null); //wrapper = regulation

        long total = pageOrder.getTotal(); // total entries
        List<Order> records = pageOrder.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //Query pagination with condition for course list
    @PostMapping("pageOrderCondition/{current}/{limit}")
    public R pageOrderCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) OrderQuery orderQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page object
        Page<Order> pageOrder = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        //dynamic sql
        String memberId = orderQuery.getMemberId();
        String status = orderQuery.getStatus();
        String begin = orderQuery.getBegin();
        String end = orderQuery.getEnd();
        //start to build up the sql condition
        if(!org.springframework.util.StringUtils.isEmpty(memberId)){
            wrapper.like("member_id",memberId); //the first parameter is name of table in database
        }
        if(!org.springframework.util.StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        if(!org.springframework.util.StringUtils.isEmpty(begin)){
            wrapper.ge("gmt_create",begin);
        }
        if(!org.springframework.util.StringUtils.isEmpty(end)){
            wrapper.le("gmt_create",end);
        }

        //sorting for frontend
        wrapper.orderByDesc("gmt_create");

        //invoke method to achieve pagination
        orderService.page(pageOrder,wrapper);

        long total = pageOrder.getTotal(); // total entries
        List<Order> records = pageOrder.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //修改Comment信息
    @PostMapping("updateOrderInfo")
    public R updateOrderInfo(@RequestBody Order order){
        boolean flag = orderService.updateById(order);
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //根据id查询banner
    @GetMapping("/getOrderById/{id}")
    public R getOrderById(@PathVariable String id){
        Order order = orderService.getById(id);
        return R.ok().data("order",order);
    }
}

