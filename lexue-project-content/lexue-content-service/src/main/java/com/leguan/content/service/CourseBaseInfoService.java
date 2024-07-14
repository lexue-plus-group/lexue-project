package com.leguan.content.service;

import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.content.model.dto.QueryCourseParamsDto;
import com.leguan.content.model.po.CourseBase;

/**
 * description 课程信息管理接口
 */
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     * @param pageParams 分页查询参数
     * @param courseParamsDto 查询条件
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);
}
