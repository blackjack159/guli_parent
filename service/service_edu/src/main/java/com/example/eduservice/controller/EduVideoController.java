package com.example.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.example.eduservice.client.VodClient;
import com.example.eduservice.entity.EduChapter;
import com.example.eduservice.entity.EduVideo;
import com.example.eduservice.service.EduVideoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-05-25
 */
@RestController
@RequestMapping("/eduservice/video")
//@CrossOrigin
public class EduVideoController {

    @Autowired
    private EduVideoService videoService;

    @Autowired
    private VodClient vodClient;

    //添加小节
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        videoService.save(eduVideo);
        return R.ok();
    }

    //删除小结 TODO 删小节也要删视频
    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id){
        //get video source id from Mysql database
        EduVideo eduVideo = videoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();

        if(!StringUtils.isEmpty(videoSourceId)) {
            //break point
            R result = vodClient.removeAlyVideo(videoSourceId);
            if(result.getCode() == 20001){
                throw new GuliException(20001,"Delete video fail, hystrix fault tolerance triggered...");
            }

        }

        videoService.removeById(id);
        return R.ok();
    }

    //修改小节
    @GetMapping("getLessonInfo/{lessonId}")
    public R getLessonInfo(@PathVariable String lessonId){
        EduVideo video = videoService.getById(lessonId);
        return R.ok().data("video",video);
    }

    //update lesson
    @PutMapping("/updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo){
        EduVideo video = new EduVideo();
        BeanUtils.copyProperties(eduVideo,video);
        boolean updateResult = videoService.updateById(video);
        if (!updateResult){
            throw new GuliException(20001,"Update video fail");
        }
        return  R.ok();
    }
}

