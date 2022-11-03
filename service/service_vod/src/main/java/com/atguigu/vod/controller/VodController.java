package com.atguigu.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.Utils.ConstantVodUtils;
import com.atguigu.vod.Utils.InitVodClient;
import com.atguigu.vod.service.VodService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eduvod/video")
//@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;

    @PostMapping("uploadAliyunVideo")
    public R uploadAlyiVideo(MultipartFile file){
        String videoId = vodService.uploadVideoAly(file);
        return R.ok().data("videoId",videoId);
    }

    @ApiOperation("根据视频id删除阿里云视频")
    @DeleteMapping("/removeAliyunVideo/{id}")
    public R removeAliyunVideo(@PathVariable String id){
        //初始化阿里云客户端对象
        DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);




        //创建删除视频的request对象
        DeleteVideoRequest deleteVideoRequest = new DeleteVideoRequest();
        deleteVideoRequest.setVideoIds(id);
        try {
            client.getAcsResponse(deleteVideoRequest);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"Delete Video Failed");
        }

    }

    //delete several video
    @DeleteMapping("delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList){
        vodService.removeMoreAlyVideo(videoIdList);

        return R.ok();
    }

    @ApiOperation("根据视频id获取视频凭证")
    @GetMapping("/getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id ){
        try {
        //创建初始化对象
        DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);

        //创建获取凭证的request和response对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();

        //向authRequest设置视频id
        request.setVideoId(id);

        //调用方法得到凭证
        GetVideoPlayAuthResponse response = null;

            response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();

            return R.ok().data("playAuth",playAuth);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"Get play auth failed");
        }

    }
}
