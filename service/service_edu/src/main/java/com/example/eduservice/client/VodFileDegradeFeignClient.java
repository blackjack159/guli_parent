package com.example.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodFileDegradeFeignClient implements VodClient {

    //fault tolerance method for hystrix
    //if method not invoked successfully, do this method
    @Override
    public R removeAlyVideo(String id) {
        return R.error().message("Delete video error happen!");
    }

    @Override
    public R deleteBatch(List<String> videoIdList) {
        return R.error().message("Delete several video error happen!");
    }
}
