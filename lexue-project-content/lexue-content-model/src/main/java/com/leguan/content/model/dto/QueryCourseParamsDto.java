package com.leguan.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @description 课程查询条件参数类
 */

@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}
