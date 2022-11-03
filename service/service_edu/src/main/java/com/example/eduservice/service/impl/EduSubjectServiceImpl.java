package com.example.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.excel.SubjectData;
import com.example.eduservice.entity.subject.OneSubject;
import com.example.eduservice.entity.subject.TwoSubject;
import com.example.eduservice.listener.SubjectExcelListener;
import com.example.eduservice.mapper.EduSubjectMapper;
import com.example.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-05-25
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {


    @Override
    public void saveSubject(MultipartFile file,EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream is = file.getInputStream();

            //调用方法进行读取
            EasyExcel.read(is, SubjectData.class,new SubjectExcelListener(subjectService))
                    .sheet().doRead();

        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001,"Add subject classification failed");
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //1.find all first level, parent_id = 0
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id","0");

        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);//自动autowired 一个 eduSubjectService
        //2. find all second level, parent_id != 0
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperOne.ne("parent_id","0");

        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);//自动autowired 一个 eduSubjectService
        //3. encapsulate first level
        ArrayList<OneSubject> finalSubjectList = new ArrayList<>();
        //convert all the <EduSubject> (extracted from database) to <OneSubject>
        //tis is becuz we must follow the frontend data type required
        for (int i = 0; i < oneSubjectList.size() ; i++) {
            EduSubject eduSubject = oneSubjectList.get(i);

            OneSubject oneSubject = new OneSubject();
            //copy only two attribute into oneSubject
            BeanUtils.copyProperties(eduSubject,oneSubject);
//            oneSubject.setId(eduSubject.getId());
//            oneSubject.setTitle(eduSubject.getTitle());

            finalSubjectList.add(oneSubject);

            //在一级分类循环遍历查询所有的二级分类
            //创建list集合封装每个一级分类的二级分类
            ArrayList<TwoSubject> finalTwoSubjects = new ArrayList<>();

            //遍历二级list集合
            for (int j = 0; j < twoSubjectList.size(); j++) {
                //获取每个二级分类
                EduSubject tSubject = twoSubjectList.get(j);
                //判断二级分类parentid和一级分类id是否一样
                if (tSubject.getParentId().equals(eduSubject.getId())){
                    //把tSubject值复制到TwoSubject，最终放在twoSubjectsList中
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    finalTwoSubjects.add(twoSubject);
                }
            }

            //把一级下面所有二级分类放到oneSubject里面
            oneSubject.setChildren(finalTwoSubjects);

        }


        return finalSubjectList;
    }
}
