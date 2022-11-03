package com.example.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.excel.SubjectData;
import com.example.eduservice.service.EduSubjectService;


public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
//在EduSubjectServiceImpl 的saveSubject中用了这个listener
    public EduSubjectService subjectService;

    public SubjectExcelListener(){}
    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //read one by one row
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null){
            throw new GuliException(20001,"The excel is empty");
        }

        //read line by line, and each read got two value which is first level and second level
        //if first level has repeated
        EduSubject existOneSubject = this.existOneSubject(subjectService, subjectData.getOneSubjectName());
        if (existOneSubject == null){ //no identical first level, just add
            existOneSubject = new EduSubject();
            existOneSubject.setParentId("0"); //设置first level's id，0 indicates first level
            existOneSubject.setTitle(subjectData.getOneSubjectName());
            subjectService.save(existOneSubject);//add to database
        }

        //get first level id
        String parent_id = existOneSubject.getId();
        //if second level no repeat
        EduSubject existTwoSubject = this.existTwoSubject(subjectService, subjectData.getTwoSubjectName(), parent_id);
        if (existTwoSubject==null){//no identical second level, just add
            existTwoSubject = new EduSubject();
            existTwoSubject.setParentId(parent_id); //set second level id
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());//set second level name
            subjectService.save(existTwoSubject);//add to database
        }

    }

    private EduSubject existOneSubject(EduSubjectService subjectService, String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    private EduSubject existTwoSubject(EduSubjectService subjectService, String name, String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }
    //read header
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
