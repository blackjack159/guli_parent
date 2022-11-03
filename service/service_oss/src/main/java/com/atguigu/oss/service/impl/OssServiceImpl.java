package com.atguigu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtils;
import lombok.var;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {


    //upload avatar to local_storage
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        //工具类获取值
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;


        InputStream inputStream = null;


        try {
            // 创建OSS实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取上传文件的输入流
            inputStream = file.getInputStream();

            //获取文件名称
            String fileName = file.getOriginalFilename();

            //1.random number for file
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            //yuy76t5rew01.jpg
            fileName = uuid + fileName;

            //2.sort file using date (joda-time)
            //2019/11/12/01.jpg
            String datePath = new DateTime().toString("yyyy/MM/dd");

            //combine format
            fileName = datePath+"/"+fileName;

            //调用oss实例中的方法实现上传
            //参数1： Bucket名称
            //参数2： 上传到oss文件路径和文件名称 /aa/bb/1.jpg
            //参数3： 上传文件的输入流
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();

            //把上传后文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            //https://achang-edu.oss-cn-hangzhou.aliyuncs.com/default.gif
            String url = "http://"+bucketName+"."+endpoint+"/"+fileName ;

            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

//
//        //selecting the local-storage folder to save the files
//        String uploadDir = System.getProperty("user.dir") + "\\local_storage\\";
//
//        String fileName = theFile.getOriginalFilename();
//        String uuid = UUID.randomUUID().toString().replaceAll("-","");
//        fileName = uuid + fileName;
//
//        try {
//
//            //saving the file to the selected folder
//            Files.copy(theFile.getInputStream(), Paths.get(uploadDir + fileName), StandardCopyOption.REPLACE_EXISTING);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            //a friendly flag if the uploading fails
////            model.addAttribute("flag", "Upload Failed");
////            return new ModelAndView("index");
//            return "error";
//        }
//          return uploadDir + fileName;
////        model.addAttribute("flag", "Upload Successful, Check the 'local-storage' folder");
////        return new ModelAndView("index");
    }
}
