package com.leguan.content.service;

import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.content.model.dto.AddCourseDto;
import com.leguan.content.model.dto.CourseBaseInfoDto;
import com.leguan.content.model.dto.EditCourseDto;
import com.leguan.content.model.dto.QueryCourseParamsDto;
import com.leguan.content.model.po.CourseBase;
import com.leguan.content.model.po.CourseMarket;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description 课程信息管理接口
 */
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     * @param pageParams 分页查询参数
     * @param courseParamsDto 查询条件
     * @param companyId 机构id
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);


    /**
     * 查看完整的课程信息包括课程营销信息
     * 根据课程id查询课程信息
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 插入或更新课程营销信息
     */
    public int saveCourseMarket(CourseMarket courseMarketNew);

    /**
     * 修改课程
     * @param companyId 机构id
     * @param editCourseDto 修改课程信息
     * @return 课程详细信息
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    public void deleteCourseBase(Long companyId, Long courseId);
}
