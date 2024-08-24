package com.leguan.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leguan.base.exception.LexueException;
import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.content.mapper.CourseBaseMapper;
import com.leguan.content.mapper.CourseCategoryMapper;
import com.leguan.content.mapper.CourseMarketMapper;
import com.leguan.content.model.dto.AddCourseDto;
import com.leguan.content.model.dto.CourseBaseInfoDto;
import com.leguan.content.model.dto.EditCourseDto;
import com.leguan.content.model.dto.QueryCourseParamsDto;
import com.leguan.content.model.po.*;
import com.leguan.content.service.CourseBaseInfoService;
import com.leguan.content.service.CourseTeacherService;
import com.leguan.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseTeacherService courseTeacherService;

    @Autowired
    TeachPlanService teachPlanService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        //分页查询
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        //根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        //根据发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());
        //根据培训机构id拼装查询条件
        queryWrapper.eq(CourseBase::getCompanyId, companyId);

        //分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //根据接口的返回参数编写合适的返回类型
        List<CourseBase> items = pageResult.getRecords();
        long counts = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, counts, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //参数合法性校验
        /*if (StringUtils.isBlank(dto.getName())) {
            //throw new RuntimeException("课程名称为空");
            LexueException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }*/

        //向课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto, courseBaseNew);//只要属性名称一致就能复制
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        courseBaseNew.setAuditStatus("202002");//审核状态为未提交
        courseBaseNew.setStatus("203001");//设置发布状态为未发布
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            throw new RuntimeException("添加课程基本信息失败");
        }

        //向课程营销表course_market写入数据
        CourseMarket courseMarketNew = new CourseMarket();
        //将页面传过来的数据复制给courseMarketNew
        BeanUtils.copyProperties(dto, courseMarketNew);
        //两表一对一联系，由相同的主键id连接起来，获取该id
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        //保存营销信息
        int i = saveCourseMarket(courseMarketNew);
        if (i <= 0) {
            throw new RuntimeException("保存课程营销信息失败");
        }

        return getCourseBaseInfo(courseId);
    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }

    //保存课程营销信息
    public int saveCourseMarket(CourseMarket courseMarketNew) {
        //收费规则
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                //throw new RuntimeException("课程为收费价格不能为空且必须大于0");
                LexueException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if (courseMarketObj == null) {
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            BeanUtils.copyProperties(courseMarketNew, courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            LexueException.cast("课程不存在");
        }

        //数据合法性校验
        if (!companyId.equals(courseBase.getCompanyId())) {
            LexueException.cast("本机构只能修改本机构的课程");
        }

        //封装数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0) {
            LexueException.cast("修改课程失败");
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        int j = saveCourseMarket(courseMarket);
        if (j <= 0 ) {
            LexueException.cast("保存课程营销信息失败");
        }
        return getCourseBaseInfo(courseId);
    }

    @Transactional
    @Override
    public void deleteCourseBase(Long companyId, Long courseId) {
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBase::getId, courseId);
        CourseBase courseBase = courseBaseMapper.selectOne(wrapper);
        if (!companyId.equals(courseBase.getCompanyId())) {
            LexueException.cast("本机构只能删除本机构的课程");
        }
        int i = courseBaseMapper.deleteById(courseId);
        if (i <= 0) {
            LexueException.cast("删除课程失败");
        }
        int j = courseMarketMapper.deleteById(courseId);
        if (j <= 0) {
            LexueException.cast("删除课程失败");
        }

        List<Teachplan> teachPlanList = teachPlanService.getTeachPlanList(courseId);
        teachPlanList.stream().forEach(item -> teachPlanService.deleteTeachPlan(item.getId()));

        List<TeachplanMedia> teachPlanMediaList = teachPlanService.getTeachPlanMediaList(courseId);
        teachPlanMediaList.stream().forEach(item -> teachPlanService.deleteTeachPlanMedia(item.getCourseId()));

        List<CourseTeacher> courseTeachers = courseTeacherService.getCourseTeacherList(courseId);
        courseTeachers.stream().forEach(item -> courseTeacherService.deleteTeacher(courseId, item.getId()));

    }

}
