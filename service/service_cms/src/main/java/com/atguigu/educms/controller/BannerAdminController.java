package com.atguigu.educms.controller;


import com.atguigu.commonutils.R;
import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Backend admin control
 * </p>
 *
 * @author testjava
 * @since 2022-05-29
 */
@RestController
@RequestMapping("/educms/banneradmin")
//@CrossOrigin
public class BannerAdminController {
    @Autowired
    private CrmBannerService bannerService;
    //1. pagination for banner
    @GetMapping("pageBanner/{page}/{limit}")
    public R pageBanner(@PathVariable long page, @PathVariable long limit){
        Page<CrmBanner> pageBanner = new Page<>(page,limit);
        bannerService.page(pageBanner,null);
        return R.ok().data("rows",pageBanner.getRecords()).data("total",pageBanner.getTotal());
    }

    //Query pagination with condition for course list
    @PostMapping("pageBannerCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) CrmBanner bannerQuery){  //request body parameter can be null, it is only passed by post method, and it is JSON type data
        //instantiate page object
        Page<CrmBanner> pageCourse = new Page<>(current,limit);

        //query for pagination
        QueryWrapper<CrmBanner> wrapper = new QueryWrapper<>();
        //dynamic sql


        //sorting for frontend
        wrapper.orderByDesc("gmt_create");

        //invoke method to achieve pagination
        bannerService.page(pageCourse,wrapper);

        long total = pageCourse.getTotal(); // total entries
        List<CrmBanner> records = pageCourse.getRecords();// data stored in list collection
        return R.ok().data("total",total).data("rows",records); // put the data into map by two operation (chain prog)
    }

    //2. add banner
    @PostMapping("addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner){
        bannerService.save(crmBanner);
        return R.ok();
    }

    //修改banner
    @PostMapping("/updateBanner")
    public R updateBanner(@RequestBody CrmBanner crmBanner){
        boolean flag = bannerService.updateById(crmBanner);
        if (flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

    //根据id删除banner
    @DeleteMapping("/deleteBannerById/{id}")
    public R deleteBannerById(@PathVariable String id){
        boolean flag = bannerService.removeById(id);
        if (flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

    //根据id查询banner
    @GetMapping("/getBannerById/{id}")
    public R getBannerById(@PathVariable String id){
        CrmBanner crmBanner = bannerService.getById(id);
        return R.ok().data("banner",crmBanner);
    }

}

