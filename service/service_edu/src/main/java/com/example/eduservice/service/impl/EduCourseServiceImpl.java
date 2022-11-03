package com.example.eduservice.service.impl;

import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseDescription;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.frontvo.CourseFrontVo;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CoursePublishVo;
import com.example.eduservice.mapper.EduCourseMapper;
import com.example.eduservice.service.EduChapterService;
import com.example.eduservice.service.EduCourseDescriptionService;
import com.example.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduservice.service.EduVideoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-05-25
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;


    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private EduChapterService chapterService;

    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        //1.向课程表添加课程基本信息
        //convert courseInfoVo to eduCourse object
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int insert = baseMapper.insert(eduCourse); //baseMapper autowired the service bean

        if(insert <= 0){
            throw new GuliException(20001,"Add course failed");
        }

        //获得添加之后的课程id ，用于course description 的 foreign key
        String cid = eduCourse.getId();

        //2.向课程简介表添加课程简介
        //edu_course_description
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        //设置description id就是课程id
        courseDescription.setId(cid);
        courseDescriptionService.save(courseDescription); //not in its corresponding service, therefore, need to be autowired

        return cid;
    }

    //根据课程id做查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //1.查询course table
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse,courseInfoVo);

        //2.查询course description table
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());


        return courseInfoVo;
    }

    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //1.修改course table
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int update = baseMapper.updateById(eduCourse);
        if(update ==0){
            throw new GuliException(20001,"Update course failed");
        }

        //2.修改course description table
        EduCourseDescription description = new EduCourseDescription();
        description.setId(courseInfoVo.getId());
        description.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(description);
    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        //调用mapper,在xml内配置了,因为sql语句复杂
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);

        return publishCourseInfo;
    }

    //删除课程
    @Override
    public void removeCourse(String courseId) {
        //1.根据course id 删除 video
        eduVideoService.removeVideoByCourseId(courseId);
        //2.根据course id 删除 chapter
        chapterService.removeChapterByCourseId(courseId);
        //3.根据course id 删除 description
        courseDescriptionService.removeById(courseId); //因为是 one to one relationship 可以直接删
        //4.根据course id 删除 course本身
        int result = baseMapper.deleteById(courseId);
        if(result==0){
            throw new GuliException(20001,"Delete course failed!");
        }

    }

    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
        //查询讲师所讲课程信息
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())){ //first level
            wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())){ //second level
            wrapper.eq("subject_id",courseFrontVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())){ //popularity
            wrapper.orderByDesc("buy_count");
        }
        if(!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())){ //date
            wrapper.orderByDesc("gmt_create");
        }
        if(!StringUtils.isEmpty(courseFrontVo.getPriceSort())){ //price
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageParam,wrapper);

        List<EduCourse> records = pageParam.getRecords();
        //当前页
        long current = pageParam.getCurrent();
        //总页数
        long pages = pageParam.getPages();
        //每页记录数
        long size = pageParam.getSize();
        //总记录数
        long total = pageParam.getTotal();


        //是否有下一页
        boolean hasNext = pageParam.hasNext();
        //是否有上一页
        boolean hasPrevious = pageParam.hasPrevious();



        Map<String,Object> map = new HashMap<>();
        //将数据封装到map中返回
        map.put("items",records);
        map.put("current",current);
        map.put("pages",pages);
        map.put("size",size);
        map.put("total",total);
        map.put("hasNext",hasNext);
        map.put("hasPrevious",hasPrevious);

        return map;
    }

    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }

    @Override
    public List<CourseWebVo> getCollectCourseInfo(String memberId) {
        return baseMapper.getCollectCourseInfo(memberId);
    }

    @Override
    public List<CourseWebVo> getLikeCollectCourseInfo(String memberId, String content) {
        return baseMapper.getLikeCollectCourseInfo(memberId,content);
    }


}
